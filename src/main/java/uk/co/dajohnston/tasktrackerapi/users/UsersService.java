package uk.co.dajohnston.tasktrackerapi.users;

import static java.util.stream.StreamSupport.stream;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import uk.co.dajohnston.tasktrackerapi.exceptions.DuplicateResourceException;
import uk.co.dajohnston.tasktrackerapi.users.controller.User;
import uk.co.dajohnston.tasktrackerapi.users.repository.UsersRepository;

@Service
@RequiredArgsConstructor
public class UsersService {

  private final UsersRepository usersRepository;
  private final UserMapper userMapper;

  public User create(User user) {
    try {
      return userMapper.toDTO(usersRepository.save(userMapper.toEntity(user)));
    } catch (DuplicateKeyException e) {
      throw new DuplicateResourceException(e);
    }
  }

  public List<User> findAll() {
    return stream(usersRepository.findAll().spliterator(), false).map(userMapper::toDTO).toList();
  }
}
