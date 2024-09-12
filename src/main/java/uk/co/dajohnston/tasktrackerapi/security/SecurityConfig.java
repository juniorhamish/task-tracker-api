package uk.co.dajohnston.tasktrackerapi.security;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private static final String[] SWAGGER_WHITELIST = {
    "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/swagger-resources"
  };

  @Value("${jwt.claims.namespace}")
  private final String customClaimNamespace;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers("/actuator/**")
                    .permitAll()
                    .requestMatchers(SWAGGER_WHITELIST)
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(
            oauth2 ->
                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
    return http.build();
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    var originalConverter = new JwtGrantedAuthoritiesConverter();
    var jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
        (Jwt jwt) -> {
          Collection<GrantedAuthority> result = originalConverter.convert(jwt);
          var rolesConverter = new JwtGrantedAuthoritiesConverter();
          rolesConverter.setAuthoritiesClaimName(customClaimNamespace + "/roles");
          rolesConverter.setAuthorityPrefix("ROLE_");
          result.addAll(rolesConverter.convert(jwt));
          return result;
        });
    return jwtAuthenticationConverter;
  }
}
