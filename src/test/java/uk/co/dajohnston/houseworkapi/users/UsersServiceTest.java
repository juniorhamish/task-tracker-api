package uk.co.dajohnston.houseworkapi.users;

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
import uk.co.dajohnston.houseworkapi.exceptions.DuplicateResourceException;
import uk.co.dajohnston.houseworkapi.users.controller.UserDTO;
import uk.co.dajohnston.houseworkapi.users.repository.UserEntity;
import uk.co.dajohnston.houseworkapi.users.repository.UsersRepository;

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
    UserDTO user = new UserDTO("David", "Johnston", "david.johnston@example.com");
    UserEntity userEntity = new UserEntity("David", "Johnston", "david.johnston@example.com");

    usersService.create(user);

    verify(usersRepository).save(userEntity);
  }

  @Test
  void create_returnsSavedUser() {
    when(usersRepository.save(any()))
        .thenReturn(new UserEntity("David", "Johnston", "david.johnston@example.com"));

    UserDTO result = usersService.create(new UserDTO(null, null, null));

    assertThat(result, is(new UserDTO("David", "Johnston", "david.johnston@example.com")));
  }

  @Test
  void create_duplicateEmail_throwsDuplicateException() {
    UserEntity user = new UserEntity(null, null, "david.johnston@example.com");
    when(usersRepository.save(user)).thenThrow(DuplicateKeyException.class);

    UserDTO userDTO = new UserDTO(null, null, "david.johnston@example.com");
    assertThrows(DuplicateResourceException.class, () -> usersService.create(userDTO));
  }

  @Test
  void findAll_returnsAllUsersFromRepository() {
    when(usersRepository.findAll())
        .thenReturn(
            List.of(
                new UserEntity("David", "Johnston", "david.johnston@example.com"),
                new UserEntity("Bobby", "Davro", "bobby.davro@example.com")));

    List<UserDTO> users = usersService.findAll();

    assertThat(
        users,
        contains(
            new UserDTO("David", "Johnston", "david.johnston@example.com"),
            new UserDTO("Bobby", "Davro", "bobby.davro@example.com")));
  }

  @Test
  void findAllScopedUsers_returnsUserWithEmailAddressFromRepository() {
    when(usersRepository.findByEmailAddress(any()))
        .thenReturn(new UserEntity("David", "Johnston", "david.johnston@example.com"));

    List<UserDTO> users = usersService.findScopedUsers("david.johnston@example.com");

    assertThat(users, contains(new UserDTO("David", "Johnston", "david.johnston@example.com")));
  }
}
