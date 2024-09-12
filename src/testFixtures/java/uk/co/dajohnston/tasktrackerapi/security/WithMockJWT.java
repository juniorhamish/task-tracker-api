package uk.co.dajohnston.tasktrackerapi.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockJWTSecurityContextFactory.class)
public @interface WithMockJWT {

  String scope() default "";

  String emailAddress() default "";

  String[] roles() default {};

  String subject() default "";
}
