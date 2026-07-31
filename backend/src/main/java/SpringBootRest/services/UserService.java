package SpringBootRest.services;
import SpringBootRest.dto.UserUpdateRequestDTO;
import SpringBootRest.dto.UserRequestDTO;
import SpringBootRest.entities.Role;
import SpringBootRest.entities.User;
import SpringBootRest.exception.UserNotFoundException;
import SpringBootRest.mappers.UserMapper;
import SpringBootRest.repositories.RoleRepository;
import SpringBootRest.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public User create(UserRequestDTO request) {

        User user = UserMapper.toEntity(request);

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() ->
                        new IllegalStateException(
                                "ROLE_USER is not configured"
                        )
                );

        user.getRoles().add(userRole);

        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found")
                );
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found")
                );
    }
public User update(
        UUID id,
        UserUpdateRequestDTO request
) {

    User user = findById(id);

    user.setName(request.getName());
    user.setEmail(request.getEmail());

    if (
        request.getPassword() != null &&
        !request.getPassword().isBlank()
    ) {
        user.setPassword(
            passwordEncoder.encode(request.getPassword())
        );
    }

    if (request.getEnabled() != null) {
        user.setEnabled(request.getEnabled());
    }

    return userRepository.save(user);
}

    public void delete(UUID id) {

        User user = findById(id);

        userRepository.delete(user);
    }
}
