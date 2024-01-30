package uk.co.dajohnston.houseworkapi.users;

import static java.util.Collections.singletonList;
import static java.util.stream.StreamSupport.stream;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
public class UsersService {

  private final UsersRepository usersRepository;

  public User create(@Valid User user) {
    return usersRepository.save(user);
  }

  public List<User> findAll() {
    return stream(usersRepository.findAll()
                                 .spliterator(), false).toList();
  }

  public List<User> findScopedUsers(String emailAddress) {
    return singletonList(usersRepository.findByEmailAddress(emailAddress));
  }
}
