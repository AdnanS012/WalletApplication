package com.example.WalletApp;


import com.example.WalletApp.Domain.User;
import com.example.WalletApp.Domain.Money;
import com.example.WalletApp.Repository.IUserRepository;
import com.example.WalletApp.Service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUserSuccessfully() {
        when(userRepository.existsByUsername("testUser")).thenReturn(false);
        when(passwordEncoder.encode("securePassword")).thenReturn("encodedPassword");
        userService.registerUser("testUser", "securePassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testDuplicateUserRegistrationThrowsException() {
        when(userRepository.existsByUsername("duplicateUser")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser("duplicateUser", "password123"),
                "Duplicate registration should throw exception");
    }

    @Test
    public void testAuthenticateUserSuccessfully() {
        User user = new User("testUser", "encodedPassword");
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(passwordEncoder.matches("securePassword", "encodedPassword")).thenReturn(true);

        boolean isAuthenticated = userService.authenticateUser("testUser", "securePassword");

        assertTrue(isAuthenticated);
    }


    @Test
    public void testAuthenticateUserWithWrongPassword() {
        User user = new User("testUser", "securePassword");
        when(userRepository.findByUsername("testUser")).thenReturn(user);

        boolean isAuthenticated = userService.authenticateUser("testUser", "wrongPassword");

        assertFalse(isAuthenticated);
    }

    @Test
    public void testAuthenticateNonExistentUser() {
        // Given: No user exists with the given username
        when(userRepository.findByUsername("nonExistentUser")).thenReturn(null);

        // When: The user is authenticated
        boolean isAuthenticated = userService.authenticateUser("nonExistentUser", "anyPassword");

        // Then: The authentication should fail
        assertFalse(isAuthenticated);
    }

    @Test
    public void testRegisterUserWithEmptyUsernameThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser("", "password123")
        );
    }

    @Test
    public void testRegisterUserWithEmptyPasswordThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser("username", "")
        );
    }
}
