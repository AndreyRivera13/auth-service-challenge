package co.com.bancolombia.memorypersistence.sessionadapter;

import co.com.bancolombia.model.session.Session;
import co.com.bancolombia.model.session.gateways.SessionRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SessionAdapterImpl  implements SessionRepository {
    private final ConcurrentHashMap<String, String> sessions = new ConcurrentHashMap<>();

    @Override
    public Mono<Session> save(Session session) {
        sessions.put(session.getSessionId(), session.getEmail());
        return Mono.just(session);
    }
}
