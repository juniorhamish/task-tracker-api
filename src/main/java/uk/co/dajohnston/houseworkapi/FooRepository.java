package uk.co.dajohnston.houseworkapi;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface FooRepository extends MongoRepository<Foo, String> {

}
