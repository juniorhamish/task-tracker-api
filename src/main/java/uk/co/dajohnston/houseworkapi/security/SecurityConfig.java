package uk.co.dajohnston.houseworkapi.security;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.oauth2.core.authorization.OAuth2AuthorizationManagers.hasScope;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/actuator/**")
                                                     .permitAll()
                                                     .requestMatchers("/tasks")
                                                     .access(hasScope("read:tasks"))
                                                     .requestMatchers("/users")
                                                     .authenticated()
                                                     .anyRequest()
                                                     .authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));
    return http.build();
  }
}
