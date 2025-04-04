package com.toby.ricemanagersystem.dto;


import com.toby.ricemanagersystem.model.enums.Role;
import com.toby.ricemanagersystem.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String password;
    private Long storeId;
    private Role role;
    private Status status;
}
