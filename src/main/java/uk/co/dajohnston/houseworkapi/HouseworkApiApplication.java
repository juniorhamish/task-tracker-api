package uk.co.dajohnston.houseworkapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class HouseworkApiApplication {

  public static void main(String[] args) {
    System.out.println("Dave");
    SpringApplication.run(HouseworkApiApplication.class, args);
  }
}
