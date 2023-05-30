package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserStorage {
    void addUser(User user);

    User getUserById(int id);

    boolean checkUserInStorage(User user);

    Map<Integer, User> getUsers();
}
