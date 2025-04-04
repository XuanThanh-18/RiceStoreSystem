package com.toby.ricemanagersystem.service;

import com.toby.ricemanagersystem.dto.*;
import com.toby.ricemanagersystem.model.User;

import java.util.List;

public interface UserService {
    User getCurrentUser();

    List<User> getAllUsers();

    User deactivateUser(Long id);

    String getStatusByUsername(String username);

    String getRoleByUsername(String username);

    UserDTO changePasswordByUserId(Long userId, String newPassword);

    void registerUser(RegisterRequestDTO registerRequestDTO);

    UserProfileDTO updateUser(Long userId, UserUpdateDTO userUpdateDTO);

    UserDTO createStaffAccount(StaffCreateDTO staffCreateDTO);

    List<UserDTO> getAllUserOfStore();

    UserDTO getUserById(Long storeId);

    boolean checkUsernameExists(String username);
}
