package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

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
        UserController userController = new UserController();
        userController.users.put(1, user);
        Assertions.assertEquals(1,userController.users.size());
    }

    @Test
    void addUser() throws ValidationException {
        User user = new User();
        user.setName("qwe");
        user.setBirthday(LocalDate.now().minusYears(10));
        user.setLogin("Lsplay");
        user.setEmail("qwe@qwe");
        UserController userController = new UserController();
        userController.createUser(user);
        Assertions.assertEquals(user,userController.users.get(1));
    }

    @Test
    void addUserFailEmail() {
        User user = new User();
        user.setName("qwe");
        user.setBirthday(LocalDate.now().minusYears(10));
        user.setLogin("Lsplay");
        user.setEmail("");
        UserController userController = new UserController();
        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
        Assertions.assertNotEquals(user,userController.users.get(1));
    }

    @Test
    void addUserFailEmailAtChar() {
        User user = new User();
        user.setName("qwe");
        user.setBirthday(LocalDate.now().minusYears(10));
        user.setLogin("Lsplay");
        user.setEmail("qweqwe"); //нет @
        UserController userController = new UserController();
        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
        Assertions.assertNotEquals(user,userController.users.get(1));
    }

    @Test
    void addUserFailLoginIsNull() {
        User user = new User();
        user.setName("qwe");
        user.setBirthday(LocalDate.now().minusYears(10));
        user.setEmail("qwe@qwe");
        user.setLogin("");
        UserController userController = new UserController();
        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
        Assertions.assertNotEquals(user,userController.users.get(1));
    }

    @Test
    void addUserFailLoginHaveSpace() {
        User user = new User();
        user.setName("qwe");
        user.setBirthday(LocalDate.now().minusDays(1));
        user.setLogin("Lsp lay");
        user.setEmail("qweqwe@"); //нет @
        UserController userController = new UserController();
        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
        Assertions.assertNotEquals(user,userController.users.get(1));
    }

    @Test
    void addUserFailBirthAfterNow() {
        User user = new User();
        user.setName("qwe");
        user.setBirthday(LocalDate.now().plusDays(10));
        user.setLogin("Lsplay");
        user.setEmail("qwe@qwe"); //нет @
        UserController userController = new UserController();
        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
        Assertions.assertNotEquals(user,userController.users.get(1));
    }

    @Test
    void addUserFailBirthIsNow() throws ValidationException {
        User user = new User();
        user.setName("qwe");
        user.setBirthday(LocalDate.now());
        user.setLogin("Lsplay");
        user.setEmail("qwe@qwe"); //нет @
        UserController userController = new UserController();
        userController.createUser(user);
        Assertions.assertEquals(user,userController.users.get(1));
    }
}
