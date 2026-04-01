package com.github.url_shortner.service;

import com.github.url_shortner.dto.LoginRequest;
import com.github.url_shortner.dto.RegisterRequest;
import com.github.url_shortner.enums.Role;
import com.github.url_shortner.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.github.url_shortner.entity.User;


@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;



    public void register(RegisterRequest request){
        if (userRepository.existsByUsername(request.getUsername())){
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);
    }


    public String login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );


        return jwtService.generateToken(request.getUsername());
    }
}
