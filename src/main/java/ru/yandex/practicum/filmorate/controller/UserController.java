package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    public Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    private int getId() {
        return ++id;
    }

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@RequestBody User user) throws ValidationException {
        validateOnCreate(user);
        user.setId(getId());
        users.put(user.getId(), user);
        log.info("Пользователь добавлен в коллекцию:" + user);
        return user;
    }

    @PutMapping
    public User createOrUpdateUser(@RequestBody User user) throws ValidationException {
        validateOnUpdate(user);
        users.put(user.getId(), user);
        log.info("Пользователь обновлен в коллекции:" + user);
        return user;
    }


    private void validate(User user) throws ValidationException {
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@") || user.getLogin() == null
                || user.getLogin().isEmpty() || user.getLogin().contains(" ")
                || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка в одном из полей пользователя");
            throw new ValidationException("Ошибка в одном из полей пользователя");
        }
    }

    private void validateOnCreate(User user) throws ValidationException {
        validate(user);
        if (user.getName() == null || user.getName().isEmpty()) {
            log.warn("Имя пользователя не указано. Будет использоваться логин:" + user.getLogin());
            user.setName(user.getLogin());
        }
    }

    private void validateOnUpdate(User user) throws ValidationException {
        validateOnCreate(user);
        if (!users.containsKey(user.getId())) {
            log.error("Ошибка фильм с таким id не существует");
            throw new ValidationException("Фильма с таким id не существует");
        }
    }

}
