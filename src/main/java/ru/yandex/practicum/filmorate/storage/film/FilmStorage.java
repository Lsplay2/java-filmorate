package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {
    void addFilm(Film film);
    Film getFilmById(int id);
    boolean checkFilmInStorage(Film film);
    Map<Integer,Film> getAllFilms();
}
