package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }


    public void addFriend(int userId, int friendId) {
        if (userId != 0 && friendId != 0) {
            userStorage.getUserById(userId).addFriend(friendId);
            userStorage.getUserById(friendId).addFriend(userId);
        }
    }

    public void delFriend(int userId, int friendId) {
        if (userId != 0 && friendId != 0) {
            userStorage.getUserById(userId).addFriend(friendId);
            userStorage.getUserById(friendId).addFriend(userId);
        }
    }

    public List<User> getFriendList(User user) {
        if (user != null) {
            List<User> friendList = new ArrayList<>();
            for (int id : user.getFriends()) {
                friendList.add(userStorage.getUserById(id));
            }
            return friendList;
        }
        return new ArrayList<>();
    }

    public Set<User> getSameFriends(User user, User friend) {
        Set<User> sameFriend = new HashSet<>();
        for (int userFromFirst : user.getFriends()) {
            if (friend.getFriends().contains(userFromFirst)) {
                sameFriend.add(userStorage.getUserById(userFromFirst));
            }
        }
        return sameFriend;
    }
}
