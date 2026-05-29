package com.fluxbank.controller;

import com.fluxbank.dto.auth.AuthRequestDto;
import com.fluxbank.dto.auth.AuthResponseDto;
import com.fluxbank.dto.user.UserResponseDto;
import com.fluxbank.dto.user.UserSaveDto;
import com.fluxbank.entity.User;
import com.fluxbank.exception.AlreadyExistsException;
import com.fluxbank.exception.NotFoundException;
import com.fluxbank.mapper.UserMapper;
import com.fluxbank.service.UserService;
import com.fluxbank.util.JwtTokenUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class AuthController {
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody @Valid UserSaveDto userSaveDto) {
        User user = userService.getByEmail(userSaveDto.getEmail());
        if (user != null) {
            throw new AlreadyExistsException("User by email: " + user.getEmail() + " already exists");
        }
        return ResponseEntity.ok(userService.register(userSaveDto));
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verify(
            @RequestParam("token")
            @NotBlank(message = "Token is required")
            @Pattern(regexp = "^\\d{6}$", message = "Token must be 6 digits")
            String token
    ) {
        UserResponseDto userByVerifyToken = userService.getByVerifyToken(token);
        if (userByVerifyToken == null) {
            throw new IllegalStateException("Invalid or expired verification token");
        }

        userService.verify(userByVerifyToken);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid AuthRequestDto authRequestDto) {
        User user = userService.getByEmail(authRequestDto.getEmail());
        if (user == null
                || !passwordEncoder.matches(authRequestDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        if (!user.isEnabled()) {
            throw new IllegalStateException("User is not verified");
        }

        return ResponseEntity.ok(AuthResponseDto.builder()
                .token(jwtTokenUtil.generateToken(user.getEmail(), user.getId()))
                .userResponseDto(userMapper.toDto(user))
                .build());
    }
}
