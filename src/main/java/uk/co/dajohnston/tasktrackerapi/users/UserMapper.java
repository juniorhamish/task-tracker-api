package uk.co.dajohnston.tasktrackerapi.users;

import org.mapstruct.Mapper;
import uk.co.dajohnston.tasktrackerapi.users.controller.User;
import uk.co.dajohnston.tasktrackerapi.users.repository.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
  User toDTO(UserEntity user);

  UserEntity toEntity(User user);
}
