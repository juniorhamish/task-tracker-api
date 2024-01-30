package uk.co.dajohnston.houseworkapi.validation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
public class ValidationConfiguration {

  @Bean
  public static MethodValidationPostProcessor validationPostProcessor() {
    var processor = new MethodValidationPostProcessor();
    processor.setAdaptConstraintViolations(false);
    return processor;
  }
}
