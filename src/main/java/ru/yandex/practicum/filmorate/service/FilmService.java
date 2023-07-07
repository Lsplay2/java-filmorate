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
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private static final LocalDate MIN_DATE = LocalDate.of(1895,12,28);
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    @Autowired
    public FilmService(FilmDbStorage filmStorage, UserDbStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film getById(int id) throws NotFoundException {
        if (filmStorage.checkInStorageById(id)) {
            return filmStorage.getById(id);
        }
        throw new NotFoundException("Фильм не найден");
    }

    public List<Film> getAll() throws NotFoundException {
        return new ArrayList<>(filmStorage.get().values());
    }

    public Film createFilm(Film film) throws ValidationException, NotFoundException {
        validate(film);
        filmStorage.add(film);
        return filmStorage.getById(filmStorage.getMaxId());
    }

    public Film updateFilm(Film film) throws ValidationException, NotFoundException {
        validateOnUpdate(film);
        filmStorage.add(film);
        return filmStorage.getById(film.getId());
    }

    public void delFilm(int id) {
        if (filmStorage.checkInStorageById(id)) {
            filmStorage.delFilm(id);
        } else {
            log.error("Ошибка в id фильма");
        }
    }

    public void addLike(int filmId, int userId) throws NotFoundException {
        validateAddAndDelLike(filmId, userId);
        if (filmStorage.checkInStorageById(filmId)
                && userStorage.checkInStorageById(userId)) {
            filmStorage.addUserToFilm(userId, filmId);
        }
    }

    public void delLike(int filmId, int userId) throws NotFoundException {
        validateAddAndDelLike(filmId, userId);
        if (filmStorage.checkInStorageById(filmId)
                && userStorage.checkInStorageById(userId)) {
            filmStorage.dellUserToFilm(userId, filmId);
        }
    }

    public List<Film> getTopFilmByGenreOrYear(Integer count, int genreId, int year) {
        Set<Film> films = new HashSet<>();
        if (genreId != 0 && year != 0) {
            for (Film film : findFilmByGenre(genreId)) {
                if (film.getReleaseDate().getYear() == year) {
                    films.add(film);
                }
            }
            for (Film film : findFilmByYear(year)) {
                for (Genre genre : film.getGenres()) {
                    if (genre.getId() == genreId) {
                        films.add(film);
                    }
                }
            }
        } else if (genreId != 0 && year == 0) {
            films.addAll(findFilmByGenre(genreId));

        } else if (genreId == 0 && year != 0) {
            films.addAll(findFilmByYear(year));

        } else {
            films.addAll(findFilmWithoutAll());
        }

        return films.stream()
                .sorted((Comparator.comparing(Film::getNumOfLike)).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateAddAndDelLike(int filmId, int userId) throws NotFoundException {
        if (filmStorage.checkInStorageById(filmId) && userStorage.checkInStorageById(userId)) {
            return;
        } else {
            log.error("Ошибка в одном из id юзера или фильма");
            throw new NotFoundException("Ошибка в одном из id юзера или фильма");
        }
    }

    private void validate(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isEmpty() || film.getDescription().getBytes().length > 200
                || film.getReleaseDate().isBefore(MIN_DATE) || film.getDuration() < 0) {
            log.error("Ошибка в одном из полей фильма");
            throw new ValidationException("Ошибка в одном из полей фильма");
        }
    }

    private void validateOnUpdate(Film film) throws NotFoundException, ValidationException {
        validate(film);
        if (!filmStorage.checkInStorageById(film.getId())) {
            log.error("Ошибка фильм с таким id не создан");
            throw new NotFoundException("Фильма с таким id не существует");
        }
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    private List<Film> findFilmByYear(int year) {
        List<Film> films = new ArrayList<>();
        for(Film film : filmStorage.get().values()) {
            if(film.getReleaseDate().getYear() == year) {
                films.add(film);
            }
        }
        System.out.println(films);
        return films;
    }

    private List<Film> findFilmByGenre(int genreId) {
        List<Film> films = filmStorage.findFilmsByGenre(genreId);
        System.out.println(films);
        return films;
    }

    private List<Film> findFilmWithoutAll() {
        List<Film> films = new ArrayList<>(filmStorage.get().values());
        System.out.println(films);
        return films;
    }
}
