package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {
    private ClearService clearService;
    private DataAccess dataAccess;

    @BeforeEach
    void setUp() {
        dataAccess = new MemDataAccess();
        clearService = new ClearService(dataAccess);
    }

    @Test
    void clear_success() throws DataAccessException {
        // First, add some data
        UserService userService = new UserService(dataAccess);
        GameService gameService = new GameService(dataAccess);

        userService.register(new UserData("user", "password", "email@example.com"));
        AuthData authData = userService.login("user", "password");
        gameService.createGame(authData.authToken(), "TestGame");

        // Now clear the data
        assertDoesNotThrow(() -> clearService.clear());

        // Verify that data is cleared
        assertThrows(DataAccessException.class, () -> userService.login("user", "password"));
        assertTrue(gameService.listGames(authData.authToken()).isEmpty());
    }
}
