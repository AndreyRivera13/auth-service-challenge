package co.com.bancolombia.memorypersistence.useradapter;

import co.com.bancolombia.memorypersistence.shared.MemoryUserStore;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
@RequiredArgsConstructor
public class UserAdapterImpl implements UserRepository {

    private final MemoryUserStore store;

    @Override
    public Mono<User> findByEmail(String email) {
        return Mono.justOrEmpty(store.getByEmail(email));
    }
}
