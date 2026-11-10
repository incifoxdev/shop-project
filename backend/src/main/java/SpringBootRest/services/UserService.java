package SpringBootRest.services;

import SpringBootRest.dto.UserRequestDTO;
import SpringBootRest.dto.UserUpdateRequestDTO;
import SpringBootRest.entities.Role;
import SpringBootRest.entities.User;
import SpringBootRest.exception.UserNotFoundException;
import SpringBootRest.mappers.UserMapper;
import SpringBootRest.repositories.RoleRepository;
import SpringBootRest.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private static final Set<String> ALLOWED_ROLES =
            Set.of(
                    ROLE_USER,
                    ROLE_ADMIN
            );

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public User create(UserRequestDTO request) {

        User user = UserMapper.toEntity(request);

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        Role userRole = findRoleByName(
                ROLE_USER
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
                        new UserNotFoundException(
                                "User not found"
                        )
                );
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        )
                );
    }

    @Transactional
    public User update(
            UUID id,
            UserUpdateRequestDTO request,
            String authenticatedEmail
    ) {
        User user = findById(id);

        boolean updatingOwnAccount =
                user.getEmail()
                        .equalsIgnoreCase(
                                authenticatedEmail
                        );

        boolean currentlyAdmin =
                user.hasRole(ROLE_ADMIN);

        boolean currentlyActive =
                user.isEnabled();

        boolean willRemainEnabled =
                request.getEnabled() == null
                        ? currentlyActive
                        : request.getEnabled();

        Set<String> requestedRoleNames =
                resolveRequestedRoleNames(
                        request,
                        user
                );

        boolean willRemainAdmin =
                requestedRoleNames.contains(
                        ROLE_ADMIN
                );

        if (
                updatingOwnAccount &&
                !willRemainEnabled
        ) {
            throw new IllegalStateException(
                    "You cannot disable your own account"
            );
        }

        if (
                updatingOwnAccount &&
                currentlyAdmin &&
                !willRemainAdmin
        ) {
            throw new IllegalStateException(
                    "You cannot remove your own administrator role"
            );
        }

        boolean removingActiveAdmin =
                currentlyAdmin &&
                currentlyActive &&
                (
                        !willRemainAdmin ||
                        !willRemainEnabled
                );

        if (
                removingActiveAdmin &&
                userRepository.countActiveAdmins() <= 1
        ) {
            throw new IllegalStateException(
                    "You cannot disable or demote the last active administrator"
            );
        }

        user.setName(
                request.getName()
        );

        user.setEmail(
                request.getEmail()
        );

        if (
                request.getPassword() != null &&
                !request.getPassword().isBlank()
        ) {
            user.setPassword(
                    passwordEncoder.encode(
                            request.getPassword()
                    )
            );
        }

        if (
                request.getEnabled() != null
        ) {
            user.setEnabled(
                    request.getEnabled()
            );
        }

        if (
                request.getRoles() != null
        ) {
            user.setRoles(
                    resolveRoles(
                            requestedRoleNames
                    )
            );
        }

        return userRepository.save(user);
    }

    @Transactional
    public void delete(
            UUID id,
            String authenticatedEmail
    ) {
        User user = findById(id);

        boolean deletingOwnAccount =
                user.getEmail()
                        .equalsIgnoreCase(
                                authenticatedEmail
                        );

        if (deletingOwnAccount) {
            throw new IllegalStateException(
                    "You cannot delete your own account"
            );
        }

        boolean deletingLastActiveAdmin =
                user.hasRole(ROLE_ADMIN) &&
                user.isEnabled() &&
                userRepository.countActiveAdmins() <= 1;

        if (deletingLastActiveAdmin) {
            throw new IllegalStateException(
                    "You cannot delete the last active administrator"
            );
        }

        userRepository.delete(user);
    }

    private Set<String> resolveRequestedRoleNames(
            UserUpdateRequestDTO request,
            User user
    ) {
        if (request.getRoles() == null) {
            return user.getRoles()
                    .stream()
                    .map(Role::getName)
                    .collect(
                            java.util.stream.Collectors.toSet()
                    );
        }

        Set<String> requestedRoles =
                new HashSet<>(
                        request.getRoles()
                );

        if (requestedRoles.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one role is required"
            );
        }

        boolean containsInvalidRole =
                requestedRoles
                        .stream()
                        .anyMatch(role ->
                                !ALLOWED_ROLES.contains(role)
                        );

        if (containsInvalidRole) {
            throw new IllegalArgumentException(
                    "Invalid role"
            );
        }

        return requestedRoles;
    }

    private Set<Role> resolveRoles(
            Set<String> roleNames
    ) {
        Set<Role> roles = new HashSet<>();

        for (String roleName : roleNames) {
            roles.add(
                    findRoleByName(roleName)
            );
        }

        return roles;
    }

    private Role findRoleByName(
            String roleName
    ) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() ->
                        new IllegalStateException(
                                roleName +
                                " is not configured"
                        )
                );
    }
}
