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
    void clearDatabase() {
        // Clear the database before each test
        server.clearDatabase();
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
}
