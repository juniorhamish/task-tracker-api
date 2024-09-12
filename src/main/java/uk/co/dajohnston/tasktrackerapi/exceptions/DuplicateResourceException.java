package uk.co.dajohnston.tasktrackerapi.exceptions;

public class DuplicateResourceException extends RuntimeException {
  public DuplicateResourceException(Throwable cause) {
    super(cause);
  }
}
