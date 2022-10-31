package pl.baluch.stickergenerator.auth.grpc.register;

import jakarta.inject.Singleton;
import pl.baluch.stickergenerator.api.RegisterRequest;
import pl.baluch.stickergenerator.auth.util.ValidatorBase;

@Singleton
class Validator extends ValidatorBase<RegisterRequest> {
    public Validator() {
        this.rules.put(r -> !r.getEmail().isBlank(), r -> "Email can not be blank.");
        this.rules.put(r -> !r.getPassword().isBlank(), r -> "Password can not be blank.");
        this.rules.put(r -> !r.getBusinessName().isBlank(), r -> "BusinessName can not be blank.");
        this.rules.put(r -> !r.getFirstName().isBlank(), r -> "FirstName can not be blank.");
        this.rules.put(r -> !r.getLastName().isBlank(), r -> "LastName can not be blank.");
    }
}
