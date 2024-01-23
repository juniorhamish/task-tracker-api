package uk.co.dajohnston.houseworkapi.users;

import static java.util.stream.StreamSupport.stream;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService {

  private final UsersRepository usersRepository;

  public User create(User user) {
    return usersRepository.save(user);
  }

  public List<User> findAll() {
    return stream(usersRepository.findAll()
                                 .spliterator(), false).toList();
  }
}
