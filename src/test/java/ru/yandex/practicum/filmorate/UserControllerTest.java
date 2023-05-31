package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

@SpringBootTest
public class UserControllerTest {
    @Test
    void getAllUser() {
        User user = new User();
        user.setName("qwe");
        user.setId(1);
        user.setBirthday(LocalDate.now().minusYears(10));
        user.setLogin("Lsplay");
        user.setEmail("qwe@qwe");
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        UserController userController = new UserController(new UserService(userStorage));
        userController.userService.userStorage.add(user);
        Assertions.assertEquals(1,userController.userService.userStorage.get().size());
    }

    @Test
    void addUser() throws ValidationException {
        User user = new User();
        user.setName("qwe");
        user.setBirthday(LocalDate.now().minusYears(10));
        user.setLogin("Lsplay");
        user.setEmail("qwe@qwe");
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        UserController userController = new UserController(new UserService(userStorage));
        userController.createUser(user);
        Assertions.assertEquals(user,userController.userService.userStorage.get().get(1));
    }

    @Test
    void addUserFailEmail() {
        User user = new User();
        user.setName("qwe");
        user.setBirthday(LocalDate.now().minusYears(10));
        user.setLogin("Lsplay");
        user.setEmail("");
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        UserController userController = new UserController(new UserService(userStorage));
        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
        Assertions.assertNotEquals(user,userController.userService.userStorage.get().get(1));
    }

    @Test
    void addUserFailEmailAtChar() {
        User user = new User();
        user.setName("qwe");
        user.setBirthday(LocalDate.now().minusYears(10));
        user.setLogin("Lsplay");
        user.setEmail("qweqwe"); //нет @
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        UserController userController = new UserController(new UserService(userStorage));
        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
        Assertions.assertNotEquals(user,userController.userService.userStorage.get().get(1));
    }

    @Test
    void addUserFailLoginIsNull() {
        User user = new User();
        user.setName("qwe");
        user.setBirthday(LocalDate.now().minusYears(10));
        user.setEmail("qwe@qwe");
        user.setLogin("");
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        UserController userController = new UserController(new UserService(userStorage));
        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
        Assertions.assertNotEquals(user,userController.userService.userStorage.get().get(1));
    }

    @Test
    void addUserFailLoginHaveSpace() {
        User user = new User();
        user.setName("qwe");
        user.setBirthday(LocalDate.now().minusDays(1));
        user.setLogin("Lsp lay");
        user.setEmail("qweqwe@"); //нет @
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        UserController userController = new UserController(new UserService(userStorage));
        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
        Assertions.assertNotEquals(user,userController.userService.userStorage.get().get(1));
    }

    @Test
    void addUserFailBirthAfterNow() {
        User user = new User();
        user.setName("qwe");
        user.setBirthday(LocalDate.now().plusDays(10));
        user.setLogin("Lsplay");
        user.setEmail("qwe@qwe"); //нет @
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        UserController userController = new UserController(new UserService(userStorage));
        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
        Assertions.assertNotEquals(user,userController.userService.userStorage.get().get(1));
    }

    @Test
    void addUserFailBirthIsNow() throws ValidationException {
        User user = new User();
        user.setName("qwe");
        user.setBirthday(LocalDate.now());
        user.setLogin("Lsplay");
        user.setEmail("qwe@qwe"); //нет @
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        UserController userController = new UserController(new UserService(userStorage));
        userController.createUser(user);
        Assertions.assertEquals(user,userController.userService.userStorage.get().get(1));
    }
}
