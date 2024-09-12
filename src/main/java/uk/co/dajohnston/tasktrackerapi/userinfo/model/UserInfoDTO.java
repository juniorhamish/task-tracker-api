package uk.co.dajohnston.tasktrackerapi.userinfo.model;

public record UserInfoDTO(
    String email, String firstName, String lastName, String nickname, String picture) {}
