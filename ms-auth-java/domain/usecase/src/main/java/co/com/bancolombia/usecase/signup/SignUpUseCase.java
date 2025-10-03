// src/main/java/co/com/bancolombia/usecase/signup/SignUpUseCase.java
package co.com.bancolombia.usecase.signup;

import co.com.bancolombia.model.signup.gateways.SignUpRepository;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.contextdata.ContextData;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.model.exception.DomainError;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

@RequiredArgsConstructor
public class SignUpUseCase {
    private final SignUpRepository sinUpRepository;
    private static final Logger log = LoggerFactory.getLogger(SignUpUseCase.class);
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    public Mono<Object> execute(User user, ContextData context) {
        log.debug("Ejecutando signup con datos: user={}, context={}", user, context);
        if (user == null || user.getEmail() == null || user.getPassword() == null) {
            log.warn("Request inválido: user={}, context={}", user, context);
            return Mono.error(new DomainError("MALFORMED_REQUEST", "Request inválido", null, context));
        }
        if (!EMAIL_REGEX.matcher(user.getEmail()).matches()) {
            log.warn("Email inválido: {}", user.getEmail());
            return Mono.error(new DomainError("INVALID_EMAIL_FORMAT", "Email inválido", null, context));
        }
        if (user.getPassword().length() < 8) {
            log.warn("Password débil para email: {}", user.getEmail());
            return Mono.error(new DomainError("WEAK_PASSWORD", "Password débil", null, context));
        }
        return sinUpRepository.findByEmail(user.getEmail())
                .flatMap(existing -> {
                    log.warn("Email ya registrado: {}", user.getEmail());
                    return Mono.error(new DomainError("EMAIL_ALREADY_EXISTS", "Email ya registrado", null, context));
                })
                .switchIfEmpty(sinUpRepository.save(user)
                        .doOnSuccess(u -> log.info("Usuario creado exitosamente: {}", user.getEmail()))
                        .then());
    }
}
