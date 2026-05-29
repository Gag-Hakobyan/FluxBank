package com.fluxbank.dto.user;

import com.fluxbank.annotation.Adult;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {
    @Size(min = 2, max = 30, message = "Name must contain from 2 to 30 characters")
    private String name;

    @Size(min = 2, max = 30, message = "Surname must contain from 2 to 30 characters")
    private String surname;

    @Size(min = 8, max = 100, message = "Password must contain from 8 to 100 characters")
    private String password;

    @Adult(message = "User must be at least 18 years old")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;
}
