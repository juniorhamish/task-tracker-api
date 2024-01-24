package uk.co.dajohnston.houseworkapi.users;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
  @PreAuthorize("hasAuthority('SCOPE_write:users')")
  public User create(@RequestBody User user) {
    return usersService.create(user);
  }

  @GetMapping("/users")
  @PreAuthorize("hasAuthority('SCOPE_read:users')")
  public List<User> findAll() {
    return usersService.findAll();
  }
}
