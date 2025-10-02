// src/main/java/co/com/bancolombia/usecase/signup/SignUpUseCase.java
package co.com.bancolombia.usecase.signup;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.contextdata.ContextData;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.model.domainerror.DomainError;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

@RequiredArgsConstructor
public class SignUpUseCase {
    private final UserRepository userRepository;
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    public Mono<Object> execute(User user, ContextData context) {
        if (user == null || user.getEmail() == null || user.getPassword() == null) {
            return Mono.error(new DomainError("MALFORMED_REQUEST", "Request inválido", null, context));
        }
        if (!EMAIL_REGEX.matcher(user.getEmail()).matches()) {
            return Mono.error(new DomainError("INVALID_EMAIL_FORMAT", "Email inválido", null, context));
        }
        if (user.getPassword().length() < 8) {
            return Mono.error(new DomainError("WEAK_PASSWORD", "Password débil", null, context));
        }
        return userRepository.findByEmail(user.getEmail())
                .flatMap(existing -> Mono.error(new DomainError("EMAIL_ALREADY_EXISTS", "Email ya registrado", null, context)))
                .switchIfEmpty(userRepository.save(user).then());
    }
}
