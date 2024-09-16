package uk.co.dajohnston.tasktrackerapi.users;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.dajohnston.tasktrackerapi.exceptions.DuplicateResourceException;
import uk.co.dajohnston.tasktrackerapi.users.controller.User;
import uk.co.dajohnston.tasktrackerapi.users.repository.UserEntity;
import uk.co.dajohnston.tasktrackerapi.users.repository.UsersRepository;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {UserMapperImpl.class})
class UsersServiceTest {

  @Mock private UsersRepository usersRepository;
  @Autowired private UserMapper userMapper;
  private UsersService usersService;

  @BeforeEach
  void setUp() {
    usersService = new UsersService(usersRepository, userMapper);
  }

  @Test
  void create_savesUserToRepository() {
    User user = new User("David", "Johnston", "david.johnston@example.com");
    UserEntity userEntity = new UserEntity("David", "Johnston", "david.johnston@example.com");

    usersService.create(user);

    verify(usersRepository).save(userEntity);
  }

  @Test
  void create_returnsSavedUser() {
    when(usersRepository.save(any()))
        .thenReturn(new UserEntity("David", "Johnston", "david.johnston@example.com"));

    User result = usersService.create(new User(null, null, null));

    assertThat(result, is(new User("David", "Johnston", "david.johnston@example.com")));
  }

  @Test
  void create_duplicateEmail_throwsDuplicateException() {
    when(usersRepository.save(new UserEntity(null, null, "david.johnston@example.com"))).thenThrow(DuplicateKeyException.class);

    User user = new User(null, null, "david.johnston@example.com");
    assertThrows(DuplicateResourceException.class, () -> usersService.create(user));
  }

  @Test
  void findAll_returnsAllUsersFromRepository() {
    when(usersRepository.findAll())
        .thenReturn(
            List.of(
                new UserEntity("David", "Johnston", "david.johnston@example.com"),
                new UserEntity("Bobby", "Davro", "bobby.davro@example.com")));

    List<User> users = usersService.findAll();

    assertThat(
        users,
        contains(
            new User("David", "Johnston", "david.johnston@example.com"),
            new User("Bobby", "Davro", "bobby.davro@example.com")));
  }
}
