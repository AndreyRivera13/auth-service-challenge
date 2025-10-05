package co.com.bancolombia.memorypersistence.signinadapter;

import co.com.bancolombia.memorypersistence.shared.MemorySessionStore;
import co.com.bancolombia.model.session.Session;
import co.com.bancolombia.model.signin.gateways.SignInRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class SignInAdapterImpl implements SignInRepository {

    private final MemorySessionStore store;

    @Override
    public Mono<Session> save(Session session) {
        return store.save(session);
    }
}
