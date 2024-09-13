package uk.co.dajohnston.tasktrackerapi.security;

import static org.springframework.security.core.context.SecurityContextHolder.createEmptyContext;

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
            .build();

    SecurityContext context = createEmptyContext();
    context.setAuthentication(new JwtAuthenticationConverter().convert(jwt));
    return context;
  }
}
