package com.fluxbank.dto.user;

import com.fluxbank.annotation.Adult;
import com.fluxbank.enums.UserStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFilterDto {
    @Size(min = 2, max = 30, message = "Name must contain from 2 to 30 characters")
    private String name;

    @Size(min = 2, max = 30, message = "Surname must contain from 2 to 30 characters")
    private String surname;

    @Size(max = 100, message = "Email must contain maximum 100 characters")
    @Email(message = "Invalid email format")
    private String email;

    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @Past(message = "Created from date must be in the past")
    private LocalDate createdFrom;

    @Past(message = "Created to date must be in the past")
    private LocalDate createdTo;


    private UserStatus status;

    @Positive(message = "Account id must be positive")
    private Long accountId;
}
