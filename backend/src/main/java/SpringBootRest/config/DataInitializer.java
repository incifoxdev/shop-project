package SpringBootRest.config;

import SpringBootRest.entities.Role;
import SpringBootRest.entities.User;
import SpringBootRest.repositories.RoleRepository;
import SpringBootRest.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) {


        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> {

                    Role role = new Role();
                    role.setName("ROLE_ADMIN");

                    return roleRepository.save(role);
                });


        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {

                    Role role = new Role();
                    role.setName("ROLE_USER");

                    return roleRepository.save(role);
                });



        if(userRepository.findByEmail("admin@email.com").isEmpty()) {

            User admin = new User();

            admin.setName("Administrador");
            admin.setEmail("admin@email.com");

            admin.setPassword(
                    passwordEncoder.encode("123456")
            );

            admin.setRoles(new HashSet<>());

            admin.getRoles().add(adminRole);

            userRepository.save(admin);


            System.out.println("==============================");
            System.out.println("ADMIN CREADO");
            System.out.println("email: admin@email.com");
            System.out.println("password: 123456");
            System.out.println("==============================");
        }

    }
}