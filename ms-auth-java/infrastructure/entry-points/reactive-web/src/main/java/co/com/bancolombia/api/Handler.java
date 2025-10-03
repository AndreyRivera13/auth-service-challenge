package co.com.bancolombia.api;

import co.com.bancolombia.filter.HeadersValidation;
import co.com.bancolombia.model.contextdata.ContextData;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.usecase.signin.SignInUseCase;
import co.com.bancolombia.usecase.signup.SignUpUseCase;
import co.com.bancolombia.util.request.SignInRequest;
import co.com.bancolombia.util.request.SignUpRequest;
import co.com.bancolombia.util.response.SignInResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
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
    private static final String MESSAGE_ID_HEADER = "message-id";
    private static final String X_REQUEST_ID_HEADER = "x-request-id";

    public Mono<ServerResponse> signupPOSTUseCase(ServerRequest serverRequest) {
        log.debug("Iniciando proceso de signup");
        return headersValidation.validateHeaders(serverRequest)
                .then(serverRequest.bodyToMono(SignUpRequest.class)
                        .flatMap(request -> {
                            User user = mapToUserSignUpRequest(request);
                            ContextData contextData = ContextData.builder()
                                    .messageId(serverRequest.headers().firstHeader(MESSAGE_ID_HEADER))
                                    .consumerCode(serverRequest.headers().firstHeader(X_REQUEST_ID_HEADER))
                                    .build();
                            return signUpUseCase.execute(user, contextData);
                        })
                )
                .then(ServerResponse.created(serverRequest.uri())
                        .header(MESSAGE_ID_HEADER, serverRequest.headers().firstHeader(MESSAGE_ID_HEADER))
                        .header(X_REQUEST_ID_HEADER, serverRequest.headers().firstHeader(X_REQUEST_ID_HEADER))
                        .build());
    }

    public Mono<ServerResponse> signinPOSTUseCase(ServerRequest serverRequest) {
        log.debug("Iniciando proceso de signin");
        return headersValidation.validateHeaders(serverRequest)
                .then(serverRequest.bodyToMono(SignInRequest.class)
                        .flatMap(request -> {
                            User user = mapToUserSignInRequest(request);
                            ContextData context = ContextData.builder()
                                    .messageId(serverRequest.headers().firstHeader(MESSAGE_ID_HEADER))
                                    .consumerCode(serverRequest.headers().firstHeader(X_REQUEST_ID_HEADER))
                                    .build();
                            return signInUseCase.execute(user, context);
                        }))
                .flatMap(session -> {
                    SignInResponse body = new SignInResponse(session.getSessionId());
                    return ServerResponse.ok()
                            .header(MESSAGE_ID_HEADER, serverRequest.headers().firstHeader(MESSAGE_ID_HEADER))
                            .header(X_REQUEST_ID_HEADER, serverRequest.headers().firstHeader(X_REQUEST_ID_HEADER))
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(body);
                });
    }


    private User mapToUserSignUpRequest(SignUpRequest request) {
        return User.builder().
                email(request.getEmail()).
                password(request.getPassword()).
                name(request.getName()).
                build();
    }

    private User mapToUserSignInRequest(SignInRequest request) {
        return User.builder().
                email(request.getEmail()).
                password(request.getPassword()).
                build();
    }
}