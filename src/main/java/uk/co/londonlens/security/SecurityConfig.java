
package uk.co.londonlens.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain applicationSecurity(
            HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/login",
                    "/error",
                    "/favicon.ico",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/assets/**",
                    "/fonts/**",
                    "/api/v1/products",
                    "/api/v1/products/**",
                    "/actuator/health",
                    "/actuator/health/**",
                    "/actuator/info"
                ).permitAll()

                .requestMatchers(
                    "/admin/**",
                    "/api/v1/admin/**"
                ).hasRole("ADMIN")

                // Secure new routes by default.
                .anyRequest().denyAll()
            )
            .formLogin(form -> form
                .defaultSuccessUrl("/admin/dashboard", true)
            );

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
