package com.yuntan.common.domain;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页响应结果")
public class PageDTO<T> {

    @Schema(description = "总条数")
    protected Long total;

    @Schema(description = "总页数")
    protected Long pages;

    @Schema(description = "当前页数据")
    protected List<T> list;

    /**
     * 构建空分页结果
     */
    public static <T> PageDTO<T> empty(Long total, Long pages) {
        return new PageDTO<>(total, pages, Collections.emptyList());
    }

    /**
     * 根据 Page 对象构建空结果
     */
    public static <T> PageDTO<T> empty(Page<?> page) {
        return new PageDTO<>(page.getTotal(), page.getPages(), Collections.emptyList());
    }

    /**
     * 1. 直接转换 (类型一致 T -> T)
     */
    public static <T> PageDTO<T> of(Page<T> page) {
        if (page == null) {
            return new PageDTO<>(0L, 0L, Collections.emptyList());
        }
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return empty(page);
        }
        return new PageDTO<>(page.getTotal(), page.getPages(), page.getRecords());
    }

    /**
     * 2. 自定义转换逻辑 (R -> T, 使用 Lambda 表达式)
     * 示例: PageDTO.of(userPage, user -> new UserVO(user));
     */
    public static <T, R> PageDTO<T> of(Page<R> page, Function<R, T> mapper) {
        if (page == null) {
            return new PageDTO<>(0L, 0L, Collections.emptyList());
        }
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return empty(page);
        }
        List<T> collect = page.getRecords().stream()
                .map(mapper)
                .collect(Collectors.toList());
        return new PageDTO<>(page.getTotal(), page.getPages(), collect);
    }

    /**
     * 3. 替换 List (用于已经手动转换好 List 的场景)
     */
    public static <T> PageDTO<T> of(Page<?> page, List<T> list) {
        return new PageDTO<>(page.getTotal(), page.getPages(), list);
    }

    /**
     * 4. 自动 BeanCopy 转换 (R -> T, 基于 Class 反射)
     * 替代了原先的 BeanUtils.copyList
     * 示例: PageDTO.of(userPage, UserVO.class);
     */
    public static <T, R> PageDTO<T> of(Page<R> page, Class<T> clazz) {
        if (page == null) {
            return new PageDTO<>(0L, 0L, Collections.emptyList());
        }
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return empty(page);
        }

        List<T> targetList = page.getRecords().stream().map(item -> {
            try {
                T target = clazz.getDeclaredConstructor().newInstance();
                BeanUtils.copyProperties(item, target);
                return target;
            } catch (Exception e) {
                throw new RuntimeException("PageDTO BeanCopy error", e);
            }
        }).collect(Collectors.toList());

        return new PageDTO<>(page.getTotal(), page.getPages(), targetList);
    }
}