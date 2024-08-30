package uk.co.dajohnston.houseworkapi.users.repository;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record UserEntity(
    String firstName, String lastName, @Indexed(unique = true) String emailAddress) {}
