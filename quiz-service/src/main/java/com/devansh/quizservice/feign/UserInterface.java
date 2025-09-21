package com.devansh.quizservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "USER-SERVICE", url = "${user.service.url}")
public interface UserInterface {

    @GetMapping("/api/v1/users/{id}")
    ResponseEntity<?> getUserById(
        @PathVariable("id") Long userId,
        @RequestHeader("Authorization") String authHeader
    );
}
