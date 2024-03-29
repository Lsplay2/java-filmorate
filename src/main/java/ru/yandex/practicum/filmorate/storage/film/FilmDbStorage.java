package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RatingDbStorage ratingDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final DirectorDbStorage directorDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, RatingDbStorage ratingDbStorage, GenreDbStorage genreDbStorage,
                         DirectorDbStorage directorDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.ratingDbStorage = ratingDbStorage;
        this.genreDbStorage = genreDbStorage;
        this.directorDbStorage = directorDbStorage;
    }

    @Override
    public void add(Film film) throws NotFoundException {
        if (checkInStorageById(film.getId())) {
            String sqlQuery = "update FILM set " +
                    "NAME = ?, RELEASEDATE = ?, DURATION = ?, DESCRIPTION = ? where FILM_ID = ?";
            jdbcTemplate.update(sqlQuery, film.getName(),film.getReleaseDate(), film.getDuration(),
                    film.getDescription(), film.getId());
            if (film.getDirectors() == null) {
                String sqlQueryForDel = "delete from DIRECTOR_FILM where FILM_ID = ?";
                jdbcTemplate.update(sqlQueryForDel, film.getId());
            }
        } else {
            if (film.getId() == 0) {
                film.setId(getMaxId() + 1);
            }
                String sqlQuerry = "insert into FILM(FILM_ID, NAME, RELEASEDATE, DURATION, DESCRIPTION)" +
                        "values (?, ?, ?, ?, ?)";
                jdbcTemplate.update(sqlQuerry, film.getId(), film.getName(),
                        film.getReleaseDate(), film.getDuration(),film.getDescription());
        }

        if (film.getMpa() != null && film.getMpa().getId() != 0) {
            ratingDbStorage.addRatingToFilm(film.getId(),film.getMpa().getId());
        }
        if (film.getGenres() != null) {
            for (Genre genre : findGenreOnFilm(film.getId())) {
                genreDbStorage.dellGenreFromFilm(genre.getId(), film.getId());
            }
            for (Genre genre : film.getGenres()) {
                genreDbStorage.addGenreToFilm(genre.getId(), film.getId());
            }
        }
        film.setGenres((findGenreOnFilm(getMaxId())));
        if (film.getGenres() == null) {
            film.setGenres(new ArrayList<>());
        }

        if (film.getDirectors() != null) {
            for (Director director : findDirectorOnFilm(film.getId())) {
                directorDbStorage.dellDirectorFromFilm(director.getId(), film.getId());
            }
            for (Director director : film.getDirectors()) {
                directorDbStorage.addDirectorToFilm(director.getId(), film.getId());
            }
        }
        film.setDirectors((findDirectorOnFilm(getMaxId())));
    }


    public int getMaxId() {
        String sqlQuery = "select MAX(FILM_ID) AS MAX from FILM";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToInt);
    }

    private int mapRowToInt(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("MAX");
    }

    @Override
    public Film getById(int id) {
        String sqlQuery = "select FILM_ID, NAME, RELEASEDATE, DURATION, DESCRIPTION, RATING_ID " +
                "from FILM where FILM_ID = ?";
        Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        return film;
    }

    @Override
    public boolean checkInStorage(Film some) {
        return getById(some.getId()) != null;
    }

    @Override
    public Map<Integer, Film> get() {
        String sqlQuery = "select FILM_ID, NAME, RELEASEDATE, DURATION, DESCRIPTION, RATING_ID from FILM";
        List<Film> listFilms = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        Map<Integer, Film> mapFilms = new HashMap<>();
        for (Film film : listFilms) {
            mapFilms.put(film.getId(), film);
        }
        return mapFilms;
    }

    public boolean checkInStorageById(int id) {
        String sqlQuery = "SELECT * FROM FILM WHERE FILM_ID = ?";
        List<Film> listFilms = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, id);
        return listFilms.size() > 0;
    }

    public Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
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

        List<Director> directors = findDirectorOnFilm(film.getId());
        if (!directors.isEmpty()) {
            directors.sort(Comparator.comparing(Director::getId));
            film.setDirectors(directors);
        }
        if (film.getDirectors() == null) {
            film.setDirectors(new ArrayList<>());
        }

        int ratingId = resultSet.getInt("RATING_ID");
        if (ratingId != 0) {
            try {
                film.setMpa(ratingDbStorage.getRatingById(ratingId));
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

    private User getUserById(int userId) {
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

    public List<Genre> findGenreOnFilm(int filmId) {
        String sqlQuery = "select GENRE_ID " +
                "from GENRE_FILM where FILM_ID = ?";
        List<Integer> genresId = jdbcTemplate.query(sqlQuery,genreDbStorage::mapRowToGenreId, filmId);
        List<Genre> genres = new ArrayList<>();
        for (Integer id : genresId) {
            try {
                genres.add(genreDbStorage.getGenreById(id));
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return genres;
    }

    public List<Film> findFilmsByGenre(int genreId) {
        String sqlQuerry = "select FILM_ID from GENRE_FILM where GENRE_ID = ?";
        List<Integer> filmsId = jdbcTemplate.query(sqlQuerry, this::mapRowToFilmId, genreId);
        List<Film> films = new ArrayList<>();
        for (Integer filmId : filmsId) {
            films.add(getById(filmId));
        }
        return films;
    }


    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("USER_ID"))
                .name(resultSet.getString("NAME"))
                .email(resultSet.getString("EMAIL"))
                .login(resultSet.getString("LOGIN"))
                .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
                .build();

    }

    private Integer mapRowToUserId(ResultSet resultSet, int rowNum) throws SQLException {
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

    public void delFilm(int id) {
        String sqlQuerry1 = "delete from GENRE_FILM where FILM_ID = ?";
        jdbcTemplate.update(sqlQuerry1, id);
        String sqlQuerry2 = "delete from USER_FILM where FILM_ID = ?";
        jdbcTemplate.update(sqlQuerry2, id);
        String sqlQuerry3 = "delete from FILM where FILM_ID = ?";
        jdbcTemplate.update(sqlQuerry3, id);
    }

    public List<Director> findDirectorOnFilm(int filmId) {
        String sqlQuery = "select DIRECTOR_ID " +
                "from DIRECTOR_FILM where FILM_ID = ?";
        List<Integer> directorsId = jdbcTemplate.query(sqlQuery,directorDbStorage::mapRowToDirectorId, filmId);
        List<Director> directors = new ArrayList<>();
        for (Integer id : directorsId) {
            try {
                directors.add(directorDbStorage.getDirectorById(id));
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return directors;
    }

    public List<Film> findFilmsOnDirector(int directorId) {
        String sqlQuerry = "select FILM_ID from DIRECTOR_FILM where DIRECTOR_ID = ?";
        List<Integer> filmsId = jdbcTemplate.query(sqlQuerry, this::mapRowToFilmId, directorId);
        List<Film> films = new ArrayList<>();
        for (Integer filmId : filmsId) {
            films.add(getById(filmId));
        }
        return films;
    }

    private int mapRowToFilmId(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("FILM_ID");
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        String sqlQuery = "SELECT film.FILM_ID, film.NAME, film.RELEASEDATE, film.DURATION, film.DESCRIPTION, film.RATING_ID " +
                "FROM FILM film " +
                "JOIN USER_FILM userFilm1 ON film.FILM_ID = userFilm1.FILM_ID AND userFilm1.USER_ID = ? " +
                "JOIN USER_FILM userFilm2 ON film.FILM_ID = userFilm2.FILM_ID AND userFilm2.USER_ID = ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, userId, friendId);
    }
}
