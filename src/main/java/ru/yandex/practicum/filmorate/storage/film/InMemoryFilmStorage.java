package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Integer, Film> films = new HashMap<>();

    public void add(Film film) {
        if (film != null) {
            films.put(film.getId(), film);
        }
    }

    @Override
    public Film getById(int id) throws NotFoundException {
        if (id != 0) {
            return films.get(id);
        }
        throw new NotFoundException("Такоко id не существует");
    }

    @Override
    public boolean checkInStorage(Film film) {
        return films.containsValue(film);
    }

    public boolean checkInStorageById(int id) {
        return films.containsKey(id);
    }

    public Map<Integer, Film> get() {
        return new HashMap<>(films);
    }
}
