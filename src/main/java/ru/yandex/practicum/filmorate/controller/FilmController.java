package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    public final InMemoryFilmStorage filmStorage;
    final FilmService filmService;
    final InMemoryUserStorage userStorage;


    @Autowired
    public FilmController(InMemoryFilmStorage filmStorage, FilmService filmService , InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
        this.userStorage = userStorage;

    }

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private static final LocalDate MIN_DATE = LocalDate.of(1895,12,28);
    private int id = 0;

    private int getId() {
        return ++id;
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(filmStorage.getAllFilms().values());
    }

    @GetMapping(value = "/{id}")
    public Film getFilmById(@PathVariable int id) throws NotFoundException {
        if (filmStorage.checkFilmInStorageById(id)) {
            return filmStorage.getFilmById(id);
        }
        throw new NotFoundException("Фильм не найден");
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) throws ValidationException {
        validate(film);
        film.setId(getId());
        filmStorage.addFilm(film);
        log.info("Фильм добавлен в коллекцию:" + film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws ValidationException, NotFoundException {
        validateOnUpdate(film);
        filmStorage.addFilm(film);
        log.info("Фильм обновлен в коллекции:" + film);
        return film;
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public Film addLike(@PathVariable int id,
                        @PathVariable int userId) throws NotFoundException {
        validateAddAndDelLike(id, userId);
        filmService.addLike(filmStorage.getFilmById(id), userStorage.getUserById(userId));
        log.info("Лайк добавлен к фильму. Текущее число лайков:" + filmStorage.getFilmById(id).getNumOfLike().size());
        return filmStorage.getFilmById(id);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public Film delLike(@PathVariable int id,
                        @PathVariable int userId) throws NotFoundException {
        validateAddAndDelLike(id, userId);
        filmService.delLike(filmStorage.getFilmById(id), userStorage.getUserById(userId));
        log.info("Лайк убран у фильма. Текущее число лайков:" + filmStorage.getFilmById(id).getNumOfLike().size());
        return filmStorage.getFilmById(id);
    }

    @GetMapping(value = "/popular")
    public List<Film> getTop(@RequestParam(defaultValue = "10") Integer count) throws ValidationException {
        if (count <= 0) {
            return new ArrayList<>(filmService.getTopFilm(filmStorage, 10));
        }
        return new ArrayList<>(filmService.getTopFilm(filmStorage, count));
    }



    private void validateAddAndDelLike(int filmId, int userId) throws NotFoundException {
        if (filmStorage.checkFilmInStorageById(filmId) && userStorage.checkUserInStorageById(userId)) {
            return;
        } else {
            log.error("Ошибка в одном из id юзера или фильма");
            throw new NotFoundException("Ошибка в одном из id юзера или фильма");
        }
    }



    private void validate(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isEmpty() || film.getDescription().getBytes().length > 200
                || film.getReleaseDate().isBefore(MIN_DATE) || film.getDuration() < 0) {
            log.error("Ошибка в одном из полcй фильма");
            throw new ValidationException("Ошибка в одном из полей фильма");
        }
    }

    private void validateOnUpdate(Film film) throws NotFoundException, ValidationException {
        validate(film);
        if (!filmStorage.checkFilmInStorageById(film.getId())) {
            log.error("Ошибка фильм с таким id не создан");
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
