package uk.co.dajohnston.houseworkapi.userinfo.auth0;

public record Auth0User(
    String given_name,
    String family_name,
    String email,
    String nickname,
    String picture,
    Auth0UserMetaData user_metadata) {}
