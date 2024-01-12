package uk.co.dajohnston.houseworkapi;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class IntegrationTest {

  @Test
  void test() {
    assertThat(5, is(5));
  }
}
