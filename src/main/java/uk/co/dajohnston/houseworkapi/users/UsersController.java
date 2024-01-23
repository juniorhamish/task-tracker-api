package uk.co.dajohnston.houseworkapi.users;

import static org.springframework.http.HttpStatus.CREATED;

import lombok.RequiredArgsConstructor;
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
  public User create(@RequestBody User user) {
    usersService.create(user);
    return user;
  }
}
