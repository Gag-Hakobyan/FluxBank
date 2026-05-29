package com.fluxbank.controller;

import com.fluxbank.dto.user.UserResponseDto;
import com.fluxbank.entity.User;
import com.fluxbank.exception.NotFoundException;
import com.fluxbank.mapper.UserMapper;
import com.fluxbank.service.UserService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/image/upload")
    public ResponseEntity<UserResponseDto> uploadImage(
            @RequestParam("image")
            @NotNull(message = "Image is required")
            MultipartFile multipartFile,
            Authentication authentication) throws IOException {
        User user = userService.getByEmail(authentication.getName());

        userService.uploadImage(user, multipartFile);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @GetMapping("/image/get")
    public ResponseEntity<byte[]> getImage(
            @RequestParam("imageName")
            @Size(min = 10, max = 150, message = "Image Name must contain from 8 to 150 characters")
            String imageName) throws IOException {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, imageName.toLowerCase().endsWith(".png") ? "image/png" : "image/jpeg")
                .body(userService.getImage(imageName));
    }
}
