package co.com.bancolombia.usecase.signup;

import co.com.bancolombia.model.shared.common.exception.model.CodeMessage;
import co.com.bancolombia.model.signup.gateways.SignUpRepository;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.shared.cqrs.ContextData;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.model.shared.common.exception.DomainError;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

@RequiredArgsConstructor
public class SignUpUseCase {
    private final SignUpRepository sinUpRepository;
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(SignUpUseCase.class);
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    public Mono<Object> execute(User user, ContextData context) {
        log.debug("Ejecutando signup con datos: user={}, context={}", user, context);
        if (user == null || user.getEmail() == null || user.getPassword() == null) {
            log.warn("Request inválido: user={}, context={}", user.getEmail(), context);
            return Mono.error(new DomainError(CodeMessage.MALFORMED_REQUEST_CODE,CodeMessage.REQUEST_INVALID_MESSAGE, null, context));
        }
        if (!EMAIL_REGEX.matcher(user.getEmail()).matches()) {
            log.warn("Email inválido: {}", user.getEmail());
            return Mono.error(new DomainError(CodeMessage.INVALID_EMAIL_FORMAT_CODE, CodeMessage.INVALID_EMAIL_FORMAT_MESSAGE, null, context));
        }
        if (user.getPassword().length() < 8) {
            log.warn("Password débil para email: {}", user.getEmail());
            return Mono.error(new DomainError(CodeMessage.WEAK_PASSWORD_CODE, CodeMessage.WEAK_PASSWORD_MESSAGE, null, context));
        }
        return userRepository.findByEmail(user.getEmail())
                .flatMap(existing -> {
                    log.warn("Email ya registrado: {}", user.getEmail());
                    return Mono.error(new DomainError(CodeMessage.EMAIL_ALREADY_EXISTS_CODE, CodeMessage.EMAIL_ALREADY_EXISTS_MESSAGE, null, context));
                })
                .switchIfEmpty(Mono.defer(() ->
                        sinUpRepository.save(user)
                                .doOnSuccess(ignored -> log.info("Usuario creado exitosamente: {}", user.getEmail()))
                                .then(Mono.<Object>empty())
                ));
    }
}