package com.yuntan.article.domain.dto.front;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleInteractDTO {
    private Long id;
    private Integer viewCount;
    private Integer likeCount;
    private Integer collectCount;
}