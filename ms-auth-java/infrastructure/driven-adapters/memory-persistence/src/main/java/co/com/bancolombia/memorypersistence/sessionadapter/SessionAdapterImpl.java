package co.com.bancolombia.memorypersistence.sessionadapter;

import co.com.bancolombia.model.session.Session;
import co.com.bancolombia.model.session.gateways.SessionRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class SessionAdapterImpl  implements SessionRepository {


    @Override
    public Mono<Session> save(Session session) {

        return Mono.just(session);
    }
}
