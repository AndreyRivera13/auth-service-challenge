// src/main/java/co/com/bancolombia/usecase/signin/SignInUseCase.java
package co.com.bancolombia.usecase.signin;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.session.Session;
import co.com.bancolombia.model.contextdata.ContextData;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.model.session.gateways.SessionRepository;
import co.com.bancolombia.model.exception.DomainError;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class SignInUseCase {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public Mono<Session> execute(User user, ContextData context) {
        if (user == null || user.getEmail() == null || user.getPassword() == null) {
            return Mono.error(new DomainError("MALFORMED_REQUEST", "Request inválido", null, context));
        }
        return userRepository.findByEmail(user.getEmail())
                .switchIfEmpty(Mono.error(new DomainError("USER_NOT_FOUND", "Usuario no existe", null, context)))
                .flatMap(u -> {
                    if (!u.getPassword().equals(user.getPassword())) {
                        return Mono.error(new DomainError("INVALID_CREDENTIALS", "Credenciales inválidas", null, context));
                    }
                    String sessionId = UUID.randomUUID().toString();
                    Session session = new Session(sessionId, user.getEmail());
                    return sessionRepository.save(session).thenReturn(session);
                });
    }
}
