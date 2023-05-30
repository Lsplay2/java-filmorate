package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public void addUser(User user) {
        if (user != null) {
            users.put(user.getId(), user);
        }
    }

    @Override
    public User getUserById(int id) {
        if (id != 0) {
            return users.get(id);
        }
        return new User();
    }

    @Override
    public boolean checkUserInStorage(User user) {
        return users.containsValue(user);
    }

    public boolean checkUserInStorageById(int id) {
        return users.containsKey(id);
    }

    @Override
    public Map<Integer, User> getUsers() {
        return users;
    }
}
