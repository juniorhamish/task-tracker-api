package uk.co.dajohnston.houseworkapi.userinfo.auth0;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.dajohnston.houseworkapi.userinfo.model.UserInfoDTO;

@Mapper
public interface Auth0UserInfoMapper {
  @Mapping(
      target = "firstName",
      source = "user_metadata.firstName",
      defaultExpression = "java(auth0User.given_name())")
  @Mapping(
      target = "lastName",
      source = "user_metadata.lastName",
      defaultExpression = "java(auth0User.family_name())")
  @Mapping(
      target = "nickname",
      source = "user_metadata.nickname",
      defaultExpression = "java(auth0User.nickname())")
  @Mapping(
      target = "picture",
      source = "user_metadata.picture",
      defaultExpression = "java(auth0User.picture())")
  UserInfoDTO toUserInfoDTO(Auth0User auth0User);

  @InheritInverseConfiguration
  @Mapping(target = "nickname", ignore = true)
  @Mapping(target = "picture", ignore = true)
  Auth0User toAuth0User(UserInfoDTO userInfoDTO);
}
