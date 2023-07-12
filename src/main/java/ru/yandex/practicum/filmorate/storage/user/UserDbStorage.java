package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, FilmDbStorage filmDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmDbStorage = filmDbStorage;
    }

    @Override
    public void add(User user) throws NotFoundException {
        if (checkInStorageById(user.getId())) {
            String sqlQuery = "update USERS set " +
                    "NAME = ?, EMAIL = ?, LOGIN = ?, BIRTHDAY = ? where USER_ID = ?";
            jdbcTemplate.update(sqlQuery, user.getName(), user.getEmail(), user.getLogin(),
                    user.getBirthday(), user.getId());
        } else {
            if (user.getId() == 0) {
                user.setId(getMaxId() + 1);
            }
            String sqlQuerry = "insert into USERS(USER_ID, NAME, EMAIL, LOGIN, BIRTHDAY)" +
                    "values (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sqlQuerry, user.getId(), user.getName(), user.getEmail(), user.getLogin(), user.getBirthday());
        }
    }

    public int getMaxId() {
        String sqlQuery = "select MAX(USER_ID) AS MAX from USERS";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToInt);
    }

    private int mapRowToInt(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("MAX");
    }

    @Override
    public User getById(int id) throws NotFoundException {
        String sqlQuery = "select USER_ID, NAME, EMAIL, LOGIN, BIRTHDAY " +
                "from USERS where USER_ID = ?";
        User user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        return user;
    }

    @Override
    public boolean checkInStorage(User some) {
        return checkInStorageById(some.getId());
    }

    public boolean checkInStorageById(int id) {
        String sqlQuery = "SELECT * FROM USERS WHERE USER_ID = ?";
        List<User> listUsers = jdbcTemplate.query(sqlQuery, this::mapRowToUser, id);
        return listUsers.size() != 0;
    }

    private Boolean mapRowForCheck(ResultSet resultSet, int rowSet) throws SQLException {
        return resultSet.getBoolean("CHECK");
    }

    @Override
    public Map<Integer, User> get() {
        String sqlQuery = "select USER_ID, NAME, USER_ID, NAME, EMAIL, LOGIN, BIRTHDAY from USERS";
        List<User> listUsers = jdbcTemplate.query(sqlQuery, this::mapRowToUser);
        Map<Integer, User> mapUsers = new HashMap<>();
        for (User user : listUsers) {
            mapUsers.put(user.getId(), user);
        }
        return mapUsers;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = User.builder()
                .id(resultSet.getInt("USER_ID"))
                .name(resultSet.getString("NAME"))
                .email(resultSet.getString("EMAIL"))
                .login(resultSet.getString("LOGIN"))
                .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
                .build();
        List<Film> films = findFilmOnUsers(user.getId());
        if (!films.isEmpty()) {
            for (Film film : films) {
                film.setUsers(new HashSet<>());
            }
            user.setFilms(new HashSet<>(films));
        }

        return user;
    }

    public void addUserToFilm(int userId, int filmId) {
        String sqlQuerry = "insert into USER_FILM(USER_ID, FILM_ID)" +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuerry, userId, filmId);
    }

    private Film getFilmById(int filmId) {
        String sqlQuery = "select FILM_ID, NAME, RELEASEDATE, DURATION, DESCRIPTION, RATING_ID " +
                "from FILM where FILM_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, filmId);
    }

    public List<Film> findFilmOnUsers(int userId) {
        List<Integer> filmId = getIdFilmsListFromUserFilm(userId);
        List<Film> films = new ArrayList<>();
        for (Integer id : filmId) {
            films.add(getFilmById(id));
        }
        return films;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("FILM_ID"))
                .name(resultSet.getString("NAME"))
                .releaseDate(resultSet.getDate("RELEASEDATE").toLocalDate())
                .description(resultSet.getString("DESCRIPTION"))
                .duration(resultSet.getInt("DURATION"))
                .build();
    }

    private Integer mapRowToFilmId(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("FILM_ID");
    }

    public void addUserToFriend(int userId, int friendId) {
        String sqlQuerry = "insert into USER_FRIEND(USER_ID, FRIEND_ID, CONFIRM)" +
                "values (?, ?, ?)";
        jdbcTemplate.update(sqlQuerry, userId, friendId, false);
    }

    public void confirmFriend(int userId, int friendId) {
        String sqlQuerry = "update USER_FRIEND set CONFIRM = ? WHERE FRIEND_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sqlQuerry, true, userId, friendId);
    }

    public void delFriendFromUser(int userId, int friendId) {
        String sqlQuerry = "delete from USER_FRIEND where USER_ID = ? and FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuerry, userId, friendId);
    }

    public List<User> findFriendOnUsers(int userId) {
        String sqlQuery = "select FRIEND_ID " +
                "from USER_FRIEND where USER_ID = ?";
        List<Integer> usersId = jdbcTemplate.query(sqlQuery, this::mapRowToUserId, userId);

        String sqlQuery2 = "select USER_ID " +
                "from USER_FRIEND where FRIEND_ID = ? AND CONFIRM ";
        List<Integer> usersId2 = jdbcTemplate.query(sqlQuery2, this::mapRowToFriendId, userId);

        usersId.addAll(usersId2);
        List<User> users = new ArrayList<>();
        for (Integer id : usersId) {
            try {
                users.add(getById(id));
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return users;
    }

    private Integer mapRowToUserId(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("FRIEND_ID");
    }

    private Integer mapRowToFriendId(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("USER_ID");
    }

    public List<User> getSame(int userId, int friendId) {
        List<User> userFriends = findFriendOnUsers(userId);
        List<User> friendFriends = findFriendOnUsers(friendId);
        return checkSame(userFriends, friendFriends);
    }

    private static List<User> checkSame(List<User> first, List<User> second) {
        List<User> common = new ArrayList<>(first);
        common.retainAll(second);
        return common;
    }

    public void delUser(int id) {
        String sqlQuerry1 = "delete from USER_FILM where USER_ID = ?";
        jdbcTemplate.update(sqlQuerry1, id);
        String sqlQuerry2 = "delete from USER_FRIEND where USER_ID = ? OR FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuerry2, id, id);
        String sqlQuerry3 = "delete from USERS where USER_ID = ?";
        jdbcTemplate.update(sqlQuerry3, id);
    }

    public List<Film> getRecommendations(int id) {
        String sqlQuery = "SELECT f.* FROM film f " +
                "JOIN (SELECT DISTINCT l2.film_id, COUNT(*) rel " +
                "FROM USER_FILM l1 " +
                "LEFT JOIN USER_FILM l2 ON l1.user_id = l2.user_id " +
                "WHERE l1.film_id IN (SELECT film_id FROM USER_FILM WHERE user_id = ?) " +
                "AND l2.film_id NOT IN (SELECT film_id FROM USER_FILM WHERE user_id = ?) " +
                "GROUP BY l1.user_id, l2.film_id " +
                "ORDER BY rel DESC) AS r " +
                "ON r.film_id = f.film_id";

        List<Film> recommendations = jdbcTemplate.query(sqlQuery, filmDbStorage::mapRowToFilm, id, id);
        return recommendations;
    }

    private List<Integer> getIdFilmsListFromUserFilm(int id) {
        String sqlQuery = "select FILM_ID from USER_FILM where USER_ID = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilmId, id);
    }
}
