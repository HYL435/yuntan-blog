package com.yuntan.comment.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuntan.api.client.AmapFeignClient;
import com.yuntan.api.client.UserClient;
import com.yuntan.api.dto.AmapIpResponseDTO;
import com.yuntan.api.dto.UserCommentDTO;
import com.yuntan.comment.domain.dto.front.CommentDTO;
import com.yuntan.comment.domain.po.Comment;
import com.yuntan.comment.domain.vo.front.CommentChildVO;
import com.yuntan.comment.domain.vo.front.CommentVO;
import com.yuntan.comment.mapper.CommentMapper;
import com.yuntan.comment.service.ICommentService;
import com.yuntan.comment.utils.CommentOssUtil;
import com.yuntan.common.constant.MessageConstant;
import com.yuntan.common.context.BaseContext;
import com.yuntan.common.domain.Result;
import com.yuntan.common.exception.BusinessException;
import com.yuntan.common.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

    public final CommentMapper commentMapper;

    public final CommentOssUtil commentOssUtil;

    public final UserClient userClient;
    public final AmapFeignClient amapFeignClient;
    // 读取配置文件中的 Key
    @Value("${amap.key}")
    private String amapKey;


    /**
     * 保存评论
     */
    @Override
    public void saveComment(CommentDTO commentDTO) {

        // 转换实体类
        Comment comment = BeanUtils.copyBean(commentDTO, Comment.class);

        // 填充用户Id和IP地址
        comment.setUserId(BaseContext.getUserId());
        comment.setIp(BaseContext.getRequest().getRemoteAddr());

        // 上传图片
        try {
            String image = commentOssUtil.uploadFile(commentDTO.getImageFile(), "comment");
            comment.setImage(image);
        } catch (IOException e) {
            throw new BusinessException(MessageConstant.UPLOAD_FAILED);
        }

        // 存入数据库
        this.save(comment);
    }

    /**
     * 获取评论列表
     */
    @Override
    @Transactional
    public List<CommentVO> listComments(Long articleId) {

        // 根据文章ID查询根评论
        LambdaQueryChainWrapper<Comment> query = new LambdaQueryChainWrapper<>(commentMapper);
        query.eq(Comment::getArticleId, articleId)
                .eq(Comment::getParentId, null)
                .eq(Comment::getStatus, 1)
                .orderByDesc(Comment::getCreateTime);
        List<Comment> comments = query.list();

        // 转换评论实体类
        List<CommentVO> commentVOS = BeanUtils.copyList(comments, CommentVO.class);

        // 遍历评论，添加子评论
        commentVOS.forEach(commentVO -> {
            // 获取用户信息，并加入到评论信息中
            Result<UserCommentDTO> userComment = userClient.getUserComment(commentVO.getUserId());
            commentVO.setNickname(userComment.getData().getNickname());
            commentVO.setUserImg(userComment.getData().getImage());
            // 将IP替换为具体的地址
            commentVO.setIp(getCityByIp(commentVO.getIp()));
            // 获取子评论
            List<Comment> childComments = commentMapper.selectByParentId(commentVO.getId());
            // 转换子评论实体类
            List<CommentChildVO> commentChildVOS = BeanUtils.copyList(childComments, CommentChildVO.class);
            commentChildVOS.forEach(commentChildVO -> {
                // 获取用户信息，并加入到评论信息中
                Result<UserCommentDTO> userChildComment = userClient.getUserComment(commentChildVO.getUserId());
                commentChildVO.setNickname(userChildComment.getData().getNickname());
                commentChildVO.setUserImg(userChildComment.getData().getImage());
                // 将IP替换为具体的地址
                commentChildVO.setIp(getCityByIp(commentChildVO.getIp()));
            });
            // 设置子评论
            commentVO.setChildren(commentChildVOS);
        });

        return commentVOS;
    }

    public String getCityByIp(String ip) {
        // 本地调试 IP 处理
        if ("127.0.0.1".equals(ip) || "localhost".equals(ip)) {
            return "本地";
        }

        try {
            // 3. 像调用本地方法一样调用远程接口
            AmapIpResponseDTO result = amapFeignClient.getLocationByIp(amapKey, ip);

            if (result != null && "1".equals(result.getStatus())) {
                String city = result.getCity();
                // 高德特性：如果是直辖市或局域网，city可能为空数组字符串 "[]" 或 String类型的 ""
                if (city == null || city.isEmpty() || "[]".equals(city)) {
                    return result.getProvince();
                }
                return city;
            }
        } catch (Exception e) {
            log.error("IP定位失败: {}", e);
        }
        return "未知";
    }
}
