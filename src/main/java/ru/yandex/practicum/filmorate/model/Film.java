package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
@Builder
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;

    private int numOfLike;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "GENRE_FILM",
            joinColumns = {
                    @JoinColumn(name = "FILM_ID", referencedColumnName = "FILM_ID")},
            inverseJoinColumns = {
                    @JoinColumn(name = "GENRE_ID", referencedColumnName = "GENRE_ID")})
    private List<Genre> genres = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RATING_ID")
    private Rating mpa;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "USER_FILMS",
            joinColumns = {
                    @JoinColumn(name = "FILM_ID", referencedColumnName = "FILM_ID")},
            inverseJoinColumns = {
                    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")})
    private Set<User> users = new HashSet<>();
    public int getNumOfLike() {
        return numOfLike;
    }
}