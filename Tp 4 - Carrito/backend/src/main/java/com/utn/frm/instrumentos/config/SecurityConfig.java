package com.utn.frm.instrumentos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new ChangeSessionIdAuthenticationStrategy();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        HttpSessionSecurityContextRepository repository = new HttpSessionSecurityContextRepository();
        repository.setAllowSessionCreation(true);
        return repository;
    }

    // --- BEAN PARA LA CONFIGURACIÓN CORS ---
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Especifica el origen de tu frontend
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        // Especifica los métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        // Permite todas las cabeceras (puedes ser más específico si lo deseas)
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // Permite el envío de credenciales (cookies)
        configuration.setAllowCredentials(true);
        // Configura por cuánto tiempo la respuesta de una solicitud pre-flight (OPTIONS) puede ser cacheada
        configuration.setMaxAge(3600L); // 1 hora

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica esta configuración a todos los paths
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityContextRepository securityContextRepository) throws Exception {
        http
                // --- APLICAR LA CONFIGURACIÓN CORS ---
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .securityContext(context -> context
                        .securityContextRepository(securityContextRepository)
                )
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(new AntPathRequestMatcher("/api/auth/login")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/api/auth/register")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/api/auth/me")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()

                        // --- REGLAS PARA INSTRUMENTOS ---
                        // Ver instrumentos es público
                        .requestMatchers(new AntPathRequestMatcher("/api/instrumentos/**", HttpMethod.GET.name())).permitAll()
                        // CRUD (Crear, Actualizar, Borrar) instrumentos solo para ADMIN
                        .requestMatchers(new AntPathRequestMatcher("/api/instrumentos/**")).hasRole("ADMIN")




                        // --- REGLAS PARA CATEGORÍAS ---
                        // Ver categorías es público
                        .requestMatchers(new AntPathRequestMatcher("/api/categoria/**", HttpMethod.GET.name())).hasAnyRole("ADMIN","OPERADOR")
                        // CRUD (Crear, Actualizar, Borrar) categorías solo para ADMIN
                        .requestMatchers(new AntPathRequestMatcher("/api/categoria/**")).hasRole("ADMIN")

                        // --- REGLAS PARA PEDIDOS ---
                        // Crear un pedido (POST /api/pedidos): Permitido para  USUARIO AUTENTICADO (ADMIN, OPERADOR)
                        .requestMatchers(new AntPathRequestMatcher("/api/pedidos", HttpMethod.POST.name())).hasAnyRole("ADMIN","OPERADOR")
                        // Crear preferencia de MP (POST /api/pedidos/{pedidoId}/preferencia): Permitido para CUALQUIER USUARIO AUTENTICADO
                        .requestMatchers(new AntPathRequestMatcher("/api/pedidos/*/preferencia", HttpMethod.POST.name())).hasAnyRole("ADMIN","OPERADOR")

                        // Ver TODOS los pedidos (GET /api/pedidos): Solo para ADMIN y OPERADOR
                        .requestMatchers(new AntPathRequestMatcher("/api/pedidos", HttpMethod.GET.name())).hasAnyRole("ADMIN", "OPERADOR", "VISOR")

                        // Ver UN pedido por ID (GET /api/pedidos/{id})
                        .requestMatchers(new AntPathRequestMatcher("/api/pedidos/{id}", HttpMethod.GET.name())).hasAnyRole("ADMIN", "OPERADOR", "VISOR")

                        .anyRequest().authenticated() // Todas las demás solicitudes no especificadas requieren autenticación
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                        .permitAll()
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }
}
