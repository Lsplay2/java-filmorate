package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    public void addLike(Film film, User user) {
        if (film != null && user != null) {
            film.addLike(user);
        }
    }

    public void delLike(Film film, User user) {
        if (film != null && user != null) {
            film.delLike(user);
        }
    }

    public List<Film> getTopFilm(InMemoryFilmStorage filmStorage, Integer count) {
        List<Film> films = new ArrayList<>(filmStorage.getAllFilms().values());
        return films.stream()
                .sorted((Comparator.comparing(Film::getNumberOfLike)).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
