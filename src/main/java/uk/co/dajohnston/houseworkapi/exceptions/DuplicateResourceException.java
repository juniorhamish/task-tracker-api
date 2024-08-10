package uk.co.dajohnston.houseworkapi.exceptions;

public class DuplicateResourceException extends RuntimeException {
  public DuplicateResourceException(Throwable cause) {
    super(cause);
  }
}
