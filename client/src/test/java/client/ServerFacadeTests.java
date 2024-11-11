package client;

import org.junit.jupiter.api.*;
import server.Server;
import model.*;
import chess.ChessGame;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }
    @BeforeEach
    public void clearFirst() {
        assertDoesNotThrow(() -> facade.clearDB());

    }
    @Test
    void registerPositive() throws Exception {
        AuthData authData = facade.register("testUser", "password", "test@email.com");
        assertNotNull(authData);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerNegative() {
        assertThrows(Exception.class, () -> {
            facade.register("testUser", "password", "test@email.com");
            facade.register("testUser", "password", "test@email.com"); // Duplicate username
        });
    }
    @Test
    void loginPositive() throws Exception {
        facade.register("testUser", "password", "test@email.com");
        AuthData authData = facade.login("testUser", "password");
        assertNotNull(authData);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void loginNegative() {
        assertThrows(Exception.class, () -> {
            facade.login("nonexistentUser", "wrongPassword");
        });
    }
    @Test
    void logoutPositive() throws Exception {
        AuthData authData = facade.register("testUser", "password", "test@email.com");
        assertDoesNotThrow(() -> facade.logout(authData.authToken()));
    }

    @Test
    void logoutNegative() {
        assertThrows(Exception.class, () -> {
            facade.logout("invalidAuthToken");
        });
    }
    @Test
    void createGamePositive() throws Exception {
        AuthData authData = facade.register("testUser", "password", "test@email.com");
        assertDoesNotThrow(() -> facade.createGame("TestGame", authData.authToken()));
    }

    @Test
    void createGameNegative() {
        assertThrows(Exception.class, () -> {
            facade.createGame("TestGame", "invalidAuthToken");
        });
    }
    @Test
    void listGamesPositive() throws Exception {
        AuthData authData = facade.register("testUser", "password", "test@email.com");
        facade.createGame("TestGame1", authData.authToken());
        facade.createGame("TestGame2", authData.authToken());
        List<GameData> games = facade.listGames(authData.authToken());
        assertEquals(2, games.size());
    }

    @Test
    void listGamesNegative() {
        assertThrows(Exception.class, () -> {
            facade.listGames("invalidAuthToken");
        });
    }
    @Test
    void joinGamePositive() throws Exception {
        AuthData authData = facade.register("testUser", "password", "test@email.com");
        facade.createGame("TestGame", authData.authToken());
        List<GameData> games = facade.listGames(authData.authToken());
        assertDoesNotThrow(() -> facade.joinGame(games.get(0).gameID(), ChessGame.TeamColor.WHITE, authData.authToken()));
    }

    @Test
    void joinGameNegative() {
        assertThrows(Exception.class, () -> {
            facade.joinGame(999, ChessGame.TeamColor.WHITE, "invalidAuthToken");
        });
    }
}
