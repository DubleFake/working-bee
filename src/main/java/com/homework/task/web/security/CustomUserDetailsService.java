package com.homework.task.web.security;

import com.homework.task.database.repositories.UserRepository;
import com.homework.task.database.templates.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Primary
@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch user from the database
        User user = userRepository.findByUsername(username);

        // Create a UserDetails object without exposing the password directly
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),        // Using email as the username
                user.getPassword(),        // Return password hash for internal use
                true,                  // account is enabled
                true,                  // account is non-expired
                true,                  // credentials are non-expired
                true,                  // account is non-locked
                user.getAuthorities()   // Fetch user's roles/authorities
        );
    }

}
