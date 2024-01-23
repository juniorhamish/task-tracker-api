package uk.co.dajohnston.houseworkapi.users;

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
}
