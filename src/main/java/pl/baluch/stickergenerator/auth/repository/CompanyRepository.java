package pl.baluch.stickergenerator.auth.repository;

import io.micronaut.data.mongodb.annotation.MongoRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import pl.baluch.stickergenerator.auth.model.Company;
import pl.baluch.stickergenerator.auth.util.DatabaseIndexUtil;

@Log4j2
@MongoRepository
@Singleton
public abstract class CompanyRepository implements ReactorCrudRepository<Company, ObjectId> {

    @Inject
    private DatabaseIndexUtil databaseUtils;
    @PostConstruct
    public void on(){
        databaseUtils.createUniqueIndex(Company.class, "name")
                .subscribe(s -> log.info("Added `company` index: {}", s));
    }
}
