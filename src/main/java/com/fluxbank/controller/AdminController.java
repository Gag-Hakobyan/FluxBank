package com.fluxbank.controller;

import com.fluxbank.dto.PagingRequestDto;
import com.fluxbank.dto.PagingResponseDto;
import com.fluxbank.dto.user.UserChangeStatusDto;
import com.fluxbank.dto.user.UserFilterDto;
import com.fluxbank.dto.user.UserResponseDto;
import com.fluxbank.dto.user.UserUpdateDto;
import com.fluxbank.entity.User;
import com.fluxbank.enums.OrderDirection;
import com.fluxbank.enums.UserRole;
import com.fluxbank.enums.UserStatus;
import com.fluxbank.exception.NotFoundException;
import com.fluxbank.mapper.UserMapper;
import com.fluxbank.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Validated
public class AdminController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/users")
    public ResponseEntity<PagingResponseDto> getUsers(
            @RequestParam(name = "page", defaultValue = "0", required = false)
            @Min(value = 0, message = "Page cannot be negative")
            int page,

            @RequestParam(name = "size", defaultValue = "10", required = false)
            @Min(value = 1, message = "Size must be at least 1")
            @Max(value = 100, message = "Size cannot be more than 100")
            int size,

            @RequestParam(name = "orderBy", defaultValue = "id", required = false)
            @Size(min = 1, max = 50)
            String orderBy,

            @RequestParam(name = "orderDirection", defaultValue = "ASC", required = false)
            OrderDirection orderDirection,

            @RequestBody @Valid UserFilterDto filterDto
    ) {
        PagingRequestDto paging = PagingRequestDto.builder()
                .page(page)
                .size(size)
                .orderBy(orderBy)
                .orderDirection(orderDirection.name())
                .build();
        return ResponseEntity.ok(userService.getAllByRole(paging, filterDto, UserRole.USER));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> getUser(
            @PathVariable @Positive(message = "User id must be positive") long id
    ) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable @Positive(message = "User id must be positive") long id,
            @RequestBody @Valid UserUpdateDto updateDto
    ) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(userService.update(user, updateDto));
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<Void> changeUserStatus(
            @PathVariable @Positive(message = "User id must be positive") long id,
            @RequestBody @Valid UserChangeStatusDto userChangeStatusDto
    ) {
        userService.changeStatusDto(id, userChangeStatusDto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{id}/image/upload")
    public ResponseEntity<Void> updateImage(
            @PathVariable @Positive(message = "User id must be positive") long id,
            @RequestParam("image") @NotNull(message = "Image is required") MultipartFile multipartFile
    ) throws IOException {
        User user = userService.getUserById(id);

        userService.uploadImage(user, multipartFile);
        return ResponseEntity.noContent().build();
    }
}
