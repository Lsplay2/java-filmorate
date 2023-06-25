package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Builder
public class Rating {
    private int id;
    private String name;

    @OneToMany(mappedBy = "RATING", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private Set<Film> films;

    public void addFilm(Film film) {
        films.add(film);
    }
}
