package uk.co.dajohnston.tasktrackerapi.userinfo.auth0;

public record Auth0UserMetaData(
    String firstName, String lastName, String nickname, String picture) {}
