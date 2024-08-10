package uk.co.dajohnston.houseworkapi.users;

import static java.util.Collections.singletonList;
import static java.util.stream.StreamSupport.stream;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import uk.co.dajohnston.houseworkapi.exceptions.DuplicateResourceException;

@Service
@RequiredArgsConstructor
public class UsersService {

  private final UsersRepository usersRepository;
  private final UserMapper userMapper;

  public UserDTO create(UserDTO user) {
    try {
      return userMapper.toDTO(usersRepository.save(userMapper.toEntity(user)));
    } catch (DuplicateKeyException e) {
      throw new DuplicateResourceException(e);
    }
  }

  public List<UserDTO> findAll() {
    return stream(usersRepository.findAll().spliterator(), false).map(userMapper::toDTO).toList();
  }

  public List<UserDTO> findScopedUsers(String emailAddress) {
    return singletonList(userMapper.toDTO(usersRepository.findByEmailAddress(emailAddress)));
  }
}
