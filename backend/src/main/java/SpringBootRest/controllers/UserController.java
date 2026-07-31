package SpringBootRest.controllers;

import SpringBootRest.dto.UserRequestDTO;
import SpringBootRest.dto.UserResponseDTO;
import SpringBootRest.entities.User;
import SpringBootRest.mappers.UserMapper;
import SpringBootRest.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import SpringBootRest.dto.UserUpdateRequestDTO;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponseDTO getCurrentUser(
            Authentication authentication
    ) {
        String email = authentication.getName();

        User user = userService.findByEmail(email);

        return UserMapper.toDTO(user);
    }

    @PostMapping
    public UserResponseDTO create(
            @Valid @RequestBody UserRequestDTO request
    ) {
        User user = userService.create(request);

        return UserMapper.toDTO(user);
    }

    @GetMapping
    public List<UserResponseDTO> findAll() {
        return userService.findAll()
                .stream()
                .map(UserMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public UserResponseDTO findById(
            @PathVariable UUID id
    ) {
        return UserMapper.toDTO(
                userService.findById(id)
        );
    }

   @PutMapping("/{id}")
public UserResponseDTO update(
        @PathVariable UUID id,
        @Valid @RequestBody UserUpdateRequestDTO request
) {
    User user = userService.update(id, request);

    return UserMapper.toDTO(user);
}

    @DeleteMapping("/{id}")
    public String delete(
            @PathVariable UUID id
    ) {
        userService.delete(id);

        return "User deleted successfully";
    }
}