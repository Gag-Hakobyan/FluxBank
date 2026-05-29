package com.fluxbank.mapper;

import com.fluxbank.dto.user.UserResponseDto;
import com.fluxbank.dto.user.UserSaveDto;
import com.fluxbank.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserSaveDto userSaveDto);

    UserResponseDto toDto(User user);

    User toEntity(UserResponseDto userResponseDto);

    List<UserResponseDto> toDtoList(List<User> users);
}
