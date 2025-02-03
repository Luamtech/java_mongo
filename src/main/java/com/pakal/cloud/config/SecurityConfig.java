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
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRepository(new CustomCsrfTokenRepository())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                // 🔹 CAMBIO: Exige CSRF en todas las solicitudes
                .requireCsrfProtectionMatcher(request -> true)
                // 🔹 Excluir `/api/csrf` de la protección CSRF
                .ignoringRequestMatchers("/api/csrf","/actuator/health", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html"))
            // Configuración de autorización
            .authorizeHttpRequests(auth -> auth
                // Permitir acceso público a las rutas de Swagger y Actuator
                .requestMatchers("/actuator/health", "/actuator/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                // Proteger las demás rutas con autenticación
                .requestMatchers(HttpMethod.GET, "/api/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/blog-forms").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults()); // Autenticación básica requerida

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
    
        // 🔹 Solo permitimos dominios específicos para mayor seguridad
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8082","https://mi-dominio-front.com"));
    
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        // 🔹 Permitimos los encabezados necesarios para autenticación y CSRF
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "X-XSRF-TOKEN"));
    
        // 🔹 Exponemos los encabezados CSRF y cookies para que el frontend pueda leerlos
        configuration.setExposedHeaders(Arrays.asList("X-XSRF-TOKEN", "Set-Cookie"));
    
        configuration.setAllowCredentials(true); // Necesario para autenticación con cookies
    
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