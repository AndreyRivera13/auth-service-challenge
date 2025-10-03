package co.com.bancolombia.memorypersistence.signinadapter;

import co.com.bancolombia.memorypersistence.shared.MemoryUserStore;
import co.com.bancolombia.model.session.Session;
import co.com.bancolombia.model.signin.gateways.SignInRepository;
import co.com.bancolombia.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class SignInAdapterImpl implements SignInRepository {

    private final MemoryUserStore store;

    public Mono<User> findByEmail(String email) {
        String password = store.getPassword(email).getPassword();
        return password != null ? Mono.just(new User(email, password)) : Mono.empty();
    }

    public Mono<Boolean> validateCredentials(String email, String rawPassword) {
        String stored = store.getPassword(email).getPassword();
        return Mono.just(stored != null && stored.equals(rawPassword));
    }

    @Override
    public Mono<Session> save(Session session) {
        return Mono.just(session);
    }
}
