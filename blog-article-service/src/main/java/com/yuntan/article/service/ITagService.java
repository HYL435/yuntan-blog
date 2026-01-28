package com.yuntan.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuntan.article.domain.dto.admin.TagDTO;
import com.yuntan.article.domain.dto.admin.TagUpdateDTO;
import com.yuntan.article.domain.po.Tag;
import com.yuntan.article.domain.vo.admin.TagContentVO;
import com.yuntan.article.domain.vo.admin.TagVO;

import java.util.List;

public interface ITagService extends IService<Tag> {

    /**
     * 获取所有标签名称
     */
    List<String> getAllTagNames();

    /**
     * 获取所有标签
     */
    List<TagVO> getAdminTag();

    /**
     * 添加标签
     */
    void addTag(TagDTO tagDTO);

    /**
     * 修改标签
     */
    void updateTag(TagUpdateDTO tagUpdateDTO);

    /**
     * 根据id获取标签内容
     */
    TagContentVO getTagById(Long id);

    /**
     * 修改标签状态
     */
    void changeTagStatus(Long id, Integer status);
}
