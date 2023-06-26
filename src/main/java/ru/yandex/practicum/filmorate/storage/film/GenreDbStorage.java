package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createGenre(String genreName) {
        String sqlQuerry = "insert into GENRE(NAME)" +
                "values (?)";
        jdbcTemplate.update(sqlQuerry, genreName);
    }

    public Genre getGenreById(int id) throws NotFoundException {
        String sqlQuery = "select GENRE_ID, NAME " +
                "from GENRE where GENRE_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
    }

    public List<Genre> getAllGenre() {
        String sqlQuery = "select GENRE_ID, NAME " +
                "from GENRE";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    public void addGenreToFilm(int genreId, int filmId) {
        if (!checkGenreOnFilm(genreId, filmId)) {
            String sqlQuerry = "insert into GENRE_FILM(GENRE_ID, FILM_ID)" +
                    "values (?, ?)";
            jdbcTemplate.update(sqlQuerry, genreId, filmId);
        }
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("GENRE_ID"))
                .name(resultSet.getString("NAME"))
                .build();
    }

    protected Integer mapRowToGenreId(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("GENRE_ID");
    }

    private boolean checkGenreOnFilm(int genreId, int filmId) {
        String sqlQuery = "SELECT GENRE_ID FROM GENRE_FILM WHERE GENRE_ID = ? AND FILM_ID = ?";
        List<Integer> listGenre = jdbcTemplate.query(sqlQuery, this::mapRowToGenreId, genreId, filmId);
        return listGenre.size() > 0;
    }

    public void dellGenreFromFilm(int genreId, int filmId) {
        String sqlQuerry = "delete from GENRE_FILM where FILM_ID = ? and GENRE_ID = ?";
        jdbcTemplate.update(sqlQuerry, filmId, genreId);
    }

    public boolean checkInStorageGenreById(int id) {
        String sqlQuery = "SELECT * FROM GENRE WHERE GENRE_ID = ?";
        List<Genre> listGenre = jdbcTemplate.query(sqlQuery, this::mapRowToGenre, id);
        return listGenre.size() > 0;
    }
}
