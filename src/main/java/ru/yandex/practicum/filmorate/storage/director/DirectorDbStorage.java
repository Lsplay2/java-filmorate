package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class DirectorDbStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createDirector(String directorName) {
        String sqlQuerry = "insert into DIRECTOR(NAME)" +
                "values (?)";
        jdbcTemplate.update(sqlQuerry, directorName);
    }

    public Director getDirectorById(int id) throws NotFoundException {
        String sqlQuery = "select DIRECTOR_ID, NAME " +
                "from DIRECTOR where DIRECTOR_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToDirector, id);
    }

    public List<Director> getAllDirector() {
        String sqlQuery = "select DIRECTOR_ID, NAME " +
                "from DIRECTOR";
        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector);
    }

    public void addDirectorToFilm(int directorId, int filmId) {
        if (!checkDirectorOnFilm(directorId, filmId)) {
            String sqlQuerry = "insert into DIRECTOR_FILM(DIRECTOR_ID, FILM_ID)" +
                    "values (?, ?)";
            jdbcTemplate.update(sqlQuerry, directorId, filmId);
        }
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getInt("DIRECTOR_ID"))
                .name(resultSet.getString("NAME"))
                .build();
    }

    public Integer mapRowToDirectorId(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("DIRECTOR_ID");
    }

    private boolean checkDirectorOnFilm(int directorId, int filmId) {
        String sqlQuery = "SELECT DIRECTOR_ID FROM DIRECTOR_FILM WHERE DIRECTOR_ID = ? AND FILM_ID = ?";
        List<Integer> listDirector = jdbcTemplate.query(sqlQuery, this::mapRowToDirectorId, directorId, filmId);
        return listDirector.size() > 0;
    }

    public void dellDirectorFromFilm(int directorId, int filmId) {
        String sqlQuerry = "delete from DIRECTOR_FILM where FILM_ID = ? and DIRECTOR_ID = ?";
        jdbcTemplate.update(sqlQuerry, filmId, directorId);
    }

    public boolean checkInStorageDirectorById(int id) {
        String sqlQuery = "SELECT * FROM DIRECTOR WHERE DIRECTOR_ID = ?";
        List<Director> listDirector = jdbcTemplate.query(sqlQuery, this::mapRowToDirector, id);
        return listDirector.size() > 0;
    }

    public void updateDirector(Director director) {
        String sqlQuery = "update DIRECTOR set NAME = ? where DIRECTOR_ID = ?";
        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
    }
}
