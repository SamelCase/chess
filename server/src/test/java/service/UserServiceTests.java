package service;
import dataaccess.DataAccess;
import dataaccess.MemDataAccess;
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

    // Test methods will go here
}