package uk.co.dajohnston.houseworkapi.userinfo.auth0;

public record Auth0User(String email, String name, String nickname, String picture, Auth0UserMetaData user_metadata) {}
