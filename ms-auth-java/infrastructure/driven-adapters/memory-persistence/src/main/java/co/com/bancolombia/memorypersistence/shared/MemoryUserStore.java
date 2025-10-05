package co.com.bancolombia.memorypersistence.shared;

import co.com.bancolombia.model.user.User;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class MemoryUserStore {
    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    public User getByEmail(String email) {
        return users.get(email);
    }
    public void save(User user) {
        users.put(user.getEmail(), user);
    }
}