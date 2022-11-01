package pl.baluch.stickergenerator.auth.util;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.micronaut.context.annotation.Value;
import io.micronaut.data.model.naming.NamingStrategies;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class DatabaseUtils {
    @Inject
    private MongoClient client;
    @Value("${mongodb.database.name:}")
    private String databaseName;

    public <T> MongoCollection<T> getCollection(Class<T> modelClass) {
        return client.getDatabase(databaseName)
                .getCollection(NamingStrategies.UnderScoreSeparatedLowerCase.DEFAULT.mappedName(modelClass.getSimpleName()), modelClass);
    }
}
