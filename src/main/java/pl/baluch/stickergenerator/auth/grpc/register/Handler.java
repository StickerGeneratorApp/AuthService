package pl.baluch.stickergenerator.auth.grpc.register;

import io.grpc.stub.StreamObserver;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import pl.baluch.stickergenerator.api.*;
import pl.baluch.stickergenerator.auth.exceptions.ValidationException;

@Log4j2
@Singleton
class Handler extends AuthServiceGrpc.AuthServiceImplBase {
    private final Validator validator;

    public Handler(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterReply> responseObserver) {
        var validationResult = validator.validate(request);
        if(validationResult.hasErrors()){
            log.log(Level.WARN, "Register Request with errors: {}", validationResult.errors());
            responseObserver.onError(new ValidationException(validationResult.collectErrors()));
            return;
        }
        responseObserver.onNext(doRegister(request));
        responseObserver.onCompleted();
    }

    private RegisterReply doRegister(RegisterRequest request) {
        return RegisterReply.newBuilder().build();
    }
}
