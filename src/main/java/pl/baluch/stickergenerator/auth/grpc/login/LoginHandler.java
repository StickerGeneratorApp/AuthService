package pl.baluch.stickergenerator.auth.grpc.login;

import io.micronaut.security.authentication.AuthenticationException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCrypt;
import pl.baluch.stickergenerator.api.ClientTokens;
import pl.baluch.stickergenerator.api.LoginReply;
import pl.baluch.stickergenerator.api.LoginRequest;
import pl.baluch.stickergenerator.auth.exceptions.ValidationException;
import pl.baluch.stickergenerator.auth.jwt.ClientTokensFactory;
import pl.baluch.stickergenerator.auth.model.User;
import pl.baluch.stickergenerator.auth.repository.UserRepository;
import reactor.core.publisher.Mono;

@Log4j2
@Singleton
public class LoginHandler {
    @Inject
    private Validator validator;
    @Inject
    private UserRepository userRepository;
    @Inject
    private ClientTokensFactory clientTokensFactory;
    public Mono<LoginReply> login(LoginRequest request) {
        var validationResult = validator.validate(request);
        if (validationResult.hasErrors()) {
            return Mono.error(new ValidationException(validationResult.collectErrors()));
        }

        return doLogin(request)
                .doOnNext(user -> log.info("Successfully logged in user: {}", request.getEmail()))
                .map(clientTokensFactory::createTokens)
                .transform(this::buildResponse);
    }

    private Mono<User> doLogin(LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .switchIfEmpty(Mono.error(new AuthenticationException("Invalid username or password")))
                .doOnNext(user -> {
                    if(!BCrypt.checkpw(request.getPassword(), user.getPassword())){
                        log.debug("Invalid password"); //todo
                        throw new AuthenticationException("Invalid username or password");
                    }
                });
    }

    private Mono<LoginReply> buildResponse(Mono<ClientTokens> clientTokensMono) {
        return clientTokensMono.map(tokens -> LoginReply.newBuilder().setTokens(tokens).build());
    }
}
