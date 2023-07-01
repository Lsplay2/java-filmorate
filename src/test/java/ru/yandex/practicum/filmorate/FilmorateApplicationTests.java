package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
	private final UserDbStorage userStorage;
	private final FilmDbStorage filmStorage;

	@Test
	public void testFindUserById() throws NotFoundException {
		userStorage.add(User.builder().name("qwe").id(1).login("Lsplay")
				.email("qwe@qwe").birthday(LocalDate.now().minusYears(10)).build());
		Optional<User> userOptional = Optional.ofNullable(userStorage.getById(1));

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", 1)
				);
	}

	@Test
	public void testUpdateUser() throws NotFoundException {
		userStorage.add(User.builder().name("qwe").id(1).login("Lsplay")
				.email("qwe@qwe").birthday(LocalDate.now().minusYears(10)).build());
		userStorage.add(User.builder().name("zzz").id(1).login("zzzz")
				.email("qwe@qwe").birthday(LocalDate.now().minusYears(10)).build());
		Optional<User> userOptional = Optional.ofNullable(userStorage.getById(1));

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("name", "zzz")
				);
	}

	@Test
	public void testGetAllUser() throws NotFoundException {
		userStorage.add(User.builder().name("qwe").id(1).login("Lsplay")
				.email("qwe@qwe").birthday(LocalDate.now().minusYears(10)).build());
		userStorage.add(User.builder().name("zzz").id(2).login("zzzz")
				.email("qwe@qwe").birthday(LocalDate.now().minusYears(10)).build());
		List<User> userList = new ArrayList<>(userStorage.get().values());
		Optional<User> userOptional = Optional.ofNullable(userList.get(0));
		Optional<User> userOptional2 = Optional.ofNullable(userList.get(1));
		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("name", "qwe")
				);
		assertThat(userOptional2)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("name", "zzz")
				);
	}

	@Test
	public void testAddFriend() throws NotFoundException {
		userStorage.add(User.builder().name("qwe").id(1).login("Lsplay")
				.email("qwe@qwe").birthday(LocalDate.now().minusYears(10)).build());
		userStorage.add(User.builder().name("zzz").id(2).login("zzzz")
				.email("qwe@qwe").birthday(LocalDate.now().minusYears(10)).build());
		userStorage.addUserToFriend(1,2);
		List<User> userList = userStorage.findFriendOnUsers(1);
		Optional<User> userOptional = Optional.ofNullable(userList.get(0));
		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("name", "zzz")
				);
	}

	@Test
	public void testConfFriend() throws NotFoundException {
		userStorage.add(User.builder().name("qwe").id(1).login("Lsplay")
				.email("qwe@qwe").birthday(LocalDate.now().minusYears(10)).build());
		userStorage.add(User.builder().name("zzz").id(2).login("zzzz")
				.email("qwe@qwe").birthday(LocalDate.now().minusYears(10)).build());
		userStorage.addUserToFriend(1,2);
		userStorage.confirmFriend(2,1);
		List<User> userList = userStorage.findFriendOnUsers(2);
		Optional<User> userOptional = Optional.ofNullable(userList.get(0));
		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("name", "qwe")
				);
	}

	@Test
	public void testDelFriend() throws NotFoundException {
		userStorage.add(User.builder().name("qwe").id(1).login("Lsplay")
				.email("qwe@qwe").birthday(LocalDate.now().minusYears(10)).build());
		userStorage.add(User.builder().name("zzz").id(2).login("zzzz")
				.email("qwe@qwe").birthday(LocalDate.now().minusYears(10)).build());
		userStorage.addUserToFriend(1,2);
		List<User> userList = userStorage.findFriendOnUsers(1);
		Optional<User> userOptional = Optional.ofNullable(userList.get(0));
		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("name", "zzz")
				);
		userStorage.delFriendFromUser(1,2);
		List<User> userList2 = userStorage.findFriendOnUsers(1);
		Optional<Integer> userOptional2 = Optional.of(userList2.size());
		assertThat(userOptional2)
				.isPresent()
				.hasValue(0);
	}

	@Test
	public void tesCreateFilm() throws NotFoundException {
		filmStorage.add(Film.builder().name("qwe").duration(120)
				.releaseDate(LocalDate.now().minusYears(4)).description("aaa").build());
		Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getById(1));
		assertThat(filmOptional)
				.isPresent()
				.hasValueSatisfying(film ->
						assertThat(film).hasFieldOrPropertyWithValue("name", "qwe"));
	}
}