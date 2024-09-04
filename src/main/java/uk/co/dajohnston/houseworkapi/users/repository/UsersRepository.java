package uk.co.dajohnston.houseworkapi.users.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends CrudRepository<UserEntity, String> {

  UserEntity findByEmailAddress(String emailAddress);
}