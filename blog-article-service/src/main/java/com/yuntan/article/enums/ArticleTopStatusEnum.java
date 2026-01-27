package com.yuntan.article.enums;

import lombok.Getter;

/**
 * 文章置顶状态枚举
 */
@Getter
public enum ArticleTopStatusEnum {
    
    /**
     * 不置顶
     */
    NOT_TOP(0, "不置顶"),
    
    /**
     * 置顶
     */
    TOP(1, "置顶");
    
    private final Integer value;
    
    private final String description;
    
    ArticleTopStatusEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }
    
    /**
     * 根据值获取枚举
     *
     * @param value 值
     * @return 枚举
     */
    public static ArticleTopStatusEnum getByValue(Integer value) {
        for (ArticleTopStatusEnum status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
}
