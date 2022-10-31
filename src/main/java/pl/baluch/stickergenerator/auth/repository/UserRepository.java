package pl.baluch.stickergenerator.auth.repository;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.reactivestreams.client.MongoClient;
import io.micronaut.data.mongodb.annotation.MongoRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import pl.baluch.stickergenerator.auth.model.User;
import reactor.core.publisher.Mono;

@Log4j2
@MongoRepository
public abstract class UserRepository implements ReactorCrudRepository<User, ObjectId> {

    @Inject
    private MongoClient client;
    @PostConstruct
    public void on(){
        //todo: read names from config
        Mono.from(client.getDatabase("authService")
                        .getCollection("user", User.class)
                        .createIndex(Indexes.text("email"), new IndexOptions().unique(true)))
                .subscribe(s -> log.info("Added `user` index: {}", s));
    }
}
