package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() throws NotFoundException {
        log.info("Поступил запрос на получение всех фильмов");
        return filmService.getAll();
    }

    @GetMapping(value = "/{id}")
    public Film getFilmById(@PathVariable int id) throws NotFoundException {
        log.info("Поступил запрос на получение фильма по id:" + id);
        return filmService.getById(id);
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) throws ValidationException, NotFoundException {
        log.info("Поступил запрос на создание фильма:" + film);
        Film filmTemp = filmService.createFilm(film);
        log.info("Фильм добавлен в коллекцию:" + film);
        return filmTemp;
    }


    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws ValidationException, NotFoundException {
        log.info("Поступил запрос на обновление фильма:" + film);
        Film filmTemp = filmService.updateFilm(film);
        log.info("Фильм обновлен в коллекции:" + film);
        return filmTemp;
    }

    @DeleteMapping(value = "/{id}")
    public void delFilm(@PathVariable int id) throws NotFoundException {
        log.info("Поступил запрос на удаление фильма:" + id);
        filmService.delFilm(id);
        log.info("Фильм удален. Текущее число фильмов:" + filmService.getAll().size());
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public Film addLike(@PathVariable int id,
                        @PathVariable int userId) throws NotFoundException {
        log.info("Поступил запрос на добавление лайка фильму:" + id);
        filmService.addLike(id, userId);
        log.info("Лайк добавлен к фильму. Текущее число лайков:" + filmService.getById(id).getNumOfLike());
        return filmService.getById(id);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public Film delLike(@PathVariable int id,
                        @PathVariable int userId) throws NotFoundException {
        log.info("Поступил запрос на удаление лайка у фильма:" + id);
        filmService.delLike(id, userId);
        log.info("Лайк убран у фильма. Текущее число лайков:" + filmService.getById(id).getNumOfLike());
        return filmService.getById(id);
    }

    @GetMapping(value = "/popular")
    public List<Film> getTop(@RequestParam(defaultValue = "10") Integer count,
                             @RequestParam(defaultValue = "0") int genreId,
                             @RequestParam(defaultValue = "0") int year) {
        log.info("Поступил запрос на получения топ фильмов:");
        return filmService.getTopFilmByGenreOrYear(count, genreId, year);

    }

    @GetMapping(value = "/common")
    public List<Film> getCommonFilms(@RequestParam Integer userId, @RequestParam Integer friendId) {
        log.info("Поступил запрос на получение общих фильмов у пользователей {} и {}.", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping(value = "/search")
    public List<Film> getSearchedFilms(@RequestParam String query, @RequestParam String by) {
        return filmService.getSearchedFilms(query, by);

    }
}
