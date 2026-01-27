package com.yuntan.article.enums;

import lombok.Getter;

/**
 * 文章状态枚举
 */
@Getter
public enum ArticleStatusEnum {
    
    /**
     * 草稿
     */
    DRAFT(0, "草稿"),
    
    /**
     * 已发布
     */
    PUBLISHED(1, "已发布"),
    
    /**
     * 私密
     */
    PRIVATE(2, "私密");
    
    private final Integer value;
    
    private final String description;
    
    ArticleStatusEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }
    
    /**
     * 根据值获取枚举
     *
     * @param value 值
     * @return 枚举
     */
    public static ArticleStatusEnum getByValue(Integer value) {
        for (ArticleStatusEnum status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
}
