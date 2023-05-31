package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

@SpringBootTest
public class FilmControllerTest {

    private final LocalDate minDate = LocalDate.of(1895,12,28);

    @Test
    void checkGetAllFilms() {
        FilmController filmController =
                new FilmController(new FilmService(new InMemoryFilmStorage(),new InMemoryUserStorage()));
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("qwe");
        film.setReleaseDate(LocalDate.MAX);
        film.setDuration(120);
        filmController.filmService.filmStorage.add(film);
        Assertions.assertEquals(1, filmController.filmService.filmStorage.get().size());
    }

    @Test
    void checkAddFilm() throws ValidationException {
        FilmController filmController =
                new FilmController(new FilmService(new InMemoryFilmStorage(),new InMemoryUserStorage()));
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("qwe");
        film.setReleaseDate(LocalDate.MAX);
        film.setDuration(120);
        filmController.createFilm(film);
        Assertions.assertEquals(film, filmController.filmService.filmStorage.get().get(1));
    }

    @Test
    void checkAddFilmFailName() {
        FilmController filmController =
                new FilmController(new FilmService(new InMemoryFilmStorage(),new InMemoryUserStorage()));
        Film film = new Film();
        film.setDescription("qwe");
        film.setReleaseDate(LocalDate.MAX);
        film.setDuration(120);
        Assertions.assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        Assertions.assertNotEquals(film, filmController.filmService.filmStorage.get().get(1));
    }

    @Test
    void checkAddFilmFailDesctiption() {
        FilmController filmController =
                new FilmController(new FilmService(new InMemoryFilmStorage(),new InMemoryUserStorage()));
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod " +
                "tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud " +
                "exerci tation"); //строка в 201 символов
        film.setReleaseDate(LocalDate.MAX);
        film.setDuration(120);
        Assertions.assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        Assertions.assertNotEquals(film, filmController.filmService.filmStorage.get().get(1));
    }

    @Test
    void checkAddFilmDesctiptionWith200Chars() throws ValidationException {
        FilmController filmController =
                new FilmController(new FilmService(new InMemoryFilmStorage(),new InMemoryUserStorage()));
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod " +
                "tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud " +
                "exerci tatio"); //строка в 200 символов
        film.setReleaseDate(LocalDate.MAX);
        film.setDuration(120);
        filmController.createFilm(film);
        Assertions.assertEquals(film, filmController.filmService.filmStorage.get().get(1));
    }

    @Test
    void checkAddFilmFailReleaseData() {
        FilmController filmController =
                new FilmController(new FilmService(new InMemoryFilmStorage(),new InMemoryUserStorage()));
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("qwe"); //строка в 201 символов
        film.setReleaseDate(LocalDate.MIN);
        film.setDuration(120);
        Assertions.assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        Assertions.assertNotEquals(film, filmController.filmService.filmStorage.get().get(1));
    }

    @Test
    void checkAddFilmReleaseDataIsMin() throws ValidationException {
        FilmController filmController =
                new FilmController(new FilmService(new InMemoryFilmStorage(),new InMemoryUserStorage()));
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("qwe"); //строка в 200 символов
        film.setReleaseDate(minDate);
        film.setDuration(120);
        filmController.createFilm(film);
        Assertions.assertEquals(film, filmController.filmService.filmStorage.get().get(1));
    }

    @Test
    void checkAddFilmReleaseDataIsMinMinusDay() {
        FilmController filmController =
                new FilmController(new FilmService(new InMemoryFilmStorage(),new InMemoryUserStorage()));
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("qwe"); //строка в 200 символов
        film.setReleaseDate(minDate.minusDays(1));
        film.setDuration(120);
        Assertions.assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        Assertions.assertNotEquals(film, filmController.filmService.filmStorage.get().get(1));
    }

    @Test
    void checkAddFilmFailDuration() {
        FilmController filmController =
                new FilmController(new FilmService(new InMemoryFilmStorage(),new InMemoryUserStorage()));
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("qwe"); //строка в 201 символов
        film.setReleaseDate(LocalDate.MAX);
        film.setDuration(-100);
        Assertions.assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        Assertions.assertNotEquals(film, filmController.filmService.filmStorage.get().get(1));
    }

    @Test
    void checkAddFilmDurationIs0() throws ValidationException {
        FilmController filmController =
                new FilmController(new FilmService(new InMemoryFilmStorage(),new InMemoryUserStorage()));
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("qwe"); //строка в 200 символов
        film.setReleaseDate(LocalDate.MAX);
        film.setDuration(0);
        filmController.createFilm(film);
        Assertions.assertEquals(film, filmController.filmService.filmStorage.get().get(1));
    }

}
