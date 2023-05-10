package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmFildsException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotExistException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    final static Logger log = LoggerFactory.getLogger(FilmController.class);
    static final LocalDate MIN_DATE = LocalDate.of(1895,12,28);
    private Map<Integer,Film> films = new HashMap<>();
    private int id=0;

    private int getId(){
        return ++id;
    }
    @GetMapping
    public List<Film> getFilms(){
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) throws FilmFildsException {
        if (film.getName().isEmpty() || film.getDescription().getBytes().length>200
                || film.getReleaseDate().isBefore(MIN_DATE) || film.getDuration()<0) {
            log.error("Ошибка в одном из полей фильма");
            throw new FilmFildsException("Ошибка в одном из полей фильма");
        }
        film.setId(getId());
        films.put(film.getId(), film);
        log.info("Фильм добавлен в коллекцию:"+film);
        return film;
    }

    @PutMapping
    public Film createOrUpdateFilm(@RequestBody Film film) throws FilmNotExistException, FilmFildsException {
        if (film.getName().isEmpty() || film.getDescription().getBytes().length>200
                || film.getReleaseDate().isBefore(MIN_DATE) || film.getDuration()<0) {
            log.error("Ошибка в одном из полей фильма");
            throw new FilmFildsException("Ошибка в одном из полей фильма");
        }

        if(!films.containsKey(film.getId())){
            log.error("Ошибка фильм с таким id не создан");
            throw new FilmNotExistException("Фильма с таким id не существует");
        }
        films.put(film.getId(), film);
        log.info("Фильм обновлен в коллекции:"+film);
        return film;
    }

}
