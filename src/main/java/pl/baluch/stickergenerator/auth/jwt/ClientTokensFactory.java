package pl.baluch.stickergenerator.auth.jwt;

import io.micronaut.context.annotation.Value;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.jwt.generator.JwtTokenGenerator;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import pl.baluch.stickergenerator.api.ClientTokens;
import pl.baluch.stickergenerator.auth.model.User;

@Singleton
public class ClientTokensFactory {
    @Inject
    private JwtTokenGenerator jwtTokenGenerator;

    @Value("${micronaut.security.token.jwt.generator.access-token.expiration:8640}")
    private int accessTokenExpiration;
    @Value("${micronaut.security.token.jwt.generator.refresh-token.expiration:259200}")
    private int refreshTokenExpiration;

    public ClientTokens createTokens(User user){
        Authentication authentication = Authentication.build(user.getEmail());
        return ClientTokens.newBuilder()
                .setAccessToken(jwtTokenGenerator.generateToken(authentication, accessTokenExpiration).orElseThrow(() -> new RuntimeException("Can't generate access token")))
                .setRefreshToken(jwtTokenGenerator.generateToken(authentication, refreshTokenExpiration).orElseThrow(() -> new RuntimeException("Can't generate refresh token")))
                .build();
    }

}
