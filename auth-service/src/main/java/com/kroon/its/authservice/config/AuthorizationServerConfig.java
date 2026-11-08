package com.kroon.its.authservice.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;

@Configuration
public class AuthorizationServerConfig {

    @Value("${auth.jwt.keystore.path}")
    private String keyStorePath;

    @Value("${auth.jwt.keystore.password}")
    private String keyStorePassword;

    @Value("${auth.jwt.keystore.alias}")
    private String keyAlias;

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {

        http.oauth2AuthorizationServer(auth ->
                        {

                            http.securityMatcher(auth.getEndpointsMatcher());

                            auth.oidc(Customizer.withDefaults());
                        }
                )
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                );

        return http.build();
    }


    @Bean
    public RegisteredClientRepository registeredClientRepository(
            org.springframework.jdbc.core.JdbcOperations jdbcOperations,
            PasswordEncoder passwordEncoder
    ) {
        JdbcRegisteredClientRepository repository = new JdbcRegisteredClientRepository(jdbcOperations);

        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("library-web")
                .clientSecret(passwordEncoder.encode("change-me-local"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/library-web")
                .scope("openid")
                .scope("profile")
                .scope("library.read")
                .clientSettings(
                        org.springframework.security.oauth2.server.authorization.settings
                                .ClientSettings.builder()
                                .requireProofKey(true)
                                .build()
                )
                .tokenSettings(
                        TokenSettings.builder()
                                .accessTokenTimeToLive(Duration.ofMinutes(15))
                                .refreshTokenTimeToLive(Duration.ofDays(7))
                                .reuseRefreshTokens(false)
                                .build()
                )
                .build();

        if (repository.findByClientId("library-web") == null) {
            repository.save(client);
        }

        return repository;
    }

    @Bean
    public OAuth2AuthorizationService authorizationService(JdbcOperations jdbcOperations,
                                                           RegisteredClientRepository registered
    ) {

        return new JdbcOAuth2AuthorizationService(jdbcOperations, registered);
    }

    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(JdbcOperations jdbcOperations,
                                                                         RegisteredClientRepository registered
    ) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcOperations, registered);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws Exception {

        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        char[] password = keyStorePassword.toCharArray();

        try (InputStream inputStream = Files.newInputStream(Path.of(keyStorePath))) {

            keyStore.load(inputStream, password);
        }

        Key key = keyStore.getKey(keyAlias, password);

        if (!(key instanceof RSAPrivateKey privateKey)) {
            throw new IllegalStateException(
                    "RSA private key not found in keystore: " + keyAlias
            );
        }

        Certificate certificate = keyStore.getCertificate(keyAlias);

        if (certificate == null || !(certificate.getPublicKey() instanceof RSAPublicKey publicKey)) {
            throw new IllegalStateException(
                    "RSA public key not found in keystore: " + keyAlias
            );
        }

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(keyAlias)
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .build();

        JWKSet jwkSet = new JWKSet(rsaKey);

        return (selector, context) -> selector.select(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("http://localhost:8081")
                .build();
    }
}
