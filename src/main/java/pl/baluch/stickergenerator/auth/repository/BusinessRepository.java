package pl.baluch.stickergenerator.auth.repository;

import io.micronaut.data.mongodb.annotation.MongoRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import pl.baluch.stickergenerator.auth.model.Business;
import pl.baluch.stickergenerator.auth.util.DatabaseIndexUtil;

@Log4j2
@MongoRepository
public abstract class BusinessRepository implements ReactorCrudRepository<Business, ObjectId> {

    @Inject
    private DatabaseIndexUtil databaseUtils;
    @PostConstruct
    public void on(){
        databaseUtils.createUniqueIndex(Business.class, "name")
                .subscribe(s -> log.info("Added `business` index: {}", s));
    }
}
