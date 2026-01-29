package com.yuntan.article.domain.doc;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "article_contents")
public class ArticleContentDoc {

    @Id
    private String id; // Mongo的主键，通常不给业务用
    
    @Indexed(unique = true) // 关键：建立唯一索引
    private Long articleId; // 对应 MySQL 的 ID
    
    private String content; // 大文本

}