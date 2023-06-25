package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private static final LocalDate MIN_DATE = LocalDate.of(1895,12,28);
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    public final FilmDbStorage filmStorage;
    public final UserDbStorage userStorage;

    @Autowired
    public FilmService(FilmDbStorage filmStorage, UserDbStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Film film, User user) {
        if (film != null && user != null) {
            filmStorage.addUserToFilm(user.getId(), film.getId());
        }
    }

    public void delLike(Film film, User user) {
        if (film != null && user != null) {
            filmStorage.dellUserToFilm(user.getId(), film.getId());
        }
    }

    public List<Film> getTopFilm(Integer count) {
        List<Film> films = new ArrayList<>(filmStorage.get().values());
        return films.stream()
                .sorted((Comparator.comparing(Film::getNumOfLike)).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public void validateAddAndDelLike(int filmId, int userId) throws NotFoundException {
        if (filmStorage.checkInStorageById(filmId) && userStorage.checkInStorageById(userId)) {
            return;
        } else {
            log.error("Ошибка в одном из id юзера или фильма");
            throw new NotFoundException("Ошибка в одном из id юзера или фильма");
        }
    }



    public void validate(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isEmpty() || film.getDescription().getBytes().length > 200
                || film.getReleaseDate().isBefore(MIN_DATE) || film.getDuration() < 0) {
            log.error("Ошибка в одном из полей фильма");
            throw new ValidationException("Ошибка в одном из полей фильма");
        }
    }

    public void validateOnUpdate(Film film) throws NotFoundException, ValidationException {
        validate(film);
        if (!filmStorage.checkInStorageById(film.getId())) {
            log.error("Ошибка фильм с таким id не создан");
            throw new NotFoundException("Фильма с таким id не существует");
        }
    }

    public void validateAddGenre (int filmId, int genreId) throws NotFoundException {
        if (!filmStorage.checkInStorageById(filmId) && filmStorage.getGenreById(genreId) == null) {
            log.error("Фильма или жанра нет в базе данных");
            throw new NotFoundException("Фильма или жанра нет в базе данных");
        }
    }

    public void validateAddRating (int filmId, int ratingId) throws NotFoundException {
        if (!filmStorage.checkInStorageById(filmId) && filmStorage.getRatingById(ratingId) == null) {
            log.error("Фильма или рейтинга нет в базе данных");
            throw new NotFoundException("Фильма или рейтинга нет в базе данных");
        }
    }

    public void validateGenre (int genreId) throws NotFoundException {
        if (!filmStorage.checkInStorageGenreById(genreId)) {
            log.error("Жанра нет в базе данных");
            throw new NotFoundException("Жанра нет в базе данных");
        }
    }

    public void validateMPA (int ratingId) throws NotFoundException {
        if (!filmStorage.checkInStorageRatingById(ratingId)) {
            log.error("Рейтинга нет в базе данных");
            throw new NotFoundException("Рейтинга нет в базе данных");
        }
    }
}
