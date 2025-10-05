package co.com.bancolombia.memorypersistence.shared;

import co.com.bancolombia.model.session.Session;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class MemorySessionStore {
    private final ConcurrentHashMap<String, String> sessions = new ConcurrentHashMap<>();

    public Mono<Session> save(Session session) {
        sessions.put(session.getEmail(),session.getSessionId());
        return Mono.just(session);
    }

}
