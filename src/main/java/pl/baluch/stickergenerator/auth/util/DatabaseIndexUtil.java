package pl.baluch.stickergenerator.auth.util;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.micronaut.context.annotation.Value;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.model.naming.NamingStrategy;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import reactor.core.publisher.Mono;

@Singleton
public class DatabaseIndexUtil {
    @Inject
    private MongoClient client;
    @Value("${mongodb.database.name:}")
    private String databaseName;

    @SneakyThrows
    public <T> MongoCollection<T> getCollection(Class<T> modelClass) {
        NamingStrategy namingStrategy = modelClass.getAnnotation(MappedEntity.class)
                .namingStrategy().getDeclaredConstructor().newInstance();
        return client.getDatabase(databaseName)
                .getCollection(namingStrategy.mappedName(modelClass.getSimpleName()), modelClass);
    }

    public Mono<String> createUniqueIndex(Class<?> typeClass, String columnName) {
        return Mono.from(getCollection(typeClass)
                .createIndex(new BsonDocument(columnName, new BsonInt32(1)), new IndexOptions().unique(true)));
    }
}
