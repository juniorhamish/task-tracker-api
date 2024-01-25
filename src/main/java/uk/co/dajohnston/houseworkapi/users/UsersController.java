package uk.co.dajohnston.houseworkapi.users;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UsersController {

  private final UsersService usersService;

  @PostMapping("/users")
  @ResponseStatus(CREATED)
  @PreAuthorize("hasAuthority('SCOPE_create:users')")
  public User create(@RequestBody User user) {
    return usersService.create(user);
  }

  @GetMapping("/users")
  @PreAuthorize("hasAnyAuthority('SCOPE_read:users', 'SCOPE_read:allusers')")
  public List<User> findAll(JwtAuthenticationToken authentication) {
    List<User> users;
    if (hasAllUsersScope(authentication)) {
      users = usersService.findAll();
    } else {
      users = usersService.findScopedUsers(emailAddress(authentication));
    }
    return users;
  }

  private static String emailAddress(JwtAuthenticationToken authentication) {
    Map<String, Object> userDetails = authentication.getToken()
                                                    .getClaimAsMap(
                                                        "https://housework-api.onrender.com/user");
    String emailAddress = null;
    if (userDetails != null) {
      emailAddress = (String) userDetails.get("email");
    }
    return emailAddress;
  }

  private static boolean hasAllUsersScope(Authentication authentication) {
    return authentication.getAuthorities()
                         .stream()
                         .map(GrantedAuthority::getAuthority)
                         .anyMatch("SCOPE_read:allusers"::equals);
  }
}
