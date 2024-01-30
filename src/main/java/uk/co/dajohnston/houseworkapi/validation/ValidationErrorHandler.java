package uk.co.dajohnston.houseworkapi.validation;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;
import static org.springframework.web.util.WebUtils.ERROR_EXCEPTION_ATTRIBUTE;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import uk.co.dajohnston.houseworkapi.validation.ValidationError.Error;

@ControllerAdvice
@RequiredArgsConstructor
public class ValidationErrorHandler {

  private final MessageSource messageSource;

  @ExceptionHandler(ConstraintViolationException.class)
  public final ResponseEntity<Object> handleException(Exception exception, WebRequest request) {
    var headers = new HttpHeaders();

    if (exception instanceof ConstraintViolationException constraintViolationException) {
      var validationError = new ValidationError();
      var locale = request.getLocale();
      constraintViolationException.getConstraintViolations()
                                  .forEach(violation -> validationError.addError(
                                      translateViolation(violation, locale)));
      return new ResponseEntity<>(validationError, new HttpHeaders(), BAD_REQUEST);
    } else {
      return handleExceptionInternal(exception, headers, request);
    }
  }

  private Error translateViolation(ConstraintViolation<?> violation, Locale locale) {
    String message = messageSource.getMessage(violation.getMessageTemplate(),
        violation.getExecutableParameters(), violation.getMessage(), locale);
    return new Error(message, violation.getPropertyPath()
                                       .toString());
  }


  protected ResponseEntity<Object> handleExceptionInternal(Exception ex, HttpHeaders headers,
      WebRequest request) {
    request.setAttribute(ERROR_EXCEPTION_ATTRIBUTE, ex, SCOPE_REQUEST);
    return new ResponseEntity<>(null, headers, INTERNAL_SERVER_ERROR);
  }
}
