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

    /**
     * Constructs a CustomUserDetailsService with the specified user repository.
     * This constructor initializes the service with the `UserRepository`, which is used to fetch user information
     * from the database. The `userRepository` is injected through Spring's dependency injection mechanism.
     *
     * @param userRepository - The repository used to interact with the user data in the database.
     */

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads user details by the given username.
     * This method fetches the user from the database using the provided username. It then constructs and returns a
     * `UserDetails` object, which includes the user's username, password, account status (enabled, expired, locked),
     * and roles/authorities. The password is provided in the `UserDetails` object, but not exposed directly.
     *
     * @param username - The username of the user to load.
     * @throws UsernameNotFoundException - If no user is found with the given username.
     * @return UserDetails - A `UserDetails` object containing user information such as username, password, roles, and account status.
     */

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch user from the database
        User user = userRepository.findByUsername(username);

        // Create a UserDetails object without exposing the password directly
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),        // Return password hash for internal use
                true,                  // account is enabled
                true,                  // account is non-expired
                true,                  // credentials are non-expired
                true,                  // account is non-locked
                user.getAuthorities()   // Fetch user's roles/authorities
        );
    }

}
