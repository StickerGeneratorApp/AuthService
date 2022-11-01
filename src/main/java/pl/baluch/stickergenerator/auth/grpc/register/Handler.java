package pl.baluch.stickergenerator.auth.grpc.register;

import io.grpc.stub.StreamObserver;
import io.micronaut.context.annotation.Value;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.jwt.generator.JwtTokenGenerator;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;
import pl.baluch.stickergenerator.api.AuthServiceGrpc;
import pl.baluch.stickergenerator.api.ClientTokens;
import pl.baluch.stickergenerator.api.RegisterReply;
import pl.baluch.stickergenerator.api.RegisterRequest;
import pl.baluch.stickergenerator.auth.exceptions.ValidationException;
import pl.baluch.stickergenerator.auth.model.Business;
import pl.baluch.stickergenerator.auth.model.User;
import pl.baluch.stickergenerator.auth.repository.BusinessRepository;
import pl.baluch.stickergenerator.auth.repository.UserRepository;
import reactor.core.publisher.Mono;

@Log4j2
@Singleton
class Handler extends AuthServiceGrpc.AuthServiceImplBase {
    @Inject
    private Validator validator;
    @Inject
    private BusinessRepository businessRepository;
    @Inject
    private UserRepository userRepository;
    @Inject
    private JwtTokenGenerator jwtTokenGenerator;

    @Value("${micronaut.security.token.jwt.generator.access-token.expiration:8640}")
    private int accessTokenExpiration;
    @Value("${micronaut.security.token.jwt.generator.refresh-token.expiration:259200}")
    private int refreshTokenExpiration;

    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterReply> responseObserver) {
        //todo: replace it with reactive syntax
        var validationResult = validator.validate(request);
        if (validationResult.hasErrors()) {
            log.info("Error when processing register request: {}", validationResult.errors());
            responseObserver.onError(new ValidationException(validationResult.collectErrors()));
            return;
        }

        doRegister(request)
                .transform(this::generateTokens)
                .transform(this::buildResponse)
                .subscribe(responseObserver::onNext, t -> {
                    log.info("Error when processing register request: {}", t.getMessage());
                    responseObserver.onError(t);
                }, responseObserver::onCompleted);
    }

    private Mono<User> doRegister(RegisterRequest request) {
        return userRepository
                .save(new User(request.getFirstName(), request.getLastName(), request.getEmail(), request.getPassword()))
                .flatMap(user -> businessRepository.save(new Business(request.getBusinessName(), user)))
                .map(Business::getOwner);
    }

    private Mono<ClientTokens> generateTokens(Mono<User> userMono) {
        return userMono.map(user -> Authentication.build(user.getEmail()))
                .map(authentication -> ClientTokens.newBuilder()
                        .setAccessToken(jwtTokenGenerator.generateToken(authentication, accessTokenExpiration).orElseThrow(() -> new RuntimeException("Can't generate access token")))
                        .setRefreshToken(jwtTokenGenerator.generateToken(authentication, refreshTokenExpiration).orElseThrow(() -> new RuntimeException("Can't generate refresh token")))
                        .build());
    }

    private Mono<RegisterReply> buildResponse(Mono<ClientTokens> clientTokensMono) {
        return clientTokensMono.map(clientTokens -> RegisterReply.newBuilder().setTokens(clientTokens).build());
    }
}
