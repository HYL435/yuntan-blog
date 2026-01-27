package com.yuntan.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuntan.article.domain.po.Tag;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 根据文章ID获取文章标签
     */
    @Select("select t.tag_name from tag t join article_tag at on t.id = at.tag_id where at.article_id = #{articleId}")
    List<String> getTagsByArticleId(Long articleId);
}
