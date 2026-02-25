package com.yuntan.api.client;

import com.yuntan.api.dto.UserCommentDTO;
import com.yuntan.common.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;

@FeignClient(name = "blog-user-service")
public interface UserClient {

    @GetMapping("/front/users/comment/{id}")
    Result<UserCommentDTO> getUserComment(@PathVariable Long id);

    @PostMapping("/front/users/comment/batch")
    Result<List<UserCommentDTO>> getUserComments(@RequestBody Set<Long> userIds);
}
