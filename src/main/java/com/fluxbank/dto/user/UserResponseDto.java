package com.fluxbank.dto.user;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private long id;
    private String name;
    private String surname;
    private String email;
    private LocalDate birthDate;
    private String imagePath;
}
