package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    public final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private int id = 0;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    private int getId() {
        return ++id;
    }

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(userService.userStorage.get().values());
    }

    @GetMapping(value = "/{id}")
    public User getUserById(@PathVariable int id) throws NotFoundException {
        userService.validateAtGetFriends(id);
        return userService.userStorage.getById(id);
    }

    @PostMapping
    public User createUser(@RequestBody User user) throws ValidationException {
        userService.validateOnCreate(user);
        user.setId(getId());
        userService.userStorage.add(user);
        log.info("Пользователь добавлен в коллекцию:" + user);
        return user;
    }

    @PutMapping
    public User createOrUpdateUser(@RequestBody User user) throws ValidationException, NotFoundException {
        userService.validateOnUpdate(user);
        userService.userStorage.add(user);
        log.info("Пользователь обновлен в коллекции:" + user);
        return user;
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public User addFriend(@PathVariable int id,
                          @PathVariable int friendId) throws NotFoundException {
        userService.validateAtAddOrDelFriends(id, friendId);
        userService.addFriend(id, friendId);
        log.info("Пользователь" + id + " добавил друга:" + friendId);
        return userService.userStorage.getById(id);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public User delFriend(@PathVariable int id,
                          @PathVariable int friendId) throws NotFoundException {
        userService.validateAtAddOrDelFriends(id, friendId);
        userService.delFriend(id, friendId);
        log.info("Пользователь" + id + " удалил из друзей:" + friendId);
        return userService.userStorage.getById(id);
    }

    @GetMapping(value = "/{id}/friends")
    public List<User> getAllFriend(@PathVariable int id) throws NotFoundException {
        userService.validateAtGetFriends(id);
        return userService.getFriendList(userService.userStorage.getById(id));
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public List<User> getAllSameFriends(@PathVariable int id,
                                        @PathVariable int otherId) throws NotFoundException {
        userService.validateAtAddOrDelFriends(id, otherId);
        return new ArrayList<>(userService.getSameFriends(userService.userStorage.getById(id),
                userService.userStorage.getById(otherId)));
    }


}
