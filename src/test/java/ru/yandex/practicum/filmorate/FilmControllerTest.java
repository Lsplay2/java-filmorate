package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@SpringBootTest
public class FilmControllerTest {

    private final LocalDate minDate = LocalDate.of(1895,12,28);

    @Test
    void checkGetAllFilms() {
        FilmController filmController = new FilmController();
        filmController.films.put(1,new Film());
        Assertions.assertEquals(1, filmController.getFilms().size());
    }

    @Test
    void checkAddFilm() throws ValidationException {
        FilmController filmController = new FilmController();
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("qwe");
        film.setReleaseDate(LocalDate.MAX);
        film.setDuration(120);
        filmController.createFilm(film);
        Assertions.assertEquals(film, filmController.films.get(1));
    }

    @Test
    void checkAddFilmFailName() {
        FilmController filmController = new FilmController();
        Film film = new Film();
        film.setDescription("qwe");
        film.setReleaseDate(LocalDate.MAX);
        film.setDuration(120);
        try {
            filmController.createFilm(film);
        } catch (ValidationException e) {
            Assertions.assertEquals("Ошибка в одном из полей фильма", e.getMessage());
        }
        Assertions.assertNotEquals(film, filmController.films.get(1));
    }

    @Test
    void checkAddFilmFailDesctiption() {
        FilmController filmController = new FilmController();
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod " +
                "tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud " +
                "exerci tation"); //строка в 201 символов
        film.setReleaseDate(LocalDate.MAX);
        film.setDuration(120);
        try {
            filmController.createFilm(film);
        } catch (ValidationException e) {
            Assertions.assertEquals("Ошибка в одном из полей фильма", e.getMessage());
        }
        Assertions.assertNotEquals(film, filmController.films.get(1));
    }

    @Test
    void checkAddFilmDesctiptionWith200Chars() throws ValidationException {
        FilmController filmController = new FilmController();
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod " +
                "tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud " +
                "exerci tatio"); //строка в 200 символов
        film.setReleaseDate(LocalDate.MAX);
        film.setDuration(120);
        filmController.createFilm(film);
        Assertions.assertEquals(film, filmController.films.get(1));
    }

    @Test
    void checkAddFilmFailReleaseData() {
        FilmController filmController = new FilmController();
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("qwe"); //строка в 201 символов
        film.setReleaseDate(LocalDate.MIN);
        film.setDuration(120);
        try {
            filmController.createFilm(film);
        } catch (ValidationException e) {
            Assertions.assertEquals("Ошибка в одном из полей фильма", e.getMessage());
        }
        Assertions.assertNotEquals(film, filmController.films.get(1));
    }

    @Test
    void checkAddFilmReleaseDataIsMin() throws ValidationException {
        FilmController filmController = new FilmController();
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("qwe"); //строка в 200 символов
        film.setReleaseDate(minDate);
        film.setDuration(120);
        filmController.createFilm(film);
        Assertions.assertEquals(film, filmController.films.get(1));
    }

    @Test
    void checkAddFilmReleaseDataIsMinMinusDay() {
        FilmController filmController = new FilmController();
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("qwe"); //строка в 200 символов
        film.setReleaseDate(minDate.minusDays(1));
        film.setDuration(120);
        try {
            filmController.createFilm(film);
        } catch (ValidationException e) {
            Assertions.assertEquals("Ошибка в одном из полей фильма", e.getMessage());
        }
        Assertions.assertNotEquals(film, filmController.films.get(1));
    }

    @Test
    void checkAddFilmFailDuration() {
        FilmController filmController = new FilmController();
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("qwe"); //строка в 201 символов
        film.setReleaseDate(LocalDate.MAX);
        film.setDuration(-100);
        try {
            filmController.createFilm(film);
        } catch (ValidationException e) {
            Assertions.assertEquals("Ошибка в одном из полей фильма", e.getMessage());
        }
        Assertions.assertNotEquals(film, filmController.films.get(1));
    }

    @Test
    void checkAddFilmDurationIs0() throws ValidationException {
        FilmController filmController = new FilmController();
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("qwe"); //строка в 200 символов
        film.setReleaseDate(LocalDate.MAX);
        film.setDuration(0);
        filmController.createFilm(film);
        Assertions.assertEquals(film, filmController.films.get(1));
    }

}
