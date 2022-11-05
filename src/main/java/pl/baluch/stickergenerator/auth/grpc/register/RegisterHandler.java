package pl.baluch.stickergenerator.auth.grpc.register;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCrypt;
import pl.baluch.stickergenerator.api.ClientTokens;
import pl.baluch.stickergenerator.api.RegisterReply;
import pl.baluch.stickergenerator.api.RegisterRequest;
import pl.baluch.stickergenerator.auth.exceptions.ValidationException;
import pl.baluch.stickergenerator.auth.jwt.ClientTokensFactory;
import pl.baluch.stickergenerator.auth.model.Company;
import pl.baluch.stickergenerator.auth.model.User;
import pl.baluch.stickergenerator.auth.repository.CompanyRepository;
import pl.baluch.stickergenerator.auth.repository.UserRepository;
import reactor.core.publisher.Mono;

@Log4j2
@Singleton
public class RegisterHandler {
    @Inject
    private Validator validator;
    @Inject
    private CompanyRepository companyRepository;
    @Inject
    private UserRepository userRepository;
    @Inject
    private ClientTokensFactory clientTokensFactory;

    public Mono<RegisterReply> register(RegisterRequest request) {
        var validationResult = validator.validate(request);
        if (validationResult.hasErrors()) {
            return Mono.error(new ValidationException(validationResult.collectErrors()));
        }

        return doRegister(request)
                .doOnNext(user -> log.info("Successfully registred user: {} {} ({})", request.getFirstName(), request.getLastName(), request.getEmail()))
                .map(clientTokensFactory::createTokens)
                .transform(this::buildResponse);
    }

    private Mono<User> doRegister(RegisterRequest request) {
        String salt = BCrypt.gensalt();
        String passwordHash = BCrypt.hashpw(request.getPassword(), salt);
        return userRepository
                .save(new User(request.getFirstName(), request.getLastName(), request.getEmail(), passwordHash))
                .flatMap(user -> companyRepository.save(new Company(request.getCompanyName(), user)))
                .map(Company::getOwner);
    }

    private Mono<RegisterReply> buildResponse(Mono<ClientTokens> clientTokensMono) {
        return clientTokensMono.map(clientTokens -> RegisterReply.newBuilder().setTokens(clientTokens).build());
    }
}
