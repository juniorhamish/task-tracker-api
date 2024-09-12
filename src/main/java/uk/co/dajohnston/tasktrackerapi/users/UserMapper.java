package uk.co.dajohnston.tasktrackerapi.users;

import org.mapstruct.Mapper;
import uk.co.dajohnston.tasktrackerapi.users.controller.UserDTO;
import uk.co.dajohnston.tasktrackerapi.users.repository.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserDTO toDTO(UserEntity user);

  UserEntity toEntity(UserDTO userDTO);
}
