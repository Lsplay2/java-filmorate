package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    public final UserService userService;


    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getAll();
    }

    @GetMapping(value = "/{id}")
    public User getUserById(@PathVariable int id) throws NotFoundException {
        return userService.getById(id);
    }

    @PostMapping
    public User createUser(@RequestBody User user) throws ValidationException, NotFoundException {
        userService.createUser(user);
        log.info("Пользователь добавлен в коллекцию:" + user);
        return user;
    }

    @PutMapping
    public User createOrUpdateUser(@RequestBody User user) throws ValidationException, NotFoundException {
        userService.updateUser(user);
        log.info("Пользователь обновлен в коллекции:" + user);
        return user;
    }

    @DeleteMapping(value = "/{id}")
    public void delUser(@PathVariable int id) throws NotFoundException {
        userService.delUser(id);
        log.info("Пользователь удален. Текущее число пользователей:" + userService.getAll().size());
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public User addFriend(@PathVariable int id,
                          @PathVariable int friendId) throws NotFoundException {

        userService.addFriend(id, friendId);
        log.info("Пользователь" + id + " добавил друга:" + friendId);
        return userService.getById(id);
    }

    @PutMapping(value = "/{id}/friends/{friendId}/confirm")
    public User confirmFriend(@PathVariable int id,
                              @PathVariable int friendId) throws NotFoundException {
        userService.confirmFriend(id, friendId);
        log.info("Пользователь" + id + " подтвердил друга:" + friendId);
        return userService.getById(id);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public User delFriend(@PathVariable int id,
                          @PathVariable int friendId) throws NotFoundException {
        userService.delFriend(id, friendId);
        log.info("Пользователь" + id + " удалил из друзей:" + friendId);
        return userService.getById(id);
    }

    @GetMapping(value = "/{id}/friends")
    public List<User> getAllFriend(@PathVariable int id) throws NotFoundException {
        return userService.getFriendList(id);
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public List<User> getAllSameFriends(@PathVariable int id,
                                        @PathVariable int otherId) throws NotFoundException {
        return userService.getSameFriends(id, otherId);
    }

    @GetMapping(value = "/{id}/films")
    public List<Film> getFilmOnUser(@PathVariable int id) {
        return userService.getFilmFromUser(id);
    }

    @PostMapping(value = "/{id}/films/{filmId}")
    public User addLikeToFilm(@PathVariable int id,
                              @PathVariable int filmId) throws NotFoundException {
        return userService.addLikeToFilm(id, filmId);
    }

    @GetMapping(value = "/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable int id) throws NotFoundException {
        return userService.getRecommendations(id);
    }
    @GetMapping(value = "/{id}/feed")
    public List<Event> getFeed(@PathVariable int id) throws NotFoundException {
        return userService.getFeed(id);
    }
}
