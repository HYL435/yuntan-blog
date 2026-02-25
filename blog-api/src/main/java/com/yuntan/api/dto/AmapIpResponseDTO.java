package com.yuntan.api.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class AmapIpResponseDTO implements Serializable {
    private String status;
    private String info;
    private String infocode;

    // --- 【关键修改】以下4个字段全部改为 Object ---

    /** 省份 (可能返回String或[]) */
    private Object province;

    /** 城市 (可能返回String或[]) */
    private Object city;

    /** 区域编码 (可能返回String或[]) -> 这次报错的就是它！ */
    private Object adcode;

    /** 矩形区域 (可能返回String或[]) -> 预防它下次报错 */
    private Object rectangle;

    // --- 手动 Getter，保证业务层拿到的是 String ---

    public String getProvince() {
        return parseString(province);
    }

    public String getCity() {
        return parseString(city);
    }

    public String getAdcode() {
        return parseString(adcode);
    }

    public String getRectangle() {
        return parseString(rectangle);
    }

    // 通用解析方法：是字符串就返回，是数组就返回空
    private String parseString(Object obj) {
        if (obj instanceof String) {
            return (String) obj;
        }
        return "";
    }
}