package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public void add(User user) {
        if (user != null) {
            users.put(user.getId(), user);
        }
    }

    @Override
    public User getById(int id) {
        if (id != 0) {
            return users.get(id);
        }
        return new User();
    }

    @Override
    public boolean checkInStorage(User user) {
        return users.containsValue(user);
    }

    public boolean checkInStorageById(int id) {
        return users.containsKey(id);
    }

    @Override
    public Map<Integer, User> get() {
        return users;
    }
}
