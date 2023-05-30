package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    public final InMemoryUserStorage userStorage;
    final UserService userService;
    @Autowired
    public UserController(InMemoryUserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private int id = 0;

    private int getId() {
        return ++id;
    }

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(userStorage.getUsers().values());
    }

    @GetMapping(value = "/{id}")
    public User getUserById(@PathVariable int id) throws NotFoundException {
        validateAtGetFriends(id);
        return userStorage.getUserById(id);
    }
    @PostMapping
    public User createUser(@RequestBody User user) throws ValidationException {
        validateOnCreate(user);
        user.setId(getId());
        userStorage.addUser(user);
        log.info("Пользователь добавлен в коллекцию:" + user);
        return user;
    }


    @PutMapping
    public User createOrUpdateUser(@RequestBody User user) throws ValidationException, NotFoundException {
        validateOnUpdate(user);
        userStorage.addUser(user);
        log.info("Пользователь обновлен в коллекции:" + user);
        return user;
    }
    @PutMapping(value = "/{id}/friends/{friendId}")
    public User addFriend(@PathVariable int id,
                          @PathVariable int friendId) throws NotFoundException {
        validateAtAddOrDelFriends(id, friendId);
        userService.addFriend(id, friendId);
        log.info("Пользователь" + id + " добавил друга:" + friendId);
        return userStorage.getUserById(id);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public User delFriend(@PathVariable int id,
                          @PathVariable int friendId) throws NotFoundException {
        validateAtAddOrDelFriends(id, friendId);
        userService.delFriend(id, friendId);
        log.info("Пользователь" + id + " удалил из друзей:" + friendId);
        return userStorage.getUserById(id);
    }

    @GetMapping(value = "/{id}/friends")
    public List<User> getAllFriend(@PathVariable int id) throws NotFoundException {
        validateAtGetFriends(id);
        return userService.getFriendList(userStorage.getUserById(id));
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public List<User> getAllSameFriends(@PathVariable int id,
                                        @PathVariable int otherId) throws NotFoundException {
        validateAtAddOrDelFriends(id, otherId);
        return new ArrayList<>
                (userService.getSameFriends(userStorage.getUserById(id), userStorage.getUserById(otherId)));
    }

    private void validate(User user) throws ValidationException {
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@") || user.getLogin() == null
                || user.getLogin().isEmpty() || user.getLogin().contains(" ")
                || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка в одном из полей пользователя");
            throw new ValidationException("Ошибка в одном из полей пользователя");
        }
    }

    private void validateAtAddOrDelFriends(int userId, int friendId) throws NotFoundException {
        if (userStorage.checkUserInStorageById(userId) && userStorage.checkUserInStorageById(friendId)) {
            return;
        }
        log.error("Ошибка в одном из id пользователя");
        throw new NotFoundException("Ошибка в одном из id пользователя");
    }

    private void validateAtGetFriends(int userId) throws NotFoundException {
        if (userStorage.checkUserInStorageById(userId)) {
            return;
        }
        log.error("Ошибка в id пользователя");
        throw new NotFoundException("Ошибка в id пользователя");
    }
    private void validateOnCreate(User user) throws ValidationException {
        validate(user);
        if (user.getName() == null || user.getName().isEmpty()) {
            log.warn("Имя пользователя не указано. Будет использоваться логин:" + user.getLogin());
            user.setName(user.getLogin());
        }
    }

    private void validateOnUpdate(User user) throws ValidationException, NotFoundException {
        validateOnCreate(user);
        if (!userStorage.checkUserInStorageById(user.getId())) {
            log.error("Ошибка фильм с таким id не существует");
            throw new NotFoundException("Фильма с таким id не существует");
        }
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Integer> handleValidation(ValidationException e) {
        return Map.of("Validation exception", 400);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Integer> handleNotFound(NotFoundException e) {
        return Map.of("Not Found exception", 404);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Integer> handleAnother(Exception e) {
        return Map.of("Unknown exception", 500);
    }
}
