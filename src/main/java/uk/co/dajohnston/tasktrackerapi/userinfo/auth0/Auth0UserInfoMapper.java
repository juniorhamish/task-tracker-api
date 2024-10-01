package uk.co.dajohnston.tasktrackerapi.userinfo.auth0;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.dajohnston.tasktrackerapi.userinfo.model.PartialUserInfo;
import uk.co.dajohnston.tasktrackerapi.userinfo.model.UserInfo;

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
  @Mapping(
      target = "gravatarEmailAddress",
      source = "user_metadata.gravatarEmailAddress",
      defaultExpression = "java(auth0User.email())")
  @Mapping(
      target = "avatarImageSource",
      source = "user_metadata.avatarImageSource",
      defaultExpression = "java(AvatarSource.GRAVATAR)")
  UserInfo toUserInfo(Auth0User auth0User);

  @Mapping(target = "user_metadata.avatarImageSource", source = "avatarImageSource")
  @Mapping(target = "user_metadata.gravatarEmailAddress", source = "gravatarEmailAddress")
  @Mapping(target = "user_metadata.picture", source = "picture")
  @Mapping(target = "user_metadata.nickname", source = "nickname")
  @Mapping(target = "user_metadata.firstName", source = "firstName")
  @Mapping(target = "user_metadata.lastName", source = "lastName")
  @Mapping(target = "email", ignore = true)
  @Mapping(target = "nickname", ignore = true)
  @Mapping(target = "picture", ignore = true)
  @Mapping(target = "given_name", ignore = true)
  @Mapping(target = "family_name", ignore = true)
  Auth0User toAuth0User(PartialUserInfo userInfo);
}
