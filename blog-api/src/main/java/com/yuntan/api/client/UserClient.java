package com.yuntan.api.client;

import com.yuntan.api.dto.UserCommentDTO;
import com.yuntan.common.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "blog-user-service")
public interface UserClient {

    @GetMapping("/comment/{id}")
    Result<UserCommentDTO> getUserComment(@PathVariable Long id);

}
