package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreDbStorage;

import java.util.List;

@Service
public class GenreService {

    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    private GenreDbStorage genreDbStorage;
    private FilmDbStorage filmDbStorage;

    @Autowired
    public GenreService(GenreDbStorage genreDbStorage, FilmDbStorage filmDbStorage) {
        this.genreDbStorage = genreDbStorage;
        this.filmDbStorage = filmDbStorage;
    }

    public List<Genre> getAll() {
        return genreDbStorage.getAllGenre();
    }

    public Genre getGenreById(int id) throws NotFoundException {
        validateGenre(id);
        return genreDbStorage.getGenreById(id);
    }

    public void createGenre(String name) throws ValidationException {
        validateCreateGenre(name);
        genreDbStorage.createGenre(name);
    }

    public Film addGenreToFilm(int filmId, int genreId) throws NotFoundException {
        validateAddGenre(filmId, genreId);
        genreDbStorage.addGenreToFilm(genreId, filmId);
        return filmDbStorage.getById(filmId);
    }

    private void validateCreateGenre(String name) throws ValidationException {
        if (name == null) {
            throw new ValidationException("Genre is null");
        }
    }

    private void validateAddGenre(int filmId, int genreId) throws NotFoundException {
        if (!filmDbStorage.checkInStorageById(filmId) && genreDbStorage.getGenreById(genreId) == null) {
            log.error("Фильма или жанра нет в базе данных");
            throw new NotFoundException("Фильма или жанра нет в базе данных");
        }
    }

    private void validateGenre(int genreId) throws NotFoundException {
        if (!genreDbStorage.checkInStorageGenreById(genreId)) {
            log.error("Жанра нет в базе данных");
            throw new NotFoundException("Жанра нет в базе данных");
        }
    }
}
