package co.com.bancolombia.model.session.gateways;

import co.com.bancolombia.model.session.Session;
import reactor.core.publisher.Mono;

public interface SessionRepository {
   Mono<Session> save(Session session);
}
