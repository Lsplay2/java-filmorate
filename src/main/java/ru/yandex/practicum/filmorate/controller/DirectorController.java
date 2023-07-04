package ru.yandex.practicum.filmorate.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
public class DirectorController {
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    public final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping(value = "/directors")
    public List<Director> getAllDirectors() {
        return directorService.getAll();
    }

    @GetMapping(value = "/directors/{id}")
    public Director getDirectorById(@PathVariable int id) throws NotFoundException {
        return directorService.getDirectorById(id);
    }

    @PostMapping(value = "/directors")
    public Director createDirector(@RequestBody Director director) throws ValidationException, NotFoundException {
        Director directorBD = directorService.createDirector(director.getName());
        log.info("Создан режиссер с именем " + directorBD.getName());
        return directorBD;
    }

    @PutMapping(value = "/directors")
    public Director updateDirector(@RequestBody Director director) throws ValidationException, NotFoundException {
        Director directorBD = directorService.updateDirector(director);
        log.info("Режиссер изменен " + director.getName());
        return directorBD;
    }

    @GetMapping(value = "/films/director/{directorId}")
    public List<Film> filmListByDirector(@PathVariable int directorId,
                                         @RequestParam(required = false, value = "sortBy") String sortBy)
            throws NotFoundException {
        return directorService.getSortedFilm(directorId, sortBy);
    }

    @DeleteMapping(value = "/directors/{id}")
    public String deleteDirector(@PathVariable int id) {
        directorService.deleteDirector(id);
        log.info("Удален режиссер с id " + id);
        return "Успешно";
    }
}
