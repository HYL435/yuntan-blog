package com.yuntan.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuntan.article.domain.dto.admin.TagDTO;
import com.yuntan.article.domain.dto.admin.TagUpdateDTO;
import com.yuntan.article.domain.po.Tag;
import com.yuntan.article.domain.vo.admin.TagContentVO;
import com.yuntan.article.domain.vo.admin.TagVO;
import com.yuntan.article.mapper.TagMapper;
import com.yuntan.article.service.ITagService;
import com.yuntan.common.constant.StatusConstant;
import com.yuntan.common.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements ITagService {

    private final TagMapper tagMapper;

    /**
     * 获取所有标签名称
     */
    @Override
    public List<String> getAllTagNames() {

        return tagMapper.getTags();
    }

    /**
     * 获取所有标签名称管理端
     */
    @Override
    public List<TagVO> getAdminTag() {

        List<Tag> tags = this.list();

        return BeanUtils.copyList(tags, TagVO.class);

    }

    /**
     * 添加标签
     */
    @Override
    public void addTag(TagDTO tagDTO) {

        Tag tag = BeanUtils.copyBean(tagDTO, Tag.class);

        this.save(tag);

    }

    /**
     * 修改标签
     */
    @Override
    public void updateTag(TagUpdateDTO tagUpdateDTO) {

        Tag tag = BeanUtils.copyBean(tagUpdateDTO, Tag.class);

        this.updateById(tag);

    }

    /**
     * 根据id获取标签内容
     */
    @Override
    public TagContentVO getTagById(Long id) {

        Tag tag = this.getById(id);

        return BeanUtils.copyBean(tag, TagContentVO.class);
    }

    /**
     * 修改标签状态
     */
    @Override
    public void changeTagStatus(Long id, Integer status) {

        status = Objects.equals(status, StatusConstant.ENABLE) ? StatusConstant.DISABLE : StatusConstant.ENABLE;

        this.lambdaUpdate()
                .eq(Tag::getId, id)
                .set(Tag::getStatus, status)
                .update();
    }
}
