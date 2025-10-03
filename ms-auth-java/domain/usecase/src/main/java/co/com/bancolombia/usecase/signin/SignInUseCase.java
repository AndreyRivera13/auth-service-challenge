// src/main/java/co/com/bancolombia/usecase/signin/SignInUseCase.java
package co.com.bancolombia.usecase.signin;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.session.Session;
import co.com.bancolombia.model.contextdata.ContextData;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.model.session.gateways.SessionRepository;
import co.com.bancolombia.model.exception.DomainError;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class SignInUseCase {
    private static final Logger log = LoggerFactory.getLogger(SignInUseCase.class);
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public Mono<Session> execute(User user, ContextData context) {
        log.debug("Iniciando SignIn para email={}, context={}", user != null ? user.getEmail() : null, context);
        if (user == null || user.getEmail() == null || user.getPassword() == null) {
            log.warn("Request mal formado: {}", user);
            return Mono.error(new DomainError("MALFORMED_REQUEST", "Request inválido", null, context));
        }
        return userRepository.findByEmail(user.getEmail())
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Usuario no encontrado: {}", user.getEmail());
                    return Mono.error(new DomainError("USER_NOT_FOUND", "Usuario no existe", null, context));
                }))
                .flatMap(u -> {
                    if (!u.getPassword().equals(user.getPassword())) {
                        log.warn("Credenciales inválidas para email={}", user.getEmail());
                        return Mono.error(new DomainError("INVALID_CREDENTIALS", "Credenciales inválidas", null, context));
                    }
                    String sessionId = UUID.randomUUID().toString();
                    Session session = new Session(sessionId, user.getEmail());
                    log.info("Signin exitoso email={}, sessionId={}", user.getEmail(), sessionId);
                    return sessionRepository.save(session).thenReturn(session);
                });
    }
}
