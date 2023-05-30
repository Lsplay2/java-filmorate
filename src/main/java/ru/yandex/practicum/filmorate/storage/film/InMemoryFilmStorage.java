package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Integer, Film> films = new HashMap<>();
    @Override
    public void addFilm(Film film) {
        if (film != null) {
            films.put(film.getId(), film);
        }
    }

    @Override
    public Film getFilmById(int id) {
        if (id != 0) {
            return films.get(id);
        }
        return new Film();
    }

    @Override
    public boolean checkFilmInStorage(Film film) {
        return films.containsValue(film);
    }

    public boolean checkFilmInStorageById(int id) {
        return films.containsKey(id);
    }

    @Override
    public Map<Integer, Film> getAllFilms() {
        return films;
    }
}
