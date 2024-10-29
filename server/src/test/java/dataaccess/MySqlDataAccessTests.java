package dataaccess;

import model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class MySqlDataAccessTests {

    private dataaccess.MySqlDataAccess dataaccess;

    @BeforeEach
    public void setUp() throws DataAccessException {
        dataaccess = new dataaccess.MySqlDataAccess();
        dataaccess.clear(); // Start with a clean database
    }

    @Test
    public void testClear_Success() throws DataAccessException {
        // Insert some test data
        // ... (implement this once we have create methods)

        // Clear the database
        dataaccess.clear();

        // Verify that the database is empty
        // ... (implement this once we have read methods)
    }
    @Test
    public void testCreateUser_Success() throws DataAccessException {
        UserData user = new UserData("testuser", "password123", "test@example.com");
        assertDoesNotThrow(() -> dataaccess.insertUser(user));

        // Verify user was created (implement this once we have a getUser method)
    }

    @Test
    public void testCreateUser_DuplicateUsername() throws DataAccessException {
        UserData user1 = new UserData("testuser", "password123", "test1@example.com");
        UserData user2 = new UserData("testuser", "password456", "test2@example.com");

        assertDoesNotThrow(() -> dataaccess.insertUser(user1));
        assertThrows(DataAccessException.class, () -> dataaccess.insertUser(user2));
    }
    @Test
    public void testGetUser_Success() throws DataAccessException {
        UserData user = new UserData("testuser", "password123", "test@example.com");
        dataaccess.insertUser(user);

        UserData retrievedUser = dataaccess.getUser("testuser");
        assertNotNull(retrievedUser);
        assertEquals(user.username(), retrievedUser.username());
        assertEquals(user.email(), retrievedUser.email());
        assertTrue(BCrypt.checkpw(user.password(), retrievedUser.password()));
    }

    @Test
    public void testGetUser_NonexistentUser() throws DataAccessException {
        UserData retrievedUser = dataaccess.getUser("nonexistentuser");
        assertNull(retrievedUser);
    }


}
