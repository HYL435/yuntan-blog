package com.yuntan.api.client;

import com.yuntan.api.dto.ArticleInfoDTO;
import com.yuntan.common.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "blog-article-service")
public interface ArticleClient {


    @GetMapping("/admin/articles/info/{id}")
    Result<ArticleInfoDTO> getArticleInfoById(@PathVariable Long id);

}
