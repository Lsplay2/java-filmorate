package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.RatingDbStorage;

import java.util.List;

@Service
public class MpaService {
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    private RatingDbStorage ratingDbStorage;
    private FilmDbStorage filmDbStorage;

    @Autowired
    public MpaService(RatingDbStorage ratingDbStorage, FilmDbStorage filmDbStorage) {
        this.ratingDbStorage = ratingDbStorage;
        this.filmDbStorage = filmDbStorage;
    }

    public List<Rating> getAll() {
        return ratingDbStorage.getAllRating();
    }

    public Rating getById(int id) throws NotFoundException {
        validateMPA(id);
        return ratingDbStorage.getRatingById(id);
    }

    public void createRating(String name) throws ValidationException {
        validateCreateMPA(name);
        ratingDbStorage.createRating(name);
    }

    public void addRatingToFilm(int filmId, int ratingId) throws NotFoundException {
        validateAddRating(filmId, ratingId);
        ratingDbStorage.addRatingToFilm(filmId, ratingId);
    }

    private void validateCreateMPA(String name) throws ValidationException {
        if (name == null) {
            log.error("Рейтинга не может быть без имени");
            throw new ValidationException("Рейтинга не может быть без имени");
        }
    }

    private void validateAddRating(int filmId, int ratingId) throws NotFoundException {
        if (!filmDbStorage.checkInStorageById(filmId) && ratingDbStorage.getRatingById(ratingId) == null) {
            log.error("Фильма или рейтинга нет в базе данных");
            throw new NotFoundException("Фильма или рейтинга нет в базе данных");
        }
    }

    private void validateMPA(int ratingId) throws NotFoundException {
        if (!ratingDbStorage.checkInStorageRatingById(ratingId)) {
            log.error("Рейтинга нет в базе данных");
            throw new NotFoundException("Рейтинга нет в базе данных");
        }
    }
}
