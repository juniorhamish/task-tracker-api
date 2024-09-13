package uk.co.dajohnston.tasktrackerapi.users.controller;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import uk.co.dajohnston.tasktrackerapi.exceptions.DuplicateResourceException;
import uk.co.dajohnston.tasktrackerapi.users.UsersService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UsersController {

  private final UsersService usersService;

  @Value("${jwt.claims.namespace}")
  private final String customClaimNamespace;

  @PostMapping("/users")
  @ResponseStatus(CREATED)
  @PreAuthorize("hasAuthority('SCOPE_create:users')")
  public UserDTO create(@RequestBody UserDTO user) {
    log.info("Creating user.");
    try {
      return usersService.create(user);
    } catch (DuplicateResourceException e) {
      log.error("Failed to create user %s".formatted(user.emailAddress()), e);
      throw new ResponseStatusException(
          CONFLICT, "User with email address %s already exists".formatted(user.emailAddress()), e);
    }
  }

  @GetMapping("/users")
  @PreAuthorize("hasAuthority('SCOPE_read:users')")
  public List<UserDTO> findAll(JwtAuthenticationToken authentication) {
    String emailAddress = emailAddress(authentication);
    log.info("Finding users by email address {}", emailAddress);
    return usersService.findScopedUsers(emailAddress);
  }

  private String emailAddress(JwtAuthenticationToken authentication) {
    Map<String, Object> userDetails =
        authentication.getToken().getClaimAsMap(customClaimNamespace + "/user");
    return (String) userDetails.get("email");
  }
}
