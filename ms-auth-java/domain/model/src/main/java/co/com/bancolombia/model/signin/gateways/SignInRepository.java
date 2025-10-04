package co.com.bancolombia.model.signin.gateways;

import co.com.bancolombia.model.session.Session;
import co.com.bancolombia.model.user.User;
import reactor.core.publisher.Mono;

public interface SignInRepository {
    Mono<Session> save(Session session);

}
