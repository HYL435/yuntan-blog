package com.yuntan.comment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuntan.api.client.AmapFeignClient;
import com.yuntan.api.client.ArticleClient;
import com.yuntan.api.client.UserClient;
import com.yuntan.api.dto.AmapIpResponseDTO;
import com.yuntan.api.dto.ArticleInfoDTO;
import com.yuntan.api.dto.UserCommentDTO;
import com.yuntan.comment.domain.dto.admin.CommentStatusDTO;
import com.yuntan.comment.domain.dto.front.CommentDTO;
import com.yuntan.comment.domain.po.Comment;
import com.yuntan.comment.domain.query.CommentQuery;
import com.yuntan.comment.domain.vo.admin.CommentAdminVO;
import com.yuntan.comment.domain.vo.front.CommentChildVO;
import com.yuntan.comment.domain.vo.front.CommentVO;
import com.yuntan.comment.mapper.CommentMapper;
import com.yuntan.comment.service.ICommentService;
import com.yuntan.comment.utils.CommentOssUtil;
import com.yuntan.common.constant.MessageConstant;
import com.yuntan.common.context.BaseContext;
import com.yuntan.common.domain.PageDTO;
import com.yuntan.common.domain.PageQuery;
import com.yuntan.common.domain.Result;
import com.yuntan.common.exception.BusinessException;
import com.yuntan.common.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

    public final CommentMapper commentMapper;

    public final CommentOssUtil commentOssUtil;

    public final UserClient userClient;
    public final AmapFeignClient amapFeignClient;
    public final ArticleClient articleClient;
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
            // 只有当文件不为 null 且不为空时，才执行上传
            if (commentDTO.getImageFile() != null && !commentDTO.getImageFile().isEmpty()) {
                String image = commentOssUtil.uploadFile(commentDTO.getImageFile(), "comment");
                comment.setImage(image);
            }
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
    public List<CommentVO> listComments(Long articleId) {

        // ==========================================
        // 第一步：查询根评论 (ParentId 为空)
        // ==========================================
        LambdaQueryWrapper<Comment> rootWrapper = new LambdaQueryWrapper<>();
        rootWrapper.eq(Comment::getArticleId, articleId)
                .isNull(Comment::getParentId)
                .eq(Comment::getStatus, 1) // 假设 1 是发布状态
                .orderByDesc(Comment::getCreateTime);

        List<Comment> rootComments = commentMapper.selectList(rootWrapper);

        // 【优化】如果没有根评论，直接返回，避免后续无效操作
        if (rootComments == null || rootComments.isEmpty()) {
            return Collections.emptyList();
        }

        // ==========================================
        // 第二步：查询子评论 (ParentId 在根评论ID列表中)
        // ==========================================
        List<Long> rootIds = rootComments.stream().map(Comment::getId).collect(Collectors.toList());

        List<Comment> childComments = new ArrayList<>();
        // MyBatis-Plus 的 in 查询不支持空集合，必须判空
        if (!rootIds.isEmpty()) {
            LambdaQueryWrapper<Comment> childWrapper = new LambdaQueryWrapper<>();
            childWrapper.in(Comment::getParentId, rootIds)
                    .eq(Comment::getStatus, 1)
                    .orderByAsc(Comment::getCreateTime);
            childComments = commentMapper.selectList(childWrapper);
        }

        // ==========================================
        // 第三步：收集所有需要查询的用户 ID (批量查询，性能优化)
        // ==========================================
        Set<Long> userIds = new HashSet<>();

        // 3.1 收集根评论作者
        rootComments.forEach(c -> userIds.add(c.getUserId()));

        // 3.2 收集子评论作者 和 被回复的人(toUserId)
        childComments.forEach(c -> {
            userIds.add(c.getUserId());
            if (c.getToUserId() != null) {
                userIds.add(c.getToUserId());
            }
        });

        // ==========================================
        // 第四步：远程调用 User 服务获取用户信息
        // ==========================================
        // 定义 Map 存储用户信息：Key=UserId, Value=UserDTO
        Map<Long, UserCommentDTO> userMap = new HashMap<>();

        // 远程调用获取用户信息后，填充 userMap
        remoteCallUserInfo(userIds, userMap);

        // ==========================================
        // 第五步：组装数据 (Entity -> VO)
        // ==========================================

        // 5.1 将子评论按 ParentId 分组 (Key=根评论ID, Value=该根评论下的子评论列表)
        Map<Long, List<Comment>> childMap = childComments.stream()
                .collect(Collectors.groupingBy(Comment::getParentId));

        // 5.2 转换根评论 VO
        // 注意：这里需要你有一个 CommentVO 类，结构应该和 CommentChildVO 类似，但多一个 children 字段
        List<CommentVO> resultList = BeanUtils.copyList(rootComments, CommentVO.class);

        // 5.3 遍历根评论，填充信息
        for (CommentVO rootVO : resultList) {
            // --- A. 填充根评论作者信息 ---
            UserCommentDTO rootUser = userMap.get(rootVO.getUserId());
            if (rootUser != null) {
                rootVO.setNickname(rootUser.getNickname());
                // 根据你的 UserCommentDTO 字段名调整，假设远程返回的是 image，VO里是 userImg
                rootVO.setUserImg(rootUser.getImage());
            } else {
                rootVO.setNickname("未知用户");
                rootVO.setUserImg(""); // 设置默认头像 URL
            }

            // --- B. 处理 IP ---
            rootVO.setIp(getCityByIp(rootVO.getIp()));

            // --- C. 处理子评论 ---
            List<Comment> myChildren = childMap.get(rootVO.getId());
            if (myChildren != null && !myChildren.isEmpty()) {
                // Entity -> ChildVO
                List<CommentChildVO> childVOs = BeanUtils.copyList(myChildren, CommentChildVO.class);

                // 遍历子评论，填充信息
                for (CommentChildVO childVO : childVOs) {
                    // C.1 填充子评论作者
                    UserCommentDTO childUser = userMap.get(childVO.getUserId());
                    if (childUser != null) {
                        childVO.setNickname(childUser.getNickname());
                        childVO.setUserImg(childUser.getImage());
                    } else {
                        childVO.setNickname("未知用户");
                    }

                    // C.2 填充“回复给谁” (toUserName)
                    // 假设 Comment 实体里有 getToUserId() 方法
                    // 我们需要找到对应的 Entity 来获取 toUserId，或者 VO 转换时已经拷过去了
                    // 假设 BeanUtils.copyList 已经把 toUserId 拷贝到了 childVO 中 (如果 childVO 有这个字段)
                    // 如果 childVO 没有 toUserId 字段，我们需要从 myChildren 里找，这里假设 VO 里没 toUserId 但有 toUserName

                    // 为了准确，这里我们用 Entity 的数据来找
                    Comment sourceEntity = myChildren.stream().filter(c -> c.getId().equals(childVO.getId())).findFirst().orElse(null);
                    if (sourceEntity != null && sourceEntity.getToUserId() != null) {
                        UserCommentDTO toUser = userMap.get(sourceEntity.getToUserId());
                        if (toUser != null) {
                            childVO.setToUserName(toUser.getNickname());
                        } else {
                            childVO.setToUserName("未知用户");
                        }
                    }

                    // C.3 处理子评论 IP
                    childVO.setIp(getCityByIp(childVO.getIp()));
                }

                rootVO.setChildren(childVOs);
            } else {
                rootVO.setChildren(Collections.emptyList());
            }
        }

        return resultList;
    }

    /**
     * 获取评论数量
     */
    @Override
    public Integer countComments(Long articleId) {

        return commentMapper.countComments(articleId);
    }

    /**
     * 后台分页查询评论列表
     */
    @Override
    public PageDTO<CommentAdminVO> listCommentsAdmin(CommentQuery pageQuery) {

        // 获取page对象
        Page<Comment> mpPage = pageQuery.toMpPage();

        // 设置查询条件
        LambdaQueryChainWrapper<Comment> queryWrapper = new LambdaQueryChainWrapper<>(commentMapper)
                .orderByDesc(Comment::getCreateTime);

        // 执行分页查询
        Page<Comment> commentPage = queryWrapper.page(mpPage);

        // 转换为VO对象
        PageDTO<CommentAdminVO> commentAdminVOPageDTO = PageDTO.of(commentPage, CommentAdminVO.class);

        // 获取用户ID列表
        Set<Long> userIds = commentAdminVOPageDTO
                .getList()
                .stream()
                .map(CommentAdminVO::getUserId)
                .collect(Collectors.toSet());

        Map<Long, UserCommentDTO> userMap = new HashMap<>();

        // 远程调用获取用户信息后，填充 userMap
        remoteCallUserInfo(userIds, userMap);

        // 填充VO对象的用户信息
        commentAdminVOPageDTO.getList().forEach(vo -> fillUserInfo(vo, userMap));

        // 远程调用 Article 服务获取文章标题后，填充VO对象
        commentAdminVOPageDTO.getList().forEach(vo -> {
            try {
                Result<ArticleInfoDTO> result = articleClient.getArticleInfoById(vo.getArticleId());
                if (result != null && result.getCode() == 200 && result.getData() != null) {
                    vo.setTitle(result.getData().getTitle());
                }
            } catch (Exception e) {
                log.error("远程获取文章信息失败", e);
                // 捕获异常不抛出，保证评论能正常展示（只是没标题而已）
            }
        });

        // 根据查询条件过滤（如果有）
        // 这里可以根据 pageQuery 中的其他字段进行过滤，比如状态、时间范围
        // 例如：
        if (pageQuery.getStatus() != null) {
            commentAdminVOPageDTO.setList(commentAdminVOPageDTO.getList().stream()
                    .filter(vo -> vo.getStatus().equals(pageQuery.getStatus()))
                    .collect(Collectors.toList()));
        }
        if (pageQuery.getTitle() != null) {
            commentAdminVOPageDTO.setList(commentAdminVOPageDTO.getList().stream()
                    .filter(vo -> vo.getTitle() != null && vo.getTitle().contains(pageQuery.getTitle()))
                    .collect(Collectors.toList()));
        }
        if (pageQuery.getNickname() != null) {
            commentAdminVOPageDTO.setList(commentAdminVOPageDTO.getList().stream()
                    .filter(vo -> vo.getNickname() != null && vo.getNickname().contains(pageQuery.getNickname()))
                    .collect(Collectors.toList()));
        }

        return commentAdminVOPageDTO;
    }

    // 远程调用获取用户信息后，填充VO对象
    public void remoteCallUserInfo(Set<Long> userIds, Map<Long, UserCommentDTO> userMap) {
        if (!userIds.isEmpty()) {
            try {
                // 远程调用
                Result<List<UserCommentDTO>> result = userClient.getUserComments(userIds);

                // 【安全】判空处理，防止远程服务挂了导致空指针
                if (result != null && result.getCode() == 200 && result.getData() != null) {
                    // 将 List 转为 Map，方便后续快速查找
                    Map<Long, UserCommentDTO> remoteMap = result.getData().stream()
                            .collect(Collectors.toMap(UserCommentDTO::getUserId, dto -> dto, (k1, k2) -> k1));
                    userMap.putAll(remoteMap);
                }
            } catch (Exception e) {
                log.error("远程获取评论用户信息失败", e);
                // 捕获异常不抛出，保证评论能正常展示（只是没头像而已）
            }
        }
    }

    /**
     * 修改评论状态
     */
    @Override
    public void updateStatusById(CommentStatusDTO commentStatusDTO) {

        Comment comment = BeanUtils.copyBean(commentStatusDTO, Comment.class);

        commentMapper.updateById(comment);
    }

    // 抽取一个填充用户信息的小方法，避免重复代码
    // 假设 CommentVO 和 CommentChildVO 都继承自一个 BaseCommentVO 或者都有这些字段
    // 这里用反射或者手动设置演示，你根据实际类结构调整
    private void fillUserInfo(Object vo, Map<Long, UserCommentDTO> userMap) {
        // 简单演示，实际请根据你的 VO 类结构调用
        if (vo instanceof CommentVO) {
            CommentVO c = (CommentVO) vo;
            UserCommentDTO u = userMap.get(c.getUserId());
            if (u != null) {
                c.setNickname(u.getNickname());
                c.setUserImg(u.getImage());
            }
        } else if (vo instanceof CommentChildVO) {
            CommentChildVO c = (CommentChildVO) vo;
            UserCommentDTO u = userMap.get(c.getUserId());
            if (u != null) {
                c.setNickname(u.getNickname());
                c.setUserImg(u.getImage());
            }
            // 如果有回复目标
            // UserCommentDTO toUser = userMap.get(c.getToUserId());
            // ...
        } else  if (vo instanceof CommentAdminVO) {
            CommentAdminVO c = (CommentAdminVO) vo;
            UserCommentDTO u = userMap.get(c.getUserId());
            if (u != null) {
                c.setNickname(u.getNickname());
            }
        }
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
