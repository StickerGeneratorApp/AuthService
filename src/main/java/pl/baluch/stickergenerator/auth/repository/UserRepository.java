package pl.baluch.stickergenerator.auth.repository;

import io.micronaut.data.mongodb.annotation.MongoRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import pl.baluch.stickergenerator.auth.model.User;
import pl.baluch.stickergenerator.auth.util.DatabaseIndexUtil;

@Log4j2
@MongoRepository
public abstract class UserRepository implements ReactorCrudRepository<User, ObjectId> {

    @Inject
    private DatabaseIndexUtil databaseUtils;
    @PostConstruct
    public void on() {
        databaseUtils.createUniqueIndex(User.class, "email")
                .subscribe(s -> log.info("Added `user` index: {}", s));
    }
}
