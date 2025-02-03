package com.pakal.cloud.config;

import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.config.Customizer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.pakal.cloud.repository.CustomCsrfTokenRepository;

import java.util.Arrays;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.User;

import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Value("${API_USERNAME:user}") // Leer variable de entorno
        private String username;

        @Value("${API_PASSWORD:password}") // Leer variable de entorno
        private String password;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http

                                // ✅ Habilitar CORS en Spring Security
                                .cors(Customizer.withDefaults()) // NUEVO: Permitir CORS correctamente

                                // Configuración CSRF
                                .csrf(csrf -> csrf
                                                .csrfTokenRepository(new CustomCsrfTokenRepository())
                                                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                                                .requireCsrfProtectionMatcher(request -> true)
                                                .ignoringRequestMatchers("/api/csrf", "/actuator/health",
                                                                "/swagger-ui/**", "/v3/api-docs/**"))
                                // Configuración de autorización
                                .authorizeHttpRequests(auth -> auth
                                                // Permitir acceso público a las rutas de Swagger y Actuator

                                                .requestMatchers("/api/csrf",
                                                                "/actuator/health",
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/swagger-resources/**",
                                                                "/webjars/**")
                                                                .permitAll()
                                                // Proteger las demás rutas con autenticación
                                                .requestMatchers(HttpMethod.GET, "/api/**").authenticated()
                                                .requestMatchers(HttpMethod.POST, "/api/blog-forms").authenticated()
                                                .requestMatchers(HttpMethod.PUT, "/api/**").authenticated()
                                                .requestMatchers(HttpMethod.DELETE, "/api/**").authenticated()
                                                .anyRequest().authenticated())
                                .httpBasic(Customizer.withDefaults()); // Autenticación básica requerida

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(Arrays.asList(
                           "http://localhost:8082",
                                "https://java-mongo.onrender.com",
                                "http://localhost:8083"));
                configuration.setAllowedMethods(Arrays.asList(
                                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList(
                                "Content-Type",
                                "Authorization",
                                "X-XSRF-TOKEN",
                                "Access-Control-Allow-Headers",
                                "Access-Control-Allow-Origin",
                                "Accept"));
                configuration.setExposedHeaders(Arrays.asList(
                                "X-XSRF-TOKEN",
                                "Set-Cookie",
                                "Access-Control-Allow-Origin"));
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

        @Bean
        public UserDetailsService userDetailsService() {

                UserDetails user = User.builder()
                                .username("user")
                                .password(passwordEncoder().encode("password"))
                                .roles("USER")
                                .build();

                return new InMemoryUserDetailsManager(user);
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}