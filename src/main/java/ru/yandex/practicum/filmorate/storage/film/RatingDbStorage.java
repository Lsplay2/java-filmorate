package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class RatingDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createRating(String ratingName) {
        String sqlQuerry = "insert into RATING(NAME)" +
                "values (?)";
        jdbcTemplate.update(sqlQuerry, ratingName);
    }

    public Rating getRatingById(int id) throws NotFoundException {
        String sqlQuery = "select RATING_ID, NAME " +
                "from RATING where RATING_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToRating, id);
    }

    public List<Rating> getAllRating() {
        String sqlQuery = "select RATING_ID, NAME " +
                "from RATING";
        return jdbcTemplate.query(sqlQuery, this::mapRowToRating);
    }

    public void addRatingToFilm(int filmId, int ratingId) {
        String sqlQuery = "update FILM set " +
                "RATING_ID = ? where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, ratingId, filmId);
    }

    protected Rating mapRowToRating(ResultSet resultSet, int rowNum) throws SQLException {
        Rating rating = Rating.builder()
                .id(resultSet.getInt("RATING_ID"))
                .name(resultSet.getString("NAME"))
                .build();
        return rating;
    }

    public boolean checkInStorageRatingById(int id) {
        String sqlQuery = "SELECT * FROM RATING WHERE RATING_ID = ?";
        List<Rating> listRating = jdbcTemplate.query(sqlQuery, this::mapRowToRating, id);
        return listRating.size() > 0;
    }
}
