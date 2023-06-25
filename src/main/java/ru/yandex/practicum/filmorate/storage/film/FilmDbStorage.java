package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class FilmDbStorage implements FilmStorage{
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public FilmDbStorage (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void add (Film film) throws NotFoundException {
        if (checkInStorageById(film.getId())) {
            String sqlQuery = "update FILM set " +
                    "NAME = ?, RELEASEDATE = ?, DURATION = ?, DESCRIPTION = ? where FILM_ID = ?";
            jdbcTemplate.update(sqlQuery, film.getName(),film.getReleaseDate(), film.getDuration(),
                    film.getDescription(), film.getId());
        } else {
            if (film.getId() == 0) {
                film.setId(getMaxId()+1);
            }
                String sqlQuerry = "insert into FILM(FILM_ID, NAME, RELEASEDATE, DURATION, DESCRIPTION)" +
                        "values (?, ?, ?, ?, ?)";
                jdbcTemplate.update(sqlQuerry, film.getId(), film.getName(),
                        film.getReleaseDate(), film.getDuration(),film.getDescription());
        }
        if (film.getMpa().getId() != 0) {
            addRatingToFilm(film.getId(),film.getMpa().getId());
        }
        if (film.getGenres() != null) {
            for (Genre genre : findGenreOnFilm(film.getId())) {
                dellGenreFromFilm(genre.getId(), film.getId());
            }
            for (Genre genre : film.getGenres()) {
                addGenreToFilm(genre.getId(), film.getId());
            }
        }
        film.setGenres((findGenreOnFilm(getMaxId())));
        if (film.getGenres() == null) {
            film.setGenres(new ArrayList<>());
        }
    }

    public int getMaxId() {
        String sqlQuery = "select MAX(FILM_ID) AS MAX from FILM";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToInt);
    }

    private int mapRowToInt(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("MAX");
    }
    @Override
    public Film getById (int id) {
        String sqlQuery = "select FILM_ID, NAME, RELEASEDATE, DURATION, DESCRIPTION, RATING_ID " +
                "from FILM where FILM_ID = ?";
        Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        return film;
    }

    @Override
    public boolean checkInStorage (Film some) {
        return getById(some.getId()) != null;
    }

    @Override
    public Map<Integer, Film> get () {
        String sqlQuery = "select FILM_ID, NAME, RELEASEDATE, DURATION, DESCRIPTION, RATING_ID from FILM";
        List<Film> listFilms = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        Map<Integer, Film> mapFilms = new HashMap<>();
        for (Film film : listFilms) {
            mapFilms.put(film.getId(), film);
        }
        return mapFilms;
    }

    public boolean checkInStorageById (int id) {
        String sqlQuery = "SELECT * FROM FILM WHERE FILM_ID = ?";
        List<Film> listFilms = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, id);
        return listFilms.size() > 0;
    }

    public boolean checkInStorageGenreById (int id) {
        String sqlQuery = "SELECT * FROM GENRE WHERE GENRE_ID = ?";
        List<Genre> listGenre = jdbcTemplate.query(sqlQuery, this::mapRowToGenre, id);
        return listGenre.size() > 0;
    }

    public boolean checkInStorageRatingById (int id) {
        String sqlQuery = "SELECT * FROM RATING WHERE RATING_ID = ?";
        List<Rating> listRating = jdbcTemplate.query(sqlQuery, this::mapRowToRating, id);
        return listRating.size() > 0;
    }

    private Film mapRowToFilm (ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getInt("FILM_ID"))
                .name(resultSet.getString("NAME"))
                .releaseDate(resultSet.getDate("RELEASEDATE").toLocalDate())
                .description(resultSet.getString("DESCRIPTION"))
                .duration(resultSet.getInt("DURATION"))
                .build();

        List<Genre> genres = findGenreOnFilm(film.getId());
        if (!genres.isEmpty()) {
            genres.sort(Comparator.comparing(Genre::getId));
            film.setGenres(genres);
        }
        if (film.getGenres() == null) {
            film.setGenres(new ArrayList<>());
        }
        int ratingId = resultSet.getInt("RATING_ID");
        if (ratingId != 0) {
            try {
                film.setMpa(getRatingById(ratingId));
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        List<User> users = findUserOnFilm(film.getId());
        if (!users.isEmpty()) {
            film.setUsers(new HashSet<>(users));
            film.setNumOfLike(users.size());
        }
        return film;
    }
    ///Rating
    public void createRating (String ratingName) {
        String sqlQuerry = "insert into RATING(NAME)" +
                "values (?)";
        jdbcTemplate.update(sqlQuerry, ratingName);
    }

    public Rating getRatingById (int id) throws NotFoundException {
        String sqlQuery = "select RATING_ID, NAME " +
                "from RATING where RATING_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToRating, id);
    }

    public List<Rating> getAllRating () {
        String sqlQuery = "select RATING_ID, NAME " +
                "from RATING";
        return jdbcTemplate.query(sqlQuery, this::mapRowToRating);
    }

    public void addRatingToFilm (int filmId, int ratingId) {
        String sqlQuery = "update FILM set " +
                "RATING_ID = ? where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, ratingId, filmId);
    }

    private Rating mapRowToRating (ResultSet resultSet, int rowNum) throws SQLException {
        Rating rating = Rating.builder()
                .id(resultSet.getInt("RATING_ID"))
                .name(resultSet.getString("NAME"))
                .build();
        return rating;
    }

    //Film_user
    private User getUserById (int userId) {
        String sqlQuery = "select USER_ID, NAME, EMAIL, LOGIN, BIRTHDAY " +
                "from USERS where USER_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, userId);
    }

    public List<User> findUserOnFilm(int userId) {
        String sqlQuery = "select USER_ID " +
                "from USER_FILM where FILM_ID = ?";
        List<Integer> usersId = jdbcTemplate.query(sqlQuery,this::mapRowToUserId, userId);
        List<User> users = new ArrayList<>();
        for (Integer id : usersId) {
            users.add(getUserById(id));
        }
        return users;
    }

    private User mapRowToUser (ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("USER_ID"))
                .name(resultSet.getString("NAME"))
                .email(resultSet.getString("EMAIL"))
                .login(resultSet.getString("LOGIN"))
                .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
                .build();

    }

    private Integer mapRowToUserId (ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("USER_ID");
    }

    public void addUserToFilm(int userId, int filmId) {
        String sqlQuerry = "insert into USER_FILM(USER_ID, FILM_ID)" +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuerry, userId, filmId);
    }

    public void dellUserToFilm(int userId, int filmId) {
        String sqlQuerry = "delete from USER_FILM where USER_ID = ? and FILM_ID = ?";
        jdbcTemplate.update(sqlQuerry, userId, filmId);
    }

    /// Genre
    public void createGenre (String genreName) {
        String sqlQuerry = "insert into GENRE(NAME)" +
                "values (?)";
        jdbcTemplate.update(sqlQuerry, genreName);
    }

    public Genre getGenreById (int id) throws NotFoundException {
        String sqlQuery = "select GENRE_ID, NAME " +
                "from GENRE where GENRE_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
    }

    public List<Genre> getAllGenre () {
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
    private Genre mapRowToGenre (ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("GENRE_ID"))
                .name(resultSet.getString("NAME"))
                .build();
    }

    private Integer mapRowToGenreId (ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("GENRE_ID");
    }

    public List<Genre> findGenreOnFilm(int filmId) {
        String sqlQuery = "select GENRE_ID " +
                "from GENRE_FILM where FILM_ID = ?";
        List<Integer> genresId = jdbcTemplate.query(sqlQuery,this::mapRowToGenreId, filmId);
        List<Genre> genres = new ArrayList<>();
        for (Integer id : genresId) {
            try {
                genres.add(getGenreById(id));
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return genres;
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
}
