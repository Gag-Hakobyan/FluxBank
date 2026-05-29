package com.fluxbank.service;

import com.fluxbank.dto.PagingRequestDto;
import com.fluxbank.dto.PagingResponseDto;
import com.fluxbank.dto.user.*;
import com.fluxbank.entity.User;
import com.fluxbank.enums.UserRole;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    UserResponseDto register(UserSaveDto saveDto);

    void verify(UserResponseDto userResponseDto);

    User save(User user);

    User getByEmail(String email);

    User getUserById(long id);

    PagingResponseDto getAllByRole(PagingRequestDto paging, UserFilterDto filterDto, UserRole role);

    List<User> getAllByEnabled(boolean enabled);

    UserResponseDto getByVerifyToken(String token);

    void deleteAll(List<User> users);

    void uploadImage(User user, MultipartFile multipartFile) throws IOException;

    byte[] getImage(String name) throws IOException;

    UserResponseDto update(User user, UserUpdateDto updateDto);

    void changeStatusDto(long id, UserChangeStatusDto userChangeStatusDto);
}