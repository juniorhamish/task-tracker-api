package uk.co.dajohnston.tasktrackerapi.userinfo.auth0;

import uk.co.dajohnston.tasktrackerapi.userinfo.AvatarSource;

public record Auth0UserMetaData(
    String firstName,
    String lastName,
    String nickname,
    String picture,
    String gravatarEmailAddress,
    AvatarSource avatarImageSource) {}
