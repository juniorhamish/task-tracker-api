package uk.co.dajohnston.tasktrackerapi.security;

import static org.springframework.security.core.context.SecurityContextHolder.createEmptyContext;

import java.util.Arrays;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WithMockJWTSecurityContextFactory implements WithSecurityContextFactory<WithMockJWT> {

  private final JwtAuthenticationConverter jwtAuthenticationConverter;

  @Value("${jwt.claims.namespace}")
  private final String customClaimNamespace;

  @Override
  public SecurityContext createSecurityContext(WithMockJWT annotation) {
    var jwt =
        Jwt.withTokenValue("token")
            .header("alg", "none")
            .claim("sub", annotation.subject())
            .claim("scope", annotation.scope())
            .claim(customClaimNamespace + "/user", Map.of("email", annotation.emailAddress()))
            .claim(customClaimNamespace + "/roles", Arrays.asList(annotation.roles()))
            .build();

    SecurityContext context = createEmptyContext();
    context.setAuthentication(jwtAuthenticationConverter.convert(jwt));
    return context;
  }
}
