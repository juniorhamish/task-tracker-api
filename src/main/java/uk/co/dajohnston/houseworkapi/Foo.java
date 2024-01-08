package uk.co.dajohnston.houseworkapi;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
public class Foo {
  @Id
  private String id;
  private String bar;
  @DBRef
  private Task task;
}
