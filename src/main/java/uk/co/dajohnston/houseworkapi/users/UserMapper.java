package uk.co.dajohnston.houseworkapi.users;

import org.mapstruct.Mapper;
import uk.co.dajohnston.houseworkapi.users.controller.UserDTO;
import uk.co.dajohnston.houseworkapi.users.repository.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserDTO toDTO(UserEntity user);

  UserEntity toEntity(UserDTO userDTO);
}
