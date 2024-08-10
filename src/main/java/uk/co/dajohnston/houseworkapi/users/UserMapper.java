package uk.co.dajohnston.houseworkapi.users;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserDTO toDTO(UserEntity user);

  UserEntity toEntity(UserDTO userDTO);
}
