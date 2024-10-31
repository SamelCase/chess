package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws DataAccessException {
        // Check if user already exists
        try {
            dataAccess.getUser(user.username());
            throw new DataAccessException("Error: already taken");
        } catch (DataAccessException e) {
            // User doesn't exist, so we can proceed with registration

            dataAccess.insertUser(new UserData (user.username(), BCrypt.hashpw(user.password(), BCrypt.gensalt()), user.email()));
            String authToken = dataAccess.generateAuthToken();
            AuthData authData = new AuthData(authToken, user.username());
            dataAccess.createAuth(authData);
            return authData;
        }
    }

    public AuthData login(String username, String password) throws DataAccessException {
        UserData user = dataAccess.getUser(username);
        if (user == null || !BCrypt.checkpw(password, user.password())) {
            throw new DataAccessException("Error: unauthorized");
        }
        String authToken = dataAccess.generateAuthToken();
        AuthData authData = new AuthData(authToken, username);
        dataAccess.createAuth(authData);
        return authData;
    }

    public void logout(String authToken) throws DataAccessException {
        dataAccess.deleteAuth(authToken);
    }
}
