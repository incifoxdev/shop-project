package SpringBootRest.mappers;

import SpringBootRest.dto.UserRequestDTO;
import SpringBootRest.dto.UserResponseDTO;
import SpringBootRest.entities.Role;
import SpringBootRest.entities.User;

public class UserMapper {

    public static User toEntity(UserRequestDTO dto) {

        User user = new User();

        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setPassword(dto.getPassword());

        return user;
    }

    public static UserResponseDTO toDTO(User user) {

        UserResponseDTO dto = new UserResponseDTO();

        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());

        dto.setEnabled(user.isEnabled());
        dto.setCreatedAt(user.getCreatedAt());

        dto.setRoles(
                user.getRoles()
                        .stream()
                        .map(Role::getName)
                        .toList()
        );

        return dto;
    }

    public static void updateEntity(User user, UserRequestDTO dto) {

        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setPassword(dto.getPassword());
    }
}
