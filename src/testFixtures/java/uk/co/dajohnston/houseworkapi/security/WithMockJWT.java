package uk.co.dajohnston.houseworkapi.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockJWTSecurityContextFactory.class)
public @interface WithMockJWT {

  String[] authorities() default {};
  String emailAddress() default "";
}
