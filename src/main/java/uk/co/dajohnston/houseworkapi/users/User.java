package uk.co.dajohnston.houseworkapi.users;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record User(
    String firstName, String lastName, @Indexed(unique = true) String emailAddress) {}
