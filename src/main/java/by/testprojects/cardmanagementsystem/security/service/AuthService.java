package by.testprojects.cardmanagementsystem.security.service;

import by.testprojects.cardmanagementsystem.entity.dto.AuthRequest;
import by.testprojects.cardmanagementsystem.entity.dto.AuthResponse;
import by.testprojects.cardmanagementsystem.entity.dto.RegisterRequest;
import by.testprojects.cardmanagementsystem.entity.Role;
import by.testprojects.cardmanagementsystem.entity.User;
import by.testprojects.cardmanagementsystem.entity.mapper.UserMapper;
import by.testprojects.cardmanagementsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public AuthResponse register(RegisterRequest registerRequest) {
        User user = userMapper.toUser(registerRequest);
        user.setRoles(new HashSet<>());
        user.setRoles(Set.of(Role.ADMIN, Role.USER));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userService.createUser(user);
        String jwtToken = jwtService.generateToken(savedUser);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userService.findByEmail(request.getEmail());

        String jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}
