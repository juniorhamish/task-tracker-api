package uk.co.dajohnston.houseworkapi.users;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record User(@Size(max = 1, message = "Size.user.firstName") String firstName, String lastName,
                   @NotNull(message = "NotNull.user.emailAddress") String emailAddress) {

}
