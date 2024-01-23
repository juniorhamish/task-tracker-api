package uk.co.dajohnston.houseworkapi.users;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UsersControllerTest {

  @Mock
  private UsersService usersService;
  private UsersController usersController;

  @BeforeEach
  void setUp() {
    usersController = new UsersController(usersService);
  }

  @Test
  void create_returnsCreatedUser() {
    User user = new User();

    var createdUser = usersController.create(user);

    assertThat(createdUser, is(user));
  }

  @Test
  void create_usesServiceToCreateUser() {
    User user = new User();

    usersController.create(user);

    verify(usersService).create(user);
  }
}
