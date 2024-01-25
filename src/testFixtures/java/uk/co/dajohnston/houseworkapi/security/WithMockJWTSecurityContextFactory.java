package uk.co.dajohnston.houseworkapi.security;

import static org.springframework.security.core.context.SecurityContextHolder.createEmptyContext;

import java.util.Arrays;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WithMockJWTSecurityContextFactory implements WithSecurityContextFactory<WithMockJWT> {

  private final JwtAuthenticationConverter jwtAuthenticationConverter;

  @Override
  public SecurityContext createSecurityContext(WithMockJWT annotation) {
    var jwt = Jwt.withTokenValue("token")
                 .header("alg", "none")
                 .claim("sub", "user")
                 .claim("scope", annotation.scope())
                 .claim("https://housework-api.onrender.com/user",
                     Map.of("email", annotation.emailAddress()))
                 .claim("https://housework-api.onrender.com/roles",
                     Arrays.asList(annotation.roles()))
                 .build();

    SecurityContext context = createEmptyContext();
    context.setAuthentication(jwtAuthenticationConverter.convert(jwt));
    return context;
  }
}
