package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {


    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private static final LocalDate MIN_DATE = LocalDate.of(1895,12,28);
    public Map<Integer,Film> films = new HashMap<>();
    private int id = 0;

    private int getId() {
        return ++id;
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) throws ValidationException {
        validate(film);
        film.setId(getId());
        films.put(film.getId(), film);
        log.info("Фильм добавлен в коллекцию:" + film);
        return film;


    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws ValidationException {
        validateOnUpdate(film);
        films.put(film.getId(), film);
        log.info("Фильм обновлен в коллекции:" + film);
        return film;
    }

    private boolean validate(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isEmpty() || film.getDescription().getBytes().length > 200
                || film.getReleaseDate().isBefore(MIN_DATE) || film.getDuration() < 0) {
            log.error("Ошибка в одном из полей фильма");
            throw new ValidationException("Ошибка в одном из полей фильма");
        }
        return true;
    }

    private boolean validateOnUpdate(Film film) throws ValidationException {
        validate(film);
        if (!films.containsKey(film.getId())) {
            log.error("Ошибка фильм с таким id не создан");
            throw new ValidationException("Фильма с таким id не существует");
        }
        return true;
    }
}