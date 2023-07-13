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
import ru.yandex.practicum.filmorate.model.feed.EventOperation;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final EventService eventService;

    @Autowired
    public FilmService(FilmDbStorage filmStorage, UserDbStorage userStorage, EventService eventService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.eventService = eventService;
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
        eventService.createEvent(userId, EventType.LIKE, EventOperation.ADD, filmId);
    }

    public void delLike(int filmId, int userId) throws NotFoundException {
        validateAddAndDelLike(filmId, userId);
        if (filmStorage.checkInStorageById(filmId)
                && userStorage.checkInStorageById(userId)) {
            filmStorage.dellUserToFilm(userId, filmId);
        }
        eventService.createEvent(userId, EventType.LIKE, EventOperation.REMOVE, filmId);
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
        for (Film film : filmStorage.get().values()) {
            if (film.getReleaseDate().getYear() == year) {
                films.add(film);
            }
        }
        return films;
    }

    private List<Film> findFilmByGenre(int genreId) {
        return filmStorage.findFilmsByGenre(genreId);
    }

    private List<Film> findFilmWithoutAll() {
        return new ArrayList<>(filmStorage.get().values());
    }

    public List<Film> getSearchedFilms(String query, String by) {
        log.info("SERVICE getSearchedFilms query {}, by {}", query, by);
        if (by.equals("director")) {
            return getSearchByDirector(query);
        } else if (by.equals("title")) {
            return getSearchByTitle(query);
        } else if (by.equals("director,title") || by.equals("title,director")) {
            return getSearchByTitleDirector(query);
        } else {
            return null;
        }
    }

    private List<Film> getSearchByTitleDirector(String query) {
        log.info("GETSEARCH TITLE DIRECTOR query {}", query);
        List<Film> films = new ArrayList<>(filmStorage.get().values());
        return films.stream()
                .filter(film -> filterForSearchDirector(query, film) || filterForSearchTitle(query, film))
                .sorted((Comparator.comparing(Film::getNumOfLike)).reversed())
                .collect(Collectors.toList());
    }

    private List<Film> getSearchByTitle(String query) {
        log.info("GETSEARCH TITLE query {}", query);
        List<Film> films = new ArrayList<>(filmStorage.get().values());
        return films.stream()
                .filter(film -> filterForSearchTitle(query, film))
                .sorted((Comparator.comparing(Film::getNumOfLike)).reversed())
                .collect(Collectors.toList());
    }

    private List<Film> getSearchByDirector(String query) {
        log.info("GETSEARCH DIRECTOR query {}", query);
        List<Film> films = new ArrayList<>(filmStorage.get().values());
        return films.stream()
                .filter(film -> filterForSearchDirector(query, film))
                .sorted((Comparator.comparing(Film::getNumOfLike)).reversed())
                .collect(Collectors.toList());
    }

    private Boolean filterForSearchDirector(String query, Film film) {
        List<Director> directors = film.getDirectors();
        return directors.stream()
                .filter(director -> director.getName()
                        .toLowerCase().contains(query.toLowerCase())).toArray().length > 0;
    }

    private Boolean filterForSearchTitle(String query, Film film) {
        return film.getName().toLowerCase().contains(query.toLowerCase());
    }
}
