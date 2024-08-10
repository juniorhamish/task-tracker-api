package uk.co.dajohnston.houseworkapi.users;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends CrudRepository<User, String> {

  User findByEmailAddress(String emailAddress);

  boolean existsByEmailAddress(String emailAddress);
}
