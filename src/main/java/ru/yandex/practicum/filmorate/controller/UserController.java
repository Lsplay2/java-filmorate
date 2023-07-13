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
import ru.yandex.practicum.filmorate.service.EventService;


import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    public final UserService userService;
    private final EventService eventService;


    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService, EventService eventService) {
        this.userService = userService;
        this.eventService = eventService;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Поступил запрос на получение всех пользователей");
        return userService.getAll();
    }

    @GetMapping(value = "/{id}")
    public User getUserById(@PathVariable int id) throws NotFoundException {
        log.info("Поступил запрос на получение пользователей по id:" + id);
        return userService.getById(id);
    }

    @PostMapping
    public User createUser(@RequestBody User user) throws ValidationException, NotFoundException {
        log.info("Поступил запрос на создание пользователя:" + user);
        userService.createUser(user);
        log.info("Пользователь добавлен в коллекцию:" + user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) throws ValidationException, NotFoundException {
        log.info("Поступил запрос на обновление пользователя:" + user);
        userService.updateUser(user);
        log.info("Пользователь обновлен в коллекции:" + user);
        return user;
    }

    @DeleteMapping(value = "/{id}")
    public void delUser(@PathVariable int id) throws NotFoundException {
        log.info("Поступил запрос на удаление пользователя:" + id);
        userService.delUser(id);
        log.info("Пользователь удален. Текущее число пользователей:" + userService.getAll().size());
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public User addFriend(@PathVariable int id,
                          @PathVariable int friendId) throws NotFoundException {
        log.info("Поступил запрос на добавление друга");
        userService.addFriend(id, friendId);
        log.info("Пользователь" + id + " добавил друга:" + friendId);
        return userService.getById(id);
    }

    @PutMapping(value = "/{id}/friends/{friendId}/confirm")
    public User confirmFriend(@PathVariable int id,
                              @PathVariable int friendId) throws NotFoundException {
        log.info("Поступил запрос на подтверждение друга");
        userService.confirmFriend(id, friendId);
        log.info("Пользователь" + id + " подтвердил друга:" + friendId);
        return userService.getById(id);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public User delFriend(@PathVariable int id,
                          @PathVariable int friendId) throws NotFoundException {
        log.info("Поступил запрос на удаление друга");
        userService.delFriend(id, friendId);
        log.info("Пользователь" + id + " удалил из друзей:" + friendId);
        return userService.getById(id);
    }

    @GetMapping(value = "/{id}/friends")
    public List<User> getAllFriend(@PathVariable int id) throws NotFoundException {
        log.info("Поступил запрос на получение всех друзей");
        return userService.getFriendList(id);
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public List<User> getAllSameFriends(@PathVariable int id,
                                        @PathVariable int otherId) throws NotFoundException {
        log.info("Поступил запрос на получение всех одинаковых с пользователем друзей");
        return userService.getSameFriends(id, otherId);
    }

    @GetMapping(value = "/{id}/films")
    public List<Film> getFilmOnUser(@PathVariable int id) {
        log.info("Поступил запрос на получение всех лайкнутых фильмов");
        return userService.getFilmFromUser(id);
    }

    @PostMapping(value = "/{id}/films/{filmId}")
    public User addLikeToFilm(@PathVariable int id,
                              @PathVariable int filmId) throws NotFoundException {
        log.info("Поступил запрос на лайк фильма от пользователя");
        return userService.addLikeToFilm(id, filmId);
    }

    @GetMapping(value = "/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable int id) throws NotFoundException {
        log.info("Поступил запрос на получение рекомендаций");
        return userService.getRecommendations(id);
    }

    @GetMapping(value = "/{id}/feed")
    public List<Event> getFeed(@PathVariable int id) throws NotFoundException {
        log.info("Поступил запрос на получение всех событий");
        return userService.getFeed(id);
    }
}
