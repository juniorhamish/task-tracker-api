package uk.co.dajohnston.houseworkapi.users;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService {

  private final UsersRepository usersRepository;

  public User create(User user) {
    usersRepository.save(user);
    return null;
  }

  public List<User> findAll() {
    List<User> result = new ArrayList<>();
    usersRepository.findAll().forEach(result::add);
    return result;
  }
}
