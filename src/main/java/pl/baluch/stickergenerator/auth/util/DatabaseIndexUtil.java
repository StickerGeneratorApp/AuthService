package pl.baluch.stickergenerator.auth.util;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.micronaut.context.annotation.Value;
import io.micronaut.data.model.naming.NamingStrategies;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

@Singleton
public class DatabaseIndexUtil {
    @Inject
    private MongoClient client;
    @Value("${mongodb.database.name:}")
    private String databaseName;

    public <T> MongoCollection<T> getCollection(Class<T> modelClass) {
        return client.getDatabase(databaseName)
                .getCollection(NamingStrategies.UnderScoreSeparatedLowerCase.DEFAULT.mappedName(modelClass.getSimpleName()), modelClass);
    }

    public Mono<String> createUniqueIndex(Class<?> typeClass, String columnName) {
        return Mono.from(getCollection(typeClass)
                .createIndex(Indexes.text(columnName), new IndexOptions().unique(true)));
    }
}
