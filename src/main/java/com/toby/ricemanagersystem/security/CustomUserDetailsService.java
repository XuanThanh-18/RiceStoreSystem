package com.toby.ricemanagersystem.security;

import com.toby.ricemanagersystem.exception.ResourceNotFoundException;
import com.toby.ricemanagersystem.model.User;
import com.toby.ricemanagersystem.model.enums.Status;
import com.toby.ricemanagersystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.toby.ricemanagersystem.model.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        System.out.println(user);
        System.out.println("User roles: " + user.getRole());
        return UserPrincipal.create(user);
    }
    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );

        if (user.getStatus() == Status.INACTIVE) {
            throw new RuntimeException("Sorry! You had been blocked!.");
        }

        System.out.println("User roles: " + user.getRole());

        return UserPrincipal.create(user);
    }

}