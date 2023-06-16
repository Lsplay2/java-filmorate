package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    public final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {

        this.filmService = filmService;

    }

    private int id = 0;

    private int getId() {
        return ++id;
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(filmService.filmStorage.get().values());
    }

    @GetMapping(value = "/{id}")
    public Film getFilmById(@PathVariable int id) throws NotFoundException {
        if (filmService.filmStorage.checkInStorageById(id)) {
            return filmService.filmStorage.getById(id);
        }
        throw new NotFoundException("Фильм не найден");
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) throws ValidationException {
        filmService.validate(film);
        film.setId(getId());
        filmService.filmStorage.add(film);
        log.info("Фильм добавлен в коллекцию:" + film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws ValidationException, NotFoundException {
        filmService.validateOnUpdate(film);
        filmService.filmStorage.add(film);
        log.info("Фильм обновлен в коллекции:" + film);
        return film;
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public Film addLike(@PathVariable int id,
                        @PathVariable int userId) throws NotFoundException {
        filmService.validateAddAndDelLike(id, userId);
        filmService.addLike(filmService.filmStorage.getById(id), filmService.userStorage.getById(userId));
        log.info("Лайк добавлен к фильму. Текущее число лайков:" + filmService.filmStorage.getById(id).getNumOfLike().size());
        return filmService.filmStorage.getById(id);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public Film delLike(@PathVariable int id,
                        @PathVariable int userId) throws NotFoundException {
        filmService.validateAddAndDelLike(id, userId);
        filmService.delLike(filmService.filmStorage.getById(id), filmService.userStorage.getById(userId));
        log.info("Лайк убран у фильма. Текущее число лайков:" + filmService.filmStorage.getById(id).getNumOfLike().size());
        return filmService.filmStorage.getById(id);
    }

    @GetMapping(value = "/popular")
    public List<Film> getTop(@RequestParam(defaultValue = "10") Integer count) throws ValidationException {
        if (count <= 0) {
            return new ArrayList<>(filmService.getTopFilm(filmService.filmStorage, 10));
        }
        return new ArrayList<>(filmService.getTopFilm(filmService.filmStorage, count));
    }
}
