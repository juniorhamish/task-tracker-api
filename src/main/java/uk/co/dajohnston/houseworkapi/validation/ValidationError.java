package uk.co.dajohnston.houseworkapi.validation;

import java.util.ArrayList;
import java.util.Collection;
import lombok.Data;

@Data
public class ValidationError {

  private final Collection<Error> errors = new ArrayList<>();

  public void addError(Error error) {
    errors.add(error);
  }

  public record Error(String message, String path) {

  }
}
