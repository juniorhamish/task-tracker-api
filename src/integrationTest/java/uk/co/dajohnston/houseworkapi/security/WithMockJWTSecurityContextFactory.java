package uk.co.dajohnston.houseworkapi.security;

import static org.springframework.security.core.context.SecurityContextHolder.createEmptyContext;

import java.util.Arrays;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockJWTSecurityContextFactory implements WithSecurityContextFactory<WithMockJWT> {

  @Override
  public SecurityContext createSecurityContext(WithMockJWT annotation) {
    var jwt = Jwt.withTokenValue("token")
                 .header("alg", "none")
                 .claim("sub", "user")
                 .build();

    var token = new JwtAuthenticationToken(jwt, Arrays.stream(annotation.authorities())
                                                      .map(SimpleGrantedAuthority::new)
                                                      .toList());

    SecurityContext context = createEmptyContext();
    context.setAuthentication(token);
    return context;
  }
}
