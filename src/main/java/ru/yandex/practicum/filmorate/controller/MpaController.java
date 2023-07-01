package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
public class MpaController {

    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    public final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping(value = "/mpa")
    public List<Rating> getRaing() {
        return mpaService.getAll();
    }

    @GetMapping(value = "/mpa/{id}")
    public Rating getRaingById(@PathVariable int id) throws NotFoundException {
        return mpaService.getById(id);
    }

    @PostMapping(value = "/mpa/{name}")
    public String createRating(@PathVariable String name) throws ValidationException {
        mpaService.createRating(name);
        log.info("Рейтинг добавлен в коллекцию:" + name);
        return name;
    }

    @PostMapping(value = "/films/{filmId}/mpa/{ratingId}")
    public String addRatingToFilm(@PathVariable int filmId,
                                  @PathVariable int ratingId) throws NotFoundException {
        mpaService.addRatingToFilm(filmId, ratingId);
        log.info("Рейтинг добавлен к фильму");
        return "Success";
    }
}
