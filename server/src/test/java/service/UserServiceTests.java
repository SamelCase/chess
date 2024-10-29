package service;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService userService;
    private DataAccess dataAccess;

    @BeforeEach
    void setUp() {
        dataAccess = new MemDataAccess();
        userService = new UserService(dataAccess);
    }
    @Test
    void register_success() throws DataAccessException {
        UserData userData = new UserData("newUser", "password", "email@example.com");
        AuthData result = userService.register(userData);
        assertNotNull(result);
        assertEquals("newUser", result.username());
    }

    @Test
    void register_userAlreadyExists() {
        UserData userData = new UserData("existingUser", "password", "email@example.com");
        assertThrows(DataAccessException.class, () -> {
            userService.register(userData);
            userService.register(userData); // Trying to register the same user again
        });
    }

    @Test
    void login_success() throws DataAccessException {
        UserData userData = new UserData("user", "password", "email@example.com");
        userService.register(userData);
        AuthData result = userService.login("user", "password");
        assertNotNull(result);
        assertEquals("user", result.username());
    }

    @Test
    void login_invalidCredentials() {
        assertThrows(DataAccessException.class, () ->
                userService.login("nonexistentUser", "wrongPassword")
        );
    }

    @Test
    void logout_success() throws DataAccessException {
        UserData userData = new UserData("user", "password", "email@example.com");
        AuthData authData = userService.register(userData);
        assertDoesNotThrow(() -> userService.logout(authData.authToken()));
    }

    @Test
    void logout_invalidAuthToken() {
        assertThrows(DataAccessException.class, () ->
                userService.logout("invalidAuthToken")
        );
    }
}
