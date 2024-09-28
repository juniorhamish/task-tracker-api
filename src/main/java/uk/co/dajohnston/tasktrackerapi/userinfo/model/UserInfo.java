package uk.co.dajohnston.tasktrackerapi.userinfo.model;

import jakarta.validation.constraints.NotNull;
import uk.co.dajohnston.tasktrackerapi.userinfo.AvatarSource;

public record UserInfo(
    @NotNull String email,
    @NotNull String firstName,
    @NotNull String lastName,
    @NotNull String nickname,
    @NotNull String picture,
    @NotNull String gravatarEmailAddress,
    @NotNull AvatarSource avatarImageSource) {}
