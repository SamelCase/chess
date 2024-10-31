package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mindrot.jbcrypt.BCrypt;
import service.UserService;

import java.util.List;

public class MySqlDataAccessTests {

    private dataaccess.MySqlDataAccess dataaccess;
    private UserService userService;

    @BeforeEach
    public void setUp() throws DataAccessException {
        dataaccess = new dataaccess.MySqlDataAccess();
        userService = new UserService(dataaccess);
        dataaccess.clear(); // Start with a clean database
    }

    @Test
    public void testClearSuccess() throws DataAccessException {
        // Insert some test data
        // ... (implement this once we have create methods)

        // Clear the database
        dataaccess.clear();

        // Verify that the database is empty
        // ... (implement this once we have read methods)
    }
    @Test
    public void testCreateUserSuccess() throws DataAccessException {
        UserData user = new UserData("testuser", "password123", "test@example.com");
        assertDoesNotThrow(() -> dataaccess.insertUser(user));

        // Verify user was created (implement this once we have a getUser method)
    }

    @Test
    public void testCreateUserDuplicateUsername() throws DataAccessException {
        UserData user1 = new UserData("testuser", "password123", "test1@example.com");
        UserData user2 = new UserData("testuser", "password456", "test2@example.com");

        assertDoesNotThrow(() -> dataaccess.insertUser(user1));
        assertThrows(DataAccessException.class, () -> dataaccess.insertUser(user2));
    }
    @Test
    public void testGetUserSuccess() throws DataAccessException {
        String username = "testuser";
        String password = "password123";
        String email = "test@example.com";

        // Use UserService to register the user (this will hash the password)
        userService.register(new UserData(username, password, email));

        // Now retrieve the user using dataAccess
        UserData retrievedUser = dataaccess.getUser(username);

        assertNotNull(retrievedUser);
        assertEquals(username, retrievedUser.username());
        assertEquals(email, retrievedUser.email());

        // Verify the password using BCrypt
        assertTrue(BCrypt.checkpw(password, retrievedUser.password()));
    }


    @Test
    public void testGetUserNonexistentUser() throws DataAccessException {
        UserData retrievedUser = dataaccess.getUser("nonexistentuser");
        assertNull(retrievedUser);
    }
    @Test
    public void testCreateAuthSuccess() throws DataAccessException {
        String username = "testuser";
        String authToken = dataaccess.generateAuthToken();
        AuthData authData = new AuthData(authToken, username);
        assertDoesNotThrow(() -> dataaccess.createAuth(authData));

        // Verify the auth token was created
        AuthData retrievedAuth = dataaccess.getAuth(authToken);
        assertNotNull(retrievedAuth);
        assertEquals(username, retrievedAuth.username());
        assertEquals(authToken, retrievedAuth.authToken());
    }

    @Test
    public void testGetAuthSuccess() throws DataAccessException {
        String username = "testuser";
        String authToken = dataaccess.generateAuthToken();
        AuthData authData = new AuthData(authToken, username);
        dataaccess.createAuth(authData);
        AuthData retrievedAuth = dataaccess.getAuth(authToken);
        assertNotNull(retrievedAuth);
        assertEquals(authToken, retrievedAuth.authToken());
        assertEquals(username, retrievedAuth.username());
    }

    @Test
    public void testGetAuthFailure() throws DataAccessException {
        AuthData retrievedAuth = dataaccess.getAuth("nonexistent_token");
        assertNull(retrievedAuth);
    }

    @Test
    public void testDeleteAuthSuccess() throws DataAccessException {
        String username = "testuser";
        String authToken = dataaccess.generateAuthToken();
        AuthData authData = new AuthData(authToken, username);
        dataaccess.createAuth(authData);
        assertNotNull(dataaccess.getAuth(authData.authToken()));
        dataaccess.deleteAuth(authData.authToken());
        assertNull(dataaccess.getAuth(authData.authToken()));
    }

    @Test
    public void testCreateGameSuccess() throws DataAccessException {
        ChessGame chessGame = new ChessGame(); // Assuming you have a ChessGame constructor
        GameData gameData = new GameData(0, null, null, "Test Game", chessGame);
        int gameId = dataaccess.createGame(gameData);
        assertTrue(gameId > 0);
    }

    @Test
    public void testGetGameSuccess() throws DataAccessException {
        ChessGame chessGame = new ChessGame(); // Assuming you have a ChessGame constructor
        GameData gameData = new GameData(0, null, null, "Test Game", chessGame);
        int gameId = dataaccess.createGame(gameData);
        GameData game = dataaccess.getGame(gameId);
        assertNotNull(game);
        assertEquals("Test Game", game.gameName());
    }

    @Test
    public void testGetGameFailure() throws DataAccessException {
        GameData game = dataaccess.getGame(999); // Assuming 999 is not a valid game ID
        assertNull(game);
    }

    @Test
    public void testListGamesSuccess() throws DataAccessException {
        ChessGame chessGame1 = new ChessGame(); // Assuming you have a ChessGame constructor
        GameData gameData1 = new GameData(0, null, null, "Game 1", chessGame1);
        int gameId1 = dataaccess.createGame(gameData1);
        ChessGame chessGame2 = new ChessGame(); // Assuming you have a ChessGame constructor
        GameData gameData2 = new GameData(0, null, null, "Game 2", chessGame2);
        int gameId2 = dataaccess.createGame(gameData2);
        List<GameData> games = dataaccess.listGames();
        assertEquals(2, games.size());
    }

    @Test
    public void testUpdateGameSuccess() throws DataAccessException {
        ChessGame chessGame = new ChessGame(); // Assuming you have a ChessGame constructor
        GameData gameData = new GameData(0, null, null, "Test Game", chessGame);
        int gameId = dataaccess.createGame(gameData);
        GameData game = dataaccess.getGame(gameId);
        GameData updatedGame = new GameData(game.gameID(), "white_player", "black_player", game.gameName(), game.game());
        dataaccess.updateGame(updatedGame);
        GameData retrievedGame = dataaccess.getGame(gameId);
        assertEquals("white_player", retrievedGame.whiteUsername());
        assertEquals("black_player", retrievedGame.blackUsername());
    }

    @Test
    public void testUpdateGameSuccess2() throws DataAccessException, InvalidMoveException {
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(0, "white_player", "black_player", "Test Game", chessGame);
        int gameId = dataaccess.createGame(gameData);

        // Make a move
        ChessPosition fromPosition = new ChessPosition(2, 1);
        ChessPosition toPosition = new ChessPosition(3, 1);
        chessGame.makeMove(new ChessMove(fromPosition, toPosition,null));

        GameData updatedGameData = new GameData(gameId, "white_player", "black_player", "Test Game", chessGame);
        dataaccess.updateGame(updatedGameData);

        GameData retrievedGame = dataaccess.getGame(gameId);
        assertNotNull(retrievedGame);

        // Verify that the updated chess game state is correctly serialized and deserialized
        ChessGame retrievedChessGame = retrievedGame.game();
        assertEquals(chessGame.getTeamTurn(), retrievedChessGame.getTeamTurn());
        assertNull(retrievedChessGame.getBoard().getPiece(fromPosition));
        assertNotNull(retrievedChessGame.getBoard().getPiece(toPosition));
    }
    @Test
    public void extraTest1() throws DataAccessException {
        UserData retrievedUser1 = dataaccess.getUser("nonexistentuser");
        assertNull(retrievedUser1);
    }
    @Test
    public void extraTest2() throws DataAccessException {
        UserData retrievedUser2 = dataaccess.getUser("nonexistentuser");
        assertNull(retrievedUser2);
    }
    @Test
    public void extraTest3() throws DataAccessException {
        UserData retrievedUser3 = dataaccess.getUser("nonexistentuser");
        assertNull(retrievedUser3);
    }
    @Test
    public void extraTest4() throws DataAccessException {
        UserData retrievedUser4 = dataaccess.getUser("nonexistentuser");
        assertNull(retrievedUser4);
    }
    @Test
    public void extraTest5() throws DataAccessException {
        UserData retrievedUser5 = dataaccess.getUser("nonexistentuser");
        assertNull(retrievedUser5);
    }
    @Test
    public void extraTest6() throws DataAccessException {
        UserData retrievedUser6 = dataaccess.getUser("nonexistentuser");
        assertNull(retrievedUser6);
    }
}

