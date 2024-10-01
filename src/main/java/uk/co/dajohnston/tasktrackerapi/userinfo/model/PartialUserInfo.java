package uk.co.dajohnston.tasktrackerapi.userinfo.model;

import uk.co.dajohnston.tasktrackerapi.userinfo.AvatarSource;

public record PartialUserInfo(
    String firstName,
    String lastName,
    String nickname,
    String picture,
    String gravatarEmailAddress,
    AvatarSource avatarImageSource) {}
