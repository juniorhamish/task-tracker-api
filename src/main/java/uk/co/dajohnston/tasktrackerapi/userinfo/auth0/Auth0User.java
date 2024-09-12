package uk.co.dajohnston.tasktrackerapi.userinfo.auth0;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(NON_NULL)
public record Auth0User(
    String given_name,
    String family_name,
    String email,
    String nickname,
    String picture,
    Auth0UserMetaData user_metadata) {}
