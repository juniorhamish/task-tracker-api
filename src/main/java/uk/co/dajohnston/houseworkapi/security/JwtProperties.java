package uk.co.dajohnston.houseworkapi.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt.claims")
@Data
public class JwtProperties {

  /**
   * The namespace that is used for custom claims, e.g. roles and user information.
   */
  private String namespace;
}
