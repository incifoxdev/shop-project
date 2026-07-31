package SpringBootRest.config;

import SpringBootRest.security.CustomUserDetailsService;
import SpringBootRest.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())

                // La API JWT no debe crear sesiones HTTP
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authenticationProvider(authenticationProvider())

                .authorizeHttpRequests(auth -> auth

        // Login y registro
        .requestMatchers("/auth/**")
        .permitAll()

        // Swagger
        .requestMatchers(
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**"
        )
        .permitAll()

        // Imágenes
        .requestMatchers("/uploads/**")
        .permitAll()

        // Datos del usuario autenticado
        .requestMatchers(HttpMethod.GET, "/users/me")
        .authenticated()

        // Gestión administrativa de usuarios
        .requestMatchers("/users/**")
        .hasRole("ADMIN")

        // Consultar productos
        .requestMatchers(
                HttpMethod.GET,
                "/products",
                "/products/**"
        )
        .authenticated()

        // Administrar productos
        .requestMatchers(
                HttpMethod.POST,
                "/products",
                "/products/**"
        )
        .hasRole("ADMIN")

        .requestMatchers(
                HttpMethod.PUT,
                "/products/**"
        )
        .hasRole("ADMIN")

        .requestMatchers(
                HttpMethod.PATCH,
                "/products/**"
        )
        .hasRole("ADMIN")

        .requestMatchers(
                HttpMethod.DELETE,
                "/products/**"
        )
        .hasRole("ADMIN")

        // Otros endpoints administrativos
        .requestMatchers("/admin/**")
        .hasRole("ADMIN")

        // Dashboard administrativo
        .requestMatchers("/dashboard/**")
        .hasRole("ADMIN")

        // Todo lo demás requiere JWT
        .anyRequest()
        .authenticated()
)
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}