package com.toby.ricemanagersystem.dto;

import com.toby.ricemanagersystem.model.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {
    private String fullName;
    private Gender gender;
    private String email;
}
