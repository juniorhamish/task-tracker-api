package uk.co.dajohnston.houseworkapi.users;

import static org.mockito.Mockito.verify;

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
}
