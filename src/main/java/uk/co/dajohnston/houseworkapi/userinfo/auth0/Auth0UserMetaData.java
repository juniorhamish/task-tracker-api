package uk.co.dajohnston.houseworkapi.userinfo.auth0;

public record Auth0UserMetaData(
    String firstName, String lastName, String nickname, String picture) {}
