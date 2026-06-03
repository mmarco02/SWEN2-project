package fh.swen.swen2tourplanner.controller;

import fh.swen.swen2tourplanner.domain.User;
import fh.swen.swen2tourplanner.dto.UserDTO;
import fh.swen.swen2tourplanner.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Long> register(@RequestBody UserDTO userDTO) {
        User user = userService.register(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user.getId());
    }

    @PostMapping("/login")
    public ResponseEntity<Long> login(@RequestBody UserDTO userDTO) {
        User user = userService.login(userDTO.username(), userDTO.password());
        return ResponseEntity.ok(user.getId());
    }

    @GetMapping("/getId/{username}")
    public ResponseEntity<Long> getUserIdFromUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserIdFromUsername(username));
    }
}
