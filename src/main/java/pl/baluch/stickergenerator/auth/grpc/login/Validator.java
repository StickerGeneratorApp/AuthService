package pl.baluch.stickergenerator.auth.grpc.login;

import jakarta.inject.Singleton;
import pl.baluch.stickergenerator.api.LoginRequest;
import pl.baluch.stickergenerator.auth.util.ValidatorBase;

@Singleton
class Validator extends ValidatorBase<LoginRequest> {
    public Validator() {
        this.rules.put(r -> !r.getEmail().isBlank(), r -> "Email can not be blank.");
        this.rules.put(r -> !r.getPassword().isBlank(), r -> "Password can not be blank.");
    }
}
