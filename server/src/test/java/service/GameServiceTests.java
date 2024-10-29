package service;
import dataaccess.*;
import model.*;
import service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    private GameService gameService;
    private DataAccess dataAccess;
    private UserService userService;

    @BeforeEach
    void setUp() {
        dataAccess = new MemDataAccess();
        gameService = new GameService(dataAccess);
        userService = new UserService(dataAccess);
    }
    @Test
    void listGamesSuccess() throws DataAccessException {
        // First, create a game
        AuthData authData = userService.register(new UserData("user", "password", "email@example.com"));
        gameService.createGame(authData.authToken(), "TestGame");

        var games = gameService.listGames(authData.authToken());
        assertFalse(games.isEmpty());
        assertEquals(1, games.size());
    }

    @Test
    void listGamesInvalidAuthToken() {
        assertThrows(DataAccessException.class, () ->
                gameService.listGames("invalidAuthToken")
        );
    }

    @Test
    void createGameSuccess() throws DataAccessException {
        // Register a user and get an auth token
        UserData userData = new UserData("user", "password", "email@example.com");
        AuthData authData = userService.register(userData);

        // Create a game
        int gameID = gameService.createGame(authData.authToken(), "NewGame");

        // Assert that the game ID is positive (valid)
        assertTrue(gameID > 0, "Game ID should be positive");

        // Optionally, verify that the game exists in the database
        GameData retrievedGame = dataAccess.getGame(gameID);
        assertNotNull(retrievedGame, "Game should exist in the database");
        assertEquals("NewGame", retrievedGame.gameName(), "Game name should match");
    }

    @Test
    void createGameInvalidAuthToken() {
        assertThrows(DataAccessException.class, () ->
                        gameService.createGame("invalidAuthToken", "NewGame"),
                "Should throw DataAccessException for invalid auth token"
        );
    }
}

