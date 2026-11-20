package SpringBootRest.services;

import SpringBootRest.dto.UserUpdateRequestDTO;
import SpringBootRest.entities.Role;
import SpringBootRest.entities.User;
import SpringBootRest.exception.UserNotFoundException;
import SpringBootRest.repositories.RoleRepository;
import SpringBootRest.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User adminUser;
    private User normalUser;
    private Role adminRole;
    private Role userRole;

    @BeforeEach
    void setUp() {
        adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");

        userRole = new Role();
        userRole.setName("ROLE_USER");

        adminUser = new User();
        adminUser.setId(UUID.randomUUID());
        adminUser.setEmail("admin@email.com");
        adminUser.setEnabled(true);
        adminUser.setRoles(Set.of(adminRole));

        normalUser = new User();
        normalUser.setId(UUID.randomUUID());
        normalUser.setEmail("user@email.com");
        normalUser.setEnabled(true);
        normalUser.setRoles(Set.of(userRole));
    }

    // ─── DELETE ───────────────────────────────────────────

    @Test
    void delete_deberiaEliminarUsuarioNormal() {
        when(userRepository.findById(normalUser.getId()))
                .thenReturn(Optional.of(normalUser));

        userService.delete(normalUser.getId(), "admin@email.com");

        verify(userRepository).delete(normalUser);
    }

    @Test
    void delete_deberiaLanzarExcepcionSiEliminaPropiaCuenta() {
        when(userRepository.findById(adminUser.getId()))
                .thenReturn(Optional.of(adminUser));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> userService.delete(adminUser.getId(), "admin@email.com")
        );

        assertEquals("You cannot delete your own account", ex.getMessage());
        verify(userRepository, never()).delete(any());
    }

    @Test
    void delete_deberiaLanzarExcepcionSiEliminaUltimoAdmin() {
        User otroAdmin = new User();
        otroAdmin.setId(UUID.randomUUID());
        otroAdmin.setEmail("otro@email.com");
        otroAdmin.setEnabled(true);
        otroAdmin.setRoles(Set.of(adminRole));

        when(userRepository.findById(otroAdmin.getId()))
                .thenReturn(Optional.of(otroAdmin));
        when(userRepository.countActiveAdmins()).thenReturn(1L);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> userService.delete(otroAdmin.getId(), "admin@email.com")
        );

        assertEquals("You cannot delete the last active administrator", ex.getMessage());
        verify(userRepository, never()).delete(any());
    }

    @Test
    void delete_deberiaLanzarExcepcionSiUsuarioNoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(userRepository.findById(idInexistente))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.delete(idInexistente, "admin@email.com")
        );
    }

    // ─── UPDATE ───────────────────────────────────────────

    @Test
    void update_deberiaLanzarExcepcionSiDesactivaPropiaCuenta() {
        when(userRepository.findById(adminUser.getId()))
                .thenReturn(Optional.of(adminUser));

        UserUpdateRequestDTO request = new UserUpdateRequestDTO();
        request.setName("Admin");
        request.setEmail("admin@email.com");
        request.setEnabled(false);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> userService.update(adminUser.getId(), request, "admin@email.com")
        );

        assertEquals("You cannot disable your own account", ex.getMessage());
    }

    @Test
    void update_deberiaLanzarExcepcionSiSeQuitaRolAdmin() {
        when(userRepository.findById(adminUser.getId()))
                .thenReturn(Optional.of(adminUser));

        UserUpdateRequestDTO request = new UserUpdateRequestDTO();
        request.setName("Admin");
        request.setEmail("admin@email.com");
        request.setRoles(Set.of("ROLE_USER"));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> userService.update(adminUser.getId(), request, "admin@email.com")
        );

        assertEquals("You cannot remove your own administrator role", ex.getMessage());
    }
}
