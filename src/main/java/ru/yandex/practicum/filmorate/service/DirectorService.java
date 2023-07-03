package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreDbStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DirectorService {

    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    private DirectorDbStorage directorDbStorage;
    private FilmDbStorage filmDbStorage;

    @Autowired
    public DirectorService(DirectorDbStorage directorDbStorage, FilmDbStorage filmDbStorage) {
        this.directorDbStorage = directorDbStorage;
        this.filmDbStorage = filmDbStorage;
    }

    public List<Director> getAll() {
        return directorDbStorage.getAllDirector();
    }

    public Director getDirectorById(int id) throws NotFoundException {
        validateDirector(id);
        return directorDbStorage.getDirectorById(id);
    }

    public void createDirector(String name) throws ValidationException {
        validateCreateDirector(name);
        directorDbStorage.createDirector(name);
    }

    public Film addDirectorToFilm(int filmId, int directorId) throws NotFoundException {
        validateAddDirector(filmId, directorId);
        directorDbStorage.addDirectorToFilm(directorId, filmId);
        return filmDbStorage.getById(filmId);
    }

    public void updateDirector(Director director) throws ValidationException, NotFoundException {
        validateUpdateDirector(director);
        directorDbStorage.updateDirector(director);
    }

    public List<Film> getSortedFilm(int directorId, String sort) throws NotFoundException {
        List<Film> films = filmDbStorage.findFilmsOnDirector(directorId);
        if (films == null || films.isEmpty()) {
            throw new NotFoundException("У данного режиссера нет фильмов");
        }
        if (sort == null || sort.equals("year")) {
            return films.stream().sorted((Comparator.comparing(Film::getReleaseDate)).reversed())
                    .collect(Collectors.toList());
        } else if (sort.equals("like")) {
            return films.stream().sorted((Comparator.comparing(Film::getNumOfLike)).reversed())
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Тип сортировки не найден");
        }
    }

    private void validateCreateDirector(String name) throws ValidationException {
        if (name == null) {
            throw new ValidationException("Genre is null");
        }
    }

    private void validateAddDirector(int filmId, int directorId) throws NotFoundException {
        if (!filmDbStorage.checkInStorageById(filmId) && directorDbStorage.getDirectorById(directorId) == null) {
            log.error("Фильма или жанра нет в базе данных");
            throw new NotFoundException("Фильма или жанра нет в базе данных");
        }
    }

    private void validateDirector(int directorId) throws NotFoundException {
        if (!directorDbStorage.checkInStorageDirectorById(directorId)) {
            log.error("Жанра нет в базе данных");
            throw new NotFoundException("Жанра нет в базе данных");
        }
    }

    private void validateUpdateDirector(Director director) throws ValidationException, NotFoundException {
        validateCreateDirector(director.getName());
        if(!directorDbStorage.checkInStorageDirectorById(director.getId())) {
            log.error("Режиссера нет в бд");
            throw new NotFoundException("Режиссера нет в бд");
        }
    }

}
