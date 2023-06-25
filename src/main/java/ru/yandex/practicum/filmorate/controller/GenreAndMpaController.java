package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
public class GenreAndMpaController {

    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    public final FilmService filmService;

    @Autowired
    public GenreAndMpaController(FilmService filmService) {
        this.filmService = filmService;
    }


    @GetMapping(value = "/mpa")
    public List<Rating> getRaing() {
        return filmService.filmStorage.getAllRating();
    }

    @GetMapping(value = "/mpa/{id}")
    public Rating getRaingById(@PathVariable int id) throws NotFoundException {
        filmService.validateMPA(id);
        return filmService.filmStorage.getRatingById(id);
    }

    @GetMapping(value = "/genres")
    public List<Genre> getGenre() throws NotFoundException {
        return filmService.filmStorage.getAllGenre();
    }

    @GetMapping(value = "/genres/{id}")
    public Genre getGenreById(@PathVariable int id) throws NotFoundException {
        filmService.validateGenre(id);
        return filmService.filmStorage.getGenreById(id);
    }

    @PostMapping(value = "/mpa/{name}")
    public String createRating(@PathVariable String name) throws ValidationException {
        if (name == null) {
            throw new ValidationException("Rating is null");
        }
        filmService.filmStorage.createRating(name);
        log.info("Рейтинг добавлен в коллекцию:" + name);
        return name;
    }

    @PostMapping(value = "/genres/{name}")
    public String createGenre(@PathVariable String name) throws ValidationException {
        if (name == null) {
            throw new ValidationException("Genre is null");
        }
        filmService.filmStorage.createGenre(name);
        log.info("Жанр добавлен в коллекцию:" + name);
        return name;
    }

    @PostMapping(value = "/films/{filmId}/genres/{genreId}")
    public Film addGenreToFilm(@PathVariable int filmId,
                               @PathVariable int genreId) throws NotFoundException {
        filmService.validateAddGenre(filmId, genreId);
        filmService.filmStorage.addGenreToFilm(genreId, filmId);
        log.info("Жанр добавлен к фильму");
        return filmService.filmStorage.getById(filmId);
    }

    @PostMapping(value = "/films/{filmId}/mpa/{ratingId}")
    public String addRatingToFilm(@PathVariable int filmId,
                                  @PathVariable int ratingId) throws NotFoundException {
        filmService.validateAddRating(filmId, ratingId);
        filmService.filmStorage.addRatingToFilm(filmId, ratingId);
        log.info("Рейтинг добавлен к фильму");
        System.out.println(filmService.filmStorage.getById(filmId).getMpa().toString());
        return "Success";
    }
}
