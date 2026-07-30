package SpringBootRest.services;

import SpringBootRest.dto.UserRequestDTO;
import SpringBootRest.entities.User;
import SpringBootRest.exception.UserNotFoundException;
import SpringBootRest.mappers.UserMapper;
import SpringBootRest.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public User create(UserRequestDTO request) {

        User user = UserMapper.toEntity(request);

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        return userRepository.save(user);
    }

    public List<User> findAll() {

        return userRepository.findAll();
    }

    public User findById(UUID id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));
    }

    public User update(UUID id, UserRequestDTO request) {

        User user = findById(id);

        UserMapper.updateEntity(user, request);

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        return userRepository.save(user);
    }

    public void delete(UUID id) {

        User user = findById(id);

        userRepository.delete(user);
    }
}
