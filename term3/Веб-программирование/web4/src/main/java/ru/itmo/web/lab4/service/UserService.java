package ru.itmo.web.lab4.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.itmo.web.lab4.dto.AuthRequest;
import ru.itmo.web.lab4.dto.AuthResponse;
import ru.itmo.web.lab4.dto.RegisterRequest;
import ru.itmo.web.lab4.exception.RegisterException;
import ru.itmo.web.lab4.model.User;
import ru.itmo.web.lab4.repository.UserRepository;
import ru.itmo.web.lab4.security.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public void register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RegisterException("Passwords do not match");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RegisterException("Username is already taken");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    public AuthResponse authenticate(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RegisterException("Invalid username or password"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RegisterException("Invalid username or password");
        }
        String token = jwtTokenProvider.generateToken(user);
        return new AuthResponse(token);
    }
}
