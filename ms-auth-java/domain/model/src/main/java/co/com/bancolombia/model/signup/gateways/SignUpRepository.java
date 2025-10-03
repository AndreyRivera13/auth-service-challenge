package co.com.bancolombia.model.signup.gateways;

import co.com.bancolombia.model.user.User;
import reactor.core.publisher.Mono;

public interface SignUpRepository {
    Mono<User> findByEmail(String email);
    Mono<Void> save(User user);
}
