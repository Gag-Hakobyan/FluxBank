package com.fluxbank.dto.user;

import com.fluxbank.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserChangeStatusDto {
    @NotNull(message = "Status is required")
    private UserStatus status;
}