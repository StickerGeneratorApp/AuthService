package pl.baluch.stickergenerator.auth.model;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotBlank;

@MappedEntity
@Getter
@Setter
public class Business {
    @Id
    @GeneratedValue
    private ObjectId id;
    @NotBlank
    private String name;
    @NonNull
    @Relation(value = Relation.Kind.ONE_TO_ONE)
    private User owner;

    public Business(String name, User owner){
        this.name = name;
        this.owner = owner;
    }

}