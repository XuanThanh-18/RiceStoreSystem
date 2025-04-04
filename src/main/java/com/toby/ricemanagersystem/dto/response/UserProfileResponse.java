package com.toby.ricemanagersystem.dto.response;

import com.toby.ricemanagersystem.model.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private String email;
    private String fullName;
    private Gender gender;
    private Date dateOfBirth;
    private String phoneNumber;
    private String identityNumber;
}
