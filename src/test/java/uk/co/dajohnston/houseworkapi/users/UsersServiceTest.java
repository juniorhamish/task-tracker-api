package uk.co.dajohnston.houseworkapi.users;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

  @Mock
  private UsersRepository usersRepository;

  @Test
  void create_savesUserToRepository() {
    User user = new User("David", "Johnston", "david.johnston@example.com");

    new UsersService(usersRepository).create(user);

    verify(usersRepository).save(user);
  }

  @Test
  void findAll_returnsAllUsersFromRepository() {
    when(usersRepository.findAll()).thenReturn(
        List.of(new User("David", "Johnston", "david.johnston@example.com"),
            new User("Bobby", "Davro", "bobby.davro@example.com")));

    List<User> users = new UsersService(usersRepository).findAll();

    assertThat(users, contains(new User("David", "Johnston", "david.johnston@example.com"),
        new User("Bobby", "Davro", "bobby.davro@example.com")));
  }
}
