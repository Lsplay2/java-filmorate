package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    public InMemoryUserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }


    public void addFriend(int userId, int friendId) {
        if (userId != 0 && friendId != 0) {
            userStorage.getById(userId).addFriend(friendId);
            userStorage.getById(friendId).addFriend(userId);
        }
    }

    public void delFriend(int userId, int friendId) {
        if (userId != 0 && friendId != 0) {
            userStorage.getById(userId).addFriend(friendId);
            userStorage.getById(friendId).addFriend(userId);
        }
    }

    public List<User> getFriendList(User user) {
        if (user != null) {
            List<User> friendList = new ArrayList<>();
            for (int id : user.getFriends()) {
                friendList.add(userStorage.getById(id));
            }
            return friendList;
        }
        return new ArrayList<>();
    }

    public Set<User> getSameFriends(User user, User friend) {
        Set<User> sameFriend = new HashSet<>();
        for (int userFromFirst : user.getFriends()) {
            if (friend.getFriends().contains(userFromFirst)) {
                sameFriend.add(userStorage.getById(userFromFirst));
            }
        }
        return sameFriend;
    }

    private void validate(User user) throws ValidationException {
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@") || user.getLogin() == null
                || user.getLogin().isEmpty() || user.getLogin().contains(" ")
                || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка в одном из полей пользователя");
            throw new ValidationException("Ошибка в одном из полей пользователя");
        }
    }

    public void validateAtAddOrDelFriends(int userId, int friendId) throws NotFoundException {
        if (userStorage.checkInStorageById(userId) && userStorage.checkInStorageById(friendId)) {
            return;
        }
        log.error("Ошибка в одном из id пользователя");
        throw new NotFoundException("Ошибка в одном из id пользователя");
    }

    public void validateAtGetFriends(int userId) throws NotFoundException {
        if (userStorage.checkInStorageById(userId)) {
            return;
        }
        log.error("Ошибка в id пользователя");
        throw new NotFoundException("Ошибка в id пользователя");
    }

    public void validateOnCreate(User user) throws ValidationException {
        validate(user);
        if (user.getName() == null || user.getName().isEmpty()) {
            log.warn("Имя пользователя не указано. Будет использоваться логин:" + user.getLogin());
            user.setName(user.getLogin());
        }
    }

    public void validateOnUpdate(User user) throws ValidationException, NotFoundException {
        validateOnCreate(user);
        if (!userStorage.checkInStorageById(user.getId())) {
            log.error("Ошибка фильм с таким id не существует");
            throw new NotFoundException("Фильма с таким id не существует");
        }
    }
}
