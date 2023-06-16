package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Genre genre;
    private Rating rating;
    private Set<User> numOfLike = new HashSet<>();

    public void addLike(User user) {
        numOfLike.add(user);
    }

    public void delLike(User user) {
        numOfLike.remove(user);
    }

    public int getNumberOfLike() {
        return numOfLike.size();
    }

}

enum Genre {
    Comedy,
    Drama,
    Cartoon,
    Thriller,
    Documentary,
    Action
}

enum Rating {
    G,
    PG,
    PG13,
    R,
    NC17
}
