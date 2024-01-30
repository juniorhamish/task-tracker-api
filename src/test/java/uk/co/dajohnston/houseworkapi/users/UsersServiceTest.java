package uk.co.dajohnston.houseworkapi.users;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig({UsersService.class, LocalValidatorFactoryBean.class})
class UsersServiceTest {

  @MockBean
  private UsersRepository usersRepository;
  @Autowired
  private UsersService usersService;

  @Test
  void create_savesUserToRepository() {
    User user = new User("David", "Johnston", "david.johnston@example.com");

    usersService.create(user);

    verify(usersRepository).save(user);
  }

  @Test
  void create_returnsSavedUser() {
    User user = new User("David", "Johnston", "david.johnston@example.com");
    when(usersRepository.save(any())).thenReturn(user);

    User result = usersService.create(new User(null, null, null));

    assertThat(result, is(user));
  }

  @Test
  void create_emailAddressNull_throwsSomething() {
    usersService.create(new User("David", "Johnston", null));
  }

  @Test
  void findAll_returnsAllUsersFromRepository() {
    when(usersRepository.findAll()).thenReturn(
        List.of(new User("David", "Johnston", "david.johnston@example.com"),
            new User("Bobby", "Davro", "bobby.davro@example.com")));

    List<User> users = usersService.findAll();

    assertThat(users, contains(new User("David", "Johnston", "david.johnston@example.com"),
        new User("Bobby", "Davro", "bobby.davro@example.com")));
  }

  @Test
  void findAllScopedUsers_returnsUserWithEmailAddressFromRepository() {
    when(usersRepository.findByEmailAddress(any())).thenReturn(
        new User("David", "Johnston", "david.johnston@example.com"));

    List<User> users = usersService.findScopedUsers("david.johnston@example.com");

    assertThat(users, contains(new User("David", "Johnston", "david.johnston@example.com")));
  }
}
