package com.api.authapi.unit.services;

import com.api.authapi.application.mappers.UserResponseMapper;
import com.api.authapi.application.services.UserService;
import com.api.authapi.config.AuthenticatedUserProvider;
import com.api.authapi.config.JwtService;
import com.api.authapi.domain.dtos.user.*;
import com.api.authapi.domain.enums.Role;
import com.api.authapi.domain.models.User;
import com.api.authapi.infraestructure.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserResponseMapper userResponseMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @InjectMocks
    private UserService authService;

    private String email;
    private String existingEmail;
    private String password;
    private String encodedPassword;
    private String token;
    private String refreshToken;
    private User existingUser;

    @BeforeEach
    void setUp() {
        email = "email@gmail.com";
        existingEmail = "existingEmail@gmail.com";
        password = "password";
        encodedPassword = "encodedPassword";
        token = "token";
        refreshToken = "refreshToken";
        existingUser = User.builder()
                .email(existingEmail)
                .password(password)
                .refreshToken(refreshToken)
                .role(Role.USER)
                .build();
    }

    @Test
    void register_ShouldReturnAuthResponse_WhenDataIsCorrect() {
        RegisterUserCommand request = new RegisterUserCommand(email, password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(jwtService.generateToken(any(User.class))).thenReturn(token);
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn(refreshToken);
        when(userResponseMapper.apply(any(User.class))).thenReturn(UserResponse.builder().email(email).build());

        AuthResponse response = authService.register(request);

        assertEquals(token, response.getToken());
        assertEquals(refreshToken, response.getRefreshToken());
        assertEquals(email, response.getUser().getEmail());
    }

    @Test
    void register_ShouldReturnConflict_WhenUserAlreadyExists() {
        RegisterUserCommand request = new RegisterUserCommand(existingEmail, password);

        when(userRepository.findByEmail(existingEmail)).thenReturn(Optional.of(existingUser));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                authService.register(request));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void login_ShouldReturnAuthResponse_WhenDataIsCorrect() {
        LoginUserRequest request = new LoginUserRequest(existingEmail, password);

        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(userRepository.findByEmail(existingEmail)).thenReturn(Optional.of(existingUser));
        when(jwtService.generateToken(any(User.class))).thenReturn(token);
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn(refreshToken);
        when(userResponseMapper.apply(any(User.class))).thenReturn(UserResponse.builder().email(existingEmail).build());

        AuthResponse response = authService.login(request);

        assertEquals(token, response.getToken());
        assertEquals(refreshToken, response.getRefreshToken());
        assertEquals(existingEmail, response.getUser().getEmail());
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenEmailDoesNotExist() {
        LoginUserRequest request = new LoginUserRequest(email, password);

        doThrow(new BadCredentialsException("Bad credentials")).when(authenticationManager).authenticate(any());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.login(request));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenPasswordDoesNotMatch() {
        LoginUserRequest request = new LoginUserRequest(email, "incorrectPassword");

        doThrow(new BadCredentialsException("Bad credentials")).when(authenticationManager).authenticate(any());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.login(request));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void logout_ShouldClearRefreshToken_WhenTokenIsValid() {
        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(existingUser);

        authService.logout();

        assertNull(existingUser.getRefreshToken());
    }

    @Test
    void refresh_ShouldReturnAuthResponse_WhenRefreshTokenIsValid() {
        RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(existingUser);
        when(jwtService.generateToken(any(User.class))).thenReturn(token);
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn(refreshToken);
        when(userResponseMapper.apply(any(User.class))).thenReturn(UserResponse.builder().email(existingEmail).build());

        AuthResponse response = authService.refresh(request);

        assertEquals(token, response.getToken());
        assertEquals(refreshToken, response.getRefreshToken());
        assertEquals(existingEmail, response.getUser().getEmail());
    }

    @Test
    void refresh_ShouldReturnUnauthorized_WhenRefreshTokenIsInvalid() {
        RefreshTokenRequest request = new RefreshTokenRequest("fakeRefreshToken");

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(existingUser);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.refresh(request));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void updateUser_ShouldReturnUserResponse_WhenDataIsCorrect() {
        UpdateUserRequest request = new UpdateUserRequest(password, "newPassword");

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(existingUser);
        when(passwordEncoder.matches(password, existingUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn(encodedPassword);
        when(userResponseMapper.apply(any(User.class))).thenReturn(UserResponse.builder().email(existingEmail).build());

        UserResponse response = authService.updateUser(request);

        assertEquals(existingEmail, response.getEmail());
    }

    @Test
    void updateUser_ShouldReturnUnauthorized_WhenLastPasswordDoesNotMatch() {
        UpdateUserRequest request = new UpdateUserRequest(password, "newPassword");

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(existingUser);
        when(passwordEncoder.matches(password, existingUser.getPassword())).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.updateUser(request));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenTokenIsValid() {
        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(existingUser);

        authService.deleteUser();

        verify(userRepository).delete(existingUser);
    }
}