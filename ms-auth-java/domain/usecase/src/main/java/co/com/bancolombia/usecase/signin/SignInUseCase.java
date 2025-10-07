package co.com.bancolombia.usecase.signin;

import co.com.bancolombia.model.shared.common.exception.model.CodeMessage;
import co.com.bancolombia.model.signin.gateways.SignInRepository;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.session.Session;
import co.com.bancolombia.model.shared.cqrs.ContextData;
import co.com.bancolombia.model.shared.common.exception.DomainError;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class SignInUseCase {
    private static final Logger log = LoggerFactory.getLogger(SignInUseCase.class);
    private final SignInRepository signInRepository;
    private final UserRepository userRepository;

    public Mono<Session> execute(User user, ContextData context) {
        log.debug("Iniciando SignIn para email={}, context={}", user != null ? user.getEmail() : null, context);
        if (user == null || user.getEmail() == null || user.getPassword() == null) {
            log.warn("Request mal formado: {}", user.getEmail());
            return Mono.error(new DomainError("MALFORMED_REQUEST", "Request inválido", null, context));
        }
        return userRepository.findByEmail(user.getEmail())
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Usuario no encontrado: {}", user.getEmail());
                    return Mono.error(new DomainError(CodeMessage.USER_NOT_FOUND_CODE,CodeMessage.USER_NOT_EXISTS_MESSAGE, null, context));
                }))
                .flatMap(u -> {
                    if (!u.getPassword().equals(user.getPassword())) {
                        log.warn("Credenciales inválidas para email={}", user.getEmail());
                        return Mono.error(new DomainError(CodeMessage.INVALID_CREDENTIALS_CODE, CodeMessage.INVALID_CREDENTIALS_MESSAGE, null, context));
                    }
                    String sessionId = UUID.randomUUID().toString();
                    Session session = new Session(sessionId, user.getEmail());
                    log.info("Signin exitoso email={}, sessionId={}", user.getEmail(), sessionId);
                    return signInRepository.save(session).thenReturn(session);
                });
    }
}
