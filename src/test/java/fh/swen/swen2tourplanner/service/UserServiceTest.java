package fh.swen.swen2tourplanner.service;

import fh.swen.swen2tourplanner.domain.User;
import fh.swen.swen2tourplanner.dto.UserDTO;
import fh.swen.swen2tourplanner.exception.AuthenticationFailedException;
import fh.swen.swen2tourplanner.exception.ResourceAlreadyExistsException;
import fh.swen.swen2tourplanner.exception.ResourceNotFoundException;
import fh.swen.swen2tourplanner.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordHash passwordHash;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("hashedpassword");
    }

    @Test
    void register_newUsername_createsUser() {
        UserDTO dto = new UserDTO("newuser", "password123");
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordHash.hash("password123")).thenReturn("hashedpassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.register(dto);

        assertNotNull(result);
        verify(passwordHash).hash("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_existingUsername_throwsException() {
        UserDTO dto = new UserDTO("testuser", "password123");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        assertThrows(ResourceAlreadyExistsException.class, () -> userService.register(dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_validCredentials_returnsUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordHash.verify("password123", "hashedpassword")).thenReturn(true);

        User result = userService.login("testuser", "password123");

        assertEquals("testuser", result.getUsername());
    }

    @Test
    void login_wrongPassword_throwsException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordHash.verify("wrongpass", "hashedpassword")).thenReturn(false);

        assertThrows(AuthenticationFailedException.class,
                () -> userService.login("testuser", "wrongpass"));
    }

    @Test
    void login_nonExistingUser_throwsException() {
        when(userRepository.findByUsername("nobody")).thenReturn(Optional.empty());

        assertThrows(AuthenticationFailedException.class,
                () -> userService.login("nobody", "pass"));
    }

    @Test
    void getUserIdFromUsername_existingUser_returnsId() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        Long id = userService.getUserIdFromUsername("testuser");

        assertEquals(1L, id);
    }

    @Test
    void getUserIdFromUsername_nonExistingUser_throwsException() {
        when(userRepository.findByUsername("nobody")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserIdFromUsername("nobody"));
    }
}
