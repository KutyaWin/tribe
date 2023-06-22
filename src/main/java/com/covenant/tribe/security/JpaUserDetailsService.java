package com.covenant.tribe.security;


import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.exeption.user.UserNotFoundException;
import com.covenant.tribe.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

    UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username)  {
        Optional<User> user = userRepository.findUserByUserEmail(username);

        return user
                .map(SecurityUser::new)
                .orElseThrow(() -> {
                    String message = String.format("User with email: %s not found", username);
                    log.error(message);
                 return new UserNotFoundException(username);
                });
    }
}
