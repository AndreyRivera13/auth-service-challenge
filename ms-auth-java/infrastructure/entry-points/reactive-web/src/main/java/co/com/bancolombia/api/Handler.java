package co.com.bancolombia.api;

import co.com.bancolombia.filter.HeadersValidation;
import co.com.bancolombia.model.contextdata.ContextData;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.usecase.signin.SignInUseCase;
import co.com.bancolombia.usecase.signup.SignUpUseCase;
import co.com.bancolombia.util.request.SignInRequest;
import co.com.bancolombia.util.request.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
    private final HeadersValidation headersValidation;
    private final SignInUseCase signInUseCase;
    private final SignUpUseCase signUpUseCase;
    private static final Logger log = LoggerFactory.getLogger(Handler.class);


    public Mono<ServerResponse> signupPOSTUseCase(ServerRequest serverRequest) {
        log.debug("Iniciando proceso de signup");
        return headersValidation.validateHeaders(serverRequest)
                .then(serverRequest.bodyToMono(SignUpRequest.class)
                        .flatMap(request -> {
                            log.info("Datos recibidos para signup: {}", request);
                            User user = mapToUserSignUpRequest(request);
                            String messageId = serverRequest.headers().firstHeader("message-id");
                            String consumerCode = serverRequest.headers().firstHeader("consumer-code");
                            ContextData contextData = ContextData.builder()
                                    .messageId(messageId)
                                    .consumerCode(consumerCode)
                                    .build();
                            return signUpUseCase.execute(user, contextData);
                        })
                )
                .flatMap(result -> {
                    log.info("Signup exitoso para usuario: {}", result);
                    return ServerResponse.ok().bodyValue(result);
                });
    }

    /*
    public Mono<ServerResponse> signinPOSTUseCase(ServerRequest serverRequest) {
        return headersValidation.validateHeaders(serverRequest)
                .flatMap(user -> serverRequest.bodyToMono(SignInRequest.class)
                        .flatMap(request -> {
                            String messageId = serverRequest.headers().firstHeader("message-id-header");
                            return signInUseCase.execute(user, messageId);
                        })
                )
                .flatMap(result -> ServerResponse.ok().bodyValue(result));
    }
    */

    private User mapToUserSignUpRequest(SignUpRequest request) {

        return User.builder().
                name( request.getName()).
                email(request.getEmail()).
                password(request.getPassword()).
                build();
    }
    private User mapToUserSignInRequest(SignInRequest request) {
        return User.builder().
                name( request.getUsername()).
                password(request.getPassword()).
                build();
    }
}