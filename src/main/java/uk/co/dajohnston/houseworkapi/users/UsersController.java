package uk.co.dajohnston.houseworkapi.users;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
  @Value("${jwt.claims.namespace}")
  private final String customClaimNamespace;

  @PostMapping("/users")
  @ResponseStatus(CREATED)
  @PreAuthorize("hasAuthority('SCOPE_create:users')")
  public User create(@RequestBody User user) {
    return usersService.create(user);
  }

  @GetMapping("/users")
  @PreAuthorize("hasAuthority('SCOPE_read:users')")
  public List<User> findAll(JwtAuthenticationToken authentication) {
    List<User> users;
    if (hasAdminRole(authentication)) {
      users = usersService.findAll();
    } else {
      users = usersService.findScopedUsers(emailAddress(authentication));
    }
    return users;
  }

  private String emailAddress(JwtAuthenticationToken authentication) {
    Map<String, Object> userDetails = authentication.getToken()
                                                    .getClaimAsMap(customClaimNamespace + "/user");
    return (String) userDetails.get("email");
  }

  private static boolean hasAdminRole(Authentication authentication) {
    return authentication.getAuthorities()
                         .stream()
                         .map(GrantedAuthority::getAuthority)
                         .anyMatch("ROLE_Admin"::equals);
  }
}
