package fh.swen.swen2tourplanner.service;

import fh.swen.swen2tourplanner.domain.User;
import fh.swen.swen2tourplanner.dto.UserDTO;
import fh.swen.swen2tourplanner.exception.AuthenticationFailedException;
import fh.swen.swen2tourplanner.exception.ResourceAlreadyExistsException;
import fh.swen.swen2tourplanner.exception.ResourceNotFoundException;
import fh.swen.swen2tourplanner.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordHash passwordHash;

    public User register(UserDTO dto) {
        if (userRepository.findByUsername(dto.username()).isPresent()) {
            throw new ResourceAlreadyExistsException("Username already taken");
        }
        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(passwordHash.hash(dto.password()));
        return userRepository.save(user);
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationFailedException("Invalid username or password"));

        if (!passwordHash.verify(password, user.getPassword())) {
            throw new AuthenticationFailedException("Invalid username or password");
        }

        return user;
    }

    public Long getUserIdFromUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username))
                .getId();
    }
}
