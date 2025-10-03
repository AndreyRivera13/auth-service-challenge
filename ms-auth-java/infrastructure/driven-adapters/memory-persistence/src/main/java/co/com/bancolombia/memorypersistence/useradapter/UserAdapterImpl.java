package co.com.bancolombia.memorypersistence.useradapter;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserAdapterImpl implements UserRepository {
    private static final Log log = LogFactory.getLog(UserAdapterImpl.class);
    private final ConcurrentHashMap<String, String> users = new ConcurrentHashMap<>();

    @Override
    public Mono<User> findByEmail(String email) {
        String password = users.get(email);
        if (password != null) {
            return Mono.just(new User(email, password));
        }
        return Mono.empty();
    }

    @Override
    public Mono<Void> save(User user) {
        users.put(user.getEmail(), user.getPassword());
        log.info("User saved: " + user.getEmail());
        return Mono.empty();
    }
}
