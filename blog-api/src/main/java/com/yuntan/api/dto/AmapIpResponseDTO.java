package com.yuntan.api.dto;

import lombok.Data;

@Data
public class AmapIpResponseDTO {
    /**
     * 返回结果状态值：1 表示成功，0 表示失败
     */
    private String status;

    /**
     * 返回状态说明：OK 代表成功
     */
    private String info;

    /**
     * 状态码
     */
    private String infocode;

    /**
     * 省份名称
     */
    private String province;

    /**
     * 城市名称
     * 注意：如果是直辖市，province和city一样；如果是局域网IP，这里可能为空
     */
    private String city;

    /**
     * 区域编码
     */
    private String adcode;

    /**
     * 所在城市矩形区域范围
     */
    private String rectangle;
}