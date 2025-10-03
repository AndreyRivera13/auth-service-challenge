package co.com.bancolombia.memorypersistence.signupadapter;

import co.com.bancolombia.memorypersistence.shared.MemoryUserStore;
import co.com.bancolombia.model.signup.gateways.SignUpRepository;
import co.com.bancolombia.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class SignUpAdapterImpl implements SignUpRepository {

    @Autowired
    private final MemoryUserStore store;

    @Override
    public Mono<User> findByEmail(String email) {
        String password = store.getByEmail(email);
        return password != null ? Mono.just(new User(email, password)) : Mono.empty();
    }

    @Override
    public Mono<Void> save(User user) {
        store.save(user);
        return Mono.empty();
    }
}