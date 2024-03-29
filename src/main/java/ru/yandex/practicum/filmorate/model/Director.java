package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Director {

    private int id;
    private String name;

    @ManyToMany(mappedBy = "DIRECTOR", fetch = FetchType.LAZY)
    private Set<Film> filmList = new HashSet<>();

}
