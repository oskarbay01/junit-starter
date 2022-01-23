package com.oskarbay.junit.service;


import com.oskarbay.junit.UserService;
import com.oskarbay.junit.dto.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UserServiceTest {

    private static final User AMAN = User.of(1, "Aman", "123");
    private static final User ZAMAN = User.of(2, "Zaman", "456");

    @BeforeAll
    static void init() {
        System.out.println("Before all: ");
    }

    private UserService userService;

    @BeforeEach
    void prepare() {
        System.out.println("Before each: " + this);
        userService = new UserService();
    }

    @Test
    void usersEmptyIfNoUserAdded() {
        System.out.println("Test 1: " + this);
        var users = userService.getAll();
        assertTrue(users.isEmpty());
    }

    @Test
    void loginSuccessIfUserExists() {
        userService.add(AMAN);
        Optional<User> maybeUser = userService.login(AMAN.getUsername(), AMAN.getPassword());

        assertThat(maybeUser).isPresent();
        maybeUser.ifPresent(user -> assertThat(user).isEqualTo(AMAN));

        // assertTrue(maybeUser.isPresent());
        // maybeUser.ifPresent(user -> assertEquals(AMAN, user));
    }

    @Test
    void loginFailIfPasswordIsNotCorrect() {
        userService.add(AMAN);
        var maybeUser = userService.login(AMAN.getUsername(), "qwerty");

        assertTrue(maybeUser.isEmpty());
    }

    @Test
    void usersConvertedToMapById() {
        userService.add(AMAN, ZAMAN);

        Map<Integer, User> userMap = userService.getAllConvertedById();

        assertAll(
                () -> assertThat(userMap).containsKeys(AMAN.getId(), ZAMAN.getId()),
                () -> assertThat(userMap).containsValues(AMAN, ZAMAN)
        );
    }

    @Test
    void loginFailIfUserDoesNotExist() {
        userService.add(AMAN);
        var maybeUser = userService.login("qwerty", AMAN.getPassword());

        assertTrue(maybeUser.isEmpty());
    }

    @Test
    void throwExceptionIfUsernameOrPasswordIsNull() {

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> userService.login(null, "qwerty")),
                () -> assertThrows(IllegalArgumentException.class, () -> userService.login("qwerty", null))
        );

    }

    @Test
    void usersSizeIfUserAdded() {
        System.out.println("Test 2: " + this);
        userService.add(AMAN);
        userService.add(ZAMAN);


        var users = userService.getAll();
        assertThat(users).hasSize(2);

        //assertEquals(2, users.size());
    }

    @AfterEach
    void deleteDataFromDatabases() {
        System.out.println("After each: " + this);
    }

    @AfterAll
    static void closeConnectionPool() {
        System.out.println("After all: ");

    }
}
