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
    void registerSuccess() throws DataAccessException {
        UserData userData = new UserData("newUser", "password", "email@example.com");
        AuthData result = userService.register(userData);
        assertNotNull(result);
        assertEquals("newUser", result.username());
    }

    @Test
    void registerUserAlreadyExists() {
        UserData userData = new UserData("existingUser", "password", "email@example.com");
        assertThrows(DataAccessException.class, () -> {
            userService.register(userData);
            userService.register(userData); // Trying to register the same user again
        });
    }

    @Test
    void loginSuccess() throws DataAccessException {
        UserData userData = new UserData("user", "password", "email@example.com");
        userService.register(userData);
        AuthData result = userService.login("user", "password");
        assertNotNull(result);
        assertEquals("user", result.username());
    }

    @Test
    void loginInvalidCredentials() {
        assertThrows(DataAccessException.class, () ->
                userService.login("nonexistentUser", "wrongPassword")
        );
    }

    @Test
    void logoutSuccess() throws DataAccessException {
        UserData userData = new UserData("user", "password", "email@example.com");
        AuthData authData = userService.register(userData);
        assertDoesNotThrow(() -> userService.logout(authData.authToken()));
    }

    @Test
    void logoutInvalidAuthToken() {
        assertThrows(DataAccessException.class, () ->
                userService.logout("invalidAuthToken")
        );
    }
}
