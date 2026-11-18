package io.github.adit12t.artifactorymanager.security;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Artifactory Manager API", version = "2.0",
        description = "Secure REST gateway for JFrog Artifactory repositories and builds."))
@SecurityScheme(name = "apiKey", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER,
        paramName = ApiKeyFilter.HEADER)
public class SecurityConfig {

    @Bean
    SecurityFilterChain api(HttpSecurity http, ApiKeyProperties keys) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // header-authenticated API, no cookies
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new ApiKeyFilter(keys), AnonymousAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/actuator/health/**", "/actuator/info").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/**").hasRole("READER")
                        .requestMatchers("/api/v1/**").hasRole("ADMIN")
                        .anyRequest().denyAll())
                .exceptionHandling(e -> e.authenticationEntryPoint(
                        (request, response, ex) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED)))
                .build();
    }
}
