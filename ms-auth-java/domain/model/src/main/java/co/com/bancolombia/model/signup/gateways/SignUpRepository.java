package co.com.bancolombia.model.signup.gateways;

import co.com.bancolombia.model.user.User;
import reactor.core.publisher.Mono;

public interface SignUpRepository {
    Mono<Void> save(User user);
}
