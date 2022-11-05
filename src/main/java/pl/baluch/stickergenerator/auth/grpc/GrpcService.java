package pl.baluch.stickergenerator.auth.grpc;

import io.grpc.stub.StreamObserver;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;
import pl.baluch.stickergenerator.api.*;
import pl.baluch.stickergenerator.auth.grpc.login.LoginHandler;
import pl.baluch.stickergenerator.auth.grpc.register.RegisterHandler;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Log4j2
@Singleton
public class GrpcService extends AuthServiceGrpc.AuthServiceImplBase {
    @Inject
    private RegisterHandler registerHandler;
    @Inject
    private LoginHandler loginHandler;
    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterReply> responseObserver) {
        subscribe(request, registerHandler::register, responseObserver);
    }

    @Override
    public void login(LoginRequest request, StreamObserver<LoginReply> responseObserver) {
        subscribe(request, loginHandler::login, responseObserver);
    }

    private <T, R> void subscribe(T request, Function<T, Mono<R>> handler, StreamObserver<R> responseObserver) {
        handler.apply(request).subscribe(responseObserver::onNext, t -> {
            log.info("Exception thrown during processing {}: {}", request.getClass().getSimpleName(), t);
            responseObserver.onError(t);
        }, responseObserver::onCompleted);
    }
}
