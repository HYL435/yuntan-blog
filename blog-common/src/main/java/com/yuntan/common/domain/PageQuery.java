package com.yuntan.common.domain;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Accessors(chain = true)
@Schema(description = "分页查询条件")
public class PageQuery {

    public static final Integer DEFAULT_PAGE_SIZE = 20;
    public static final Integer DEFAULT_PAGE_NUM = 1;

    // 防止SQL注入的正则：只允许字母、数字、下划线
    private static final String SQL_FILTER_REGEX = "[^a-zA-Z0-9_]";

    @Schema(description = "页码", example = "1")
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNo = DEFAULT_PAGE_NUM;

    @Schema(description = "每页数量", example = "20")
    @Min(value = 1, message = "每页查询数量不能小于1")
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    @Schema(description = "是否升序", example = "true")
    private Boolean isAsc = true;

    @Schema(description = "排序字段(驼峰命名)", example = "createTime")
    private String sortBy;

    /**
     * 计算起始位置（用于自定义SQL的 limit offset）
     */
    public int from() {
        return (pageNo - 1) * pageSize;
    }

    /**
     * 1. 转换为 MyBatis Plus 的 Page 对象
     * (优先使用前端传来的排序，如果没有则不排序)
     */
    public <T> Page<T> toMpPage() {
        return toMpPage(null, true);
    }

    /**
     * 2. 转换为 MyBatis Plus 的 Page 对象 (指定默认排序)
     * (如果前端传了排序则用前端的，否则使用默认排序)
     *
     * @param defaultSortBy 默认排序字段 (如: "createTime" 或 "create_time")
     * @param defaultAsc    默认是否升序
     */
    public <T> Page<T> toMpPage(String defaultSortBy, boolean defaultAsc) {
        Page<T> page = new Page<>(pageNo, pageSize);
        List<OrderItem> orderItems = new ArrayList<>();

        // A. 优先处理前端传来的排序参数
        if (StringUtils.isNotBlank(sortBy)) {
            // 安全检查：防止SQL注入
            String cleanSortBy = sortBy.replaceAll(SQL_FILTER_REGEX, "");
            // 驼峰转下划线 (createTime -> create_time)
            String column = toUnderlineCase(cleanSortBy);

            orderItems.add(isAsc ? OrderItem.asc(column) : OrderItem.desc(column));
        }
        // B. 如果前端没传，且有默认排序，则使用默认排序
        else if (StringUtils.isNotBlank(defaultSortBy)) {
            String cleanDefault = defaultSortBy.replaceAll(SQL_FILTER_REGEX, "");
            String column = toUnderlineCase(cleanDefault);

            orderItems.add(defaultAsc ? OrderItem.asc(column) : OrderItem.desc(column));
        }

        if (!orderItems.isEmpty()) {
            page.addOrder(orderItems);
        }
        return page;
    }

    /**
     * 3. 转换为 MyBatis Plus 的 Page 对象 (手动指定强制排序规则)
     * (完全忽略前端的排序参数，使用代码中硬编码的规则)
     */
    public <T> Page<T> toMpPage(OrderItem... orderItems) {
        Page<T> page = new Page<>(pageNo, pageSize);
        if (orderItems != null && orderItems.length > 0) {
            page.addOrder(Arrays.asList(orderItems));
        }
        return page;
    }

    /**
     * 快捷方法：默认按创建时间倒序
     */
    public <T> Page<T> toMpPageDefaultSortByCreateTimeDesc() {
        return toMpPage("createTime", false);
    }

    /**
     * 快捷方法：默认按更新时间倒序
     */
    public <T> Page<T> toMpPageDefaultSortByUpdateTimeDesc() {
        return toMpPage("updateTime", false);
    }

    /**
     * 工具方法：驼峰转下划线
     * User -> user, userName -> user_name
     */
    private String toUnderlineCase(String camelCaseStr) {
        if (StringUtils.isBlank(camelCaseStr)) {
            return "";
        }
        // 如果已经是下划线命名，直接返回（简单判断）
        if (camelCaseStr.contains("_")) {
            return camelCaseStr;
        }
        StringBuilder sb = new StringBuilder();
        char[] chars = camelCaseStr.toCharArray();
        for (char c : chars) {
            if (Character.isUpperCase(c)) {
                sb.append("_").append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}