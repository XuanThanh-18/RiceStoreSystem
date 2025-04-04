package com.toby.ricemanagersystem.service.impl;

import com.toby.ricemanagersystem.dto.*;
import com.toby.ricemanagersystem.model.User;
import com.toby.ricemanagersystem.model.UserProfile;
import com.toby.ricemanagersystem.model.enums.Role;
import com.toby.ricemanagersystem.model.enums.Status;
import com.toby.ricemanagersystem.repository.UserProfileRepository;
import com.toby.ricemanagersystem.repository.UserRepository;
import com.toby.ricemanagersystem.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements com.toby.ricemanagersystem.service.UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileRepository userProfileRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public User getCurrentUser() {
        UserPrincipal currentUserPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User>currentUser = userRepository.findById(currentUserPrincipal.getId());
        if(!currentUser.isPresent()) {
            throw new RuntimeException("Authenticated user not found.");
        }
        return currentUser.get();
    }

    @Override
    public List<User> getAllUsers() {
        // lay ra tat ca chu cua hang va nhan vien  (Chi ap dung cho Systemadmin)
        try{
            User currentUser = getCurrentUser();
            if(currentUser.getRole() != Role.SYSTEM_ADMIN){
                throw new RuntimeException("Error retrieving user.Current user is not system admin.");
            }else{
                List<Role> roles = new ArrayList<>();
                roles.add(Role.STORE_OWNER);
                roles.add(Role.STAFF);
                return userRepository.findByRoleIn(roles);
            }
        }catch(Exception e){
            throw new IllegalArgumentException("Bạn không có quyền xem thông tin này");
        }
    }

    @Override
    public User deactivateUser(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        if(user.getStatus() == Status.ACTIVE){
            user.setStatus(Status.INACTIVE);
        }else{
            user.setStatus(Status.ACTIVE);
        }
        return userRepository.save(user);
    }

    @Override
    public String getStatusByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        String status = "";
        if(userOptional.isPresent()){
            status = userOptional.get().getStatus().toString();
        }else{
            throw new RuntimeException("User not found.");
        }
        return status;
    }

    @Override
    public String getRoleByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        String role = "";
        if(userOptional.isPresent()){
            role = userOptional.get().getRole().name();
        }else{
            throw new RuntimeException("User not found.");
        }
        return role;
    }
    @Override
    public UserDTO changePasswordByUserId(Long userId, String newPassword) {
        if(userId == null || newPassword == null){
            throw new IllegalArgumentException("User id and newPassword can not be null");
        }
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()){
            throw new RuntimeException("Not found any user with id " + userId);
        }
        User foundUser = userOptional.get();
        foundUser.setPassword(passwordEncoder.encode(newPassword));

        User updatedUser = userRepository.save(foundUser);
        return modelMapper.map(updatedUser, UserDTO.class);
    }

    @Override
    public void registerUser(RegisterRequestDTO registerRequestDTO) {
        String username = registerRequestDTO.getUsername();
        if(username.matches("^\\d.*")){
            throw new IllegalArgumentException("Username cannot begin with a number.");
        }

        Optional<User> existingUser = userRepository.findByUsername(username);
        if(existingUser.isPresent()){
            throw new IllegalArgumentException("User already exists.");
        }

        var userProfile = userProfileRepository.findByEmail(registerRequestDTO.getEmail());
        if(userProfile.isPresent()){
            throw new IllegalArgumentException("Email has been registered");
        }

        User user = User.builder()
                .username(registerRequestDTO.getUsername())
                .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
                .role(Role.STORE_OWNER)
                .status(Status.ACTIVE)
                .build();
        User newUser = userRepository.save(user);
        UserProfile newUserProfile = UserProfile.builder()
                .email(registerRequestDTO.getEmail())
                .user(newUser)
                .build();
        userProfileRepository.save(newUserProfile);
    }

    @Override
    public UserProfileDTO updateUser(Long userId, UserUpdateDTO userUpdateDTO) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(!userOptional.isPresent()){
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
        UserProfile userProfile = userProfileRepository.findByUser(userOptional.get());
        if(userProfile == null){
            userProfile = UserProfile.builder().user(userOptional.get()).build();
        }
        userProfile = UserProfile.builder()
                .fullName(userUpdateDTO.getFullName())
                .gender(userUpdateDTO.getGender())
                .email(userUpdateDTO.getEmail())
                .build();
        userRepository.save(userOptional.get());
        return modelMapper.map(userProfile, UserProfileDTO.class);
    }

    @Override
    public UserDTO createStaffAccount(StaffCreateDTO staffCreateDTO) {
        boolean isExistUser = userRepository.existsByUsername(staffCreateDTO.getUsername());
        if(!isExistUser){
            throw new IllegalArgumentException("User "+staffCreateDTO.getUsername()+" dã tồn tại! Hãy bấm quên mật khẩu nếu bạn không nhớ mật khẩu của mình!");
        }
        User currentUser = getCurrentUser();
        String prefixStaffAccount = currentUser.getStore().getId().toString() +"/";
        User newStaff = User.builder()
                .username( prefixStaffAccount + staffCreateDTO.getUsername())
                .password(passwordEncoder.encode(staffCreateDTO.getPassword()))
                .role(Role.STAFF)
                .status(Status.ACTIVE)
                .store(currentUser.getStore())
                .createdBy(currentUser.getUsername())
                .build();

        User savedStaff = userRepository.save(newStaff);
        return modelMapper.map(savedStaff, UserDTO.class);
    }

    @Override
    public List<UserDTO> getAllUserOfStore(){
        try{
            User currentUser = getCurrentUser();
            List<User> users = userRepository.findByStoreId(currentUser.getStore().getId());
            return users.stream().map(
                    user -> modelMapper.map(user,UserDTO.class)
            )
                    .collect(Collectors.toList());
        }catch(Exception e){
            throw new RuntimeException("Error retrieving user !" + e);
        }
    }

    @Override
    public UserDTO getUserById(Long storeId) {
        Optional<User> userOptional = userRepository.findById(storeId);
        if(!userOptional.isPresent()){
            throw new IllegalArgumentException("User not found with store id: " + storeId);
        }
        return modelMapper.map(userOptional.get(), UserDTO.class);
    }

    @Override
    public boolean checkUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
}
