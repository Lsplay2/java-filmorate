package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
public class GenreController {

    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    public final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }


    @GetMapping(value = "/genres")
    public List<Genre> getGenre() {
        log.info("Поступил запрос на получение всех жанров");
        return genreService.getAll();
    }

    @GetMapping(value = "/genres/{id}")
    public Genre getGenreById(@PathVariable int id) throws NotFoundException {
        log.info("Поступил запрос на получение жанра по id:" + id);
        return genreService.getGenreById(id);
    }

    @PostMapping(value = "/genres/{name}")
    public String createGenre(@PathVariable String name) throws ValidationException {
        log.info("Поступил запрос на создание жанра:" + name);
        genreService.createGenre(name);
        log.info("Жанр добавлен в коллекцию:" + name);
        return name;
    }

    @PostMapping(value = "/films/{filmId}/genres/{genreId}")
    public Film addGenreToFilm(@PathVariable int filmId,
                               @PathVariable int genreId) throws NotFoundException {
        log.info("Поступил запрос на добавление жанра " + genreId + " к фильму " + filmId);
        Film film =  genreService.addGenreToFilm(filmId, genreId);
        log.info("Жанр добавлен к фильму");
        return film;
    }
}
