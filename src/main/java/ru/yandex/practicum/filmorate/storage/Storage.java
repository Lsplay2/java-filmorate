package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import java.util.Map;

public interface Storage<T> {
    void add(T some) throws NotFoundException;

    T getById(int id) throws NotFoundException;

    boolean checkInStorage(T some) throws NotFoundException;

    Map<Integer, T> get();

}
