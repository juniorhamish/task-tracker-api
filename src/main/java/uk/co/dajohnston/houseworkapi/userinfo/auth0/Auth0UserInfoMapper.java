package uk.co.dajohnston.houseworkapi.userinfo.auth0;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.dajohnston.houseworkapi.userinfo.model.UserInfoDTO;

@Mapper
public interface Auth0UserInfoMapper {
  @Mapping(target = "firstName", source = "user_metadata.first_name")
  @Mapping(target = "lastName", source = "user_metadata.last_name")
  @Mapping(
      target = "nickname",
      source = "user_metadata.nickname",
      defaultExpression = "java(auth0User.nickname())")
  UserInfoDTO toUserInfoDTO(Auth0User auth0User);
}
