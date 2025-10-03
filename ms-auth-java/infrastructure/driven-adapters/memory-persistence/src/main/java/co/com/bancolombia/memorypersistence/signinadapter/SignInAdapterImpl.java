package co.com.bancolombia.memorypersistence.signinadapter;

import co.com.bancolombia.memorypersistence.shared.MemoryUserStore;
import co.com.bancolombia.model.session.Session;
import co.com.bancolombia.model.session.gateways.SessionRepository;
import co.com.bancolombia.model.signin.gateways.SignInRepository;
import co.com.bancolombia.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class SignInAdapterImpl implements SignInRepository {

        private final MemoryUserStore store;
        private final SessionRepository sessionRepository;

        @Override
        public Mono<User> findByEmail(String email) {
            User user = store.getByEmail(email);
            return user != null ? Mono.just(user) : Mono.empty();
        }

        @Override
        public Mono<Session> save(Session session) {
            return sessionRepository.save(session);
        }
    }
