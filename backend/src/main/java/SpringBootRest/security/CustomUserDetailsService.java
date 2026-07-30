package SpringBootRest.security;

import SpringBootRest.entities.User;
import SpringBootRest.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = repository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())

                // OWNER -> ROLE_OWNER automáticamente
                .roles(
                        user.getRoles()
                                .stream()
                                .map(role -> role.getName()
                                        .replace("ROLE_", ""))
                                .toArray(String[]::new)
                )

                .build();
    }
}