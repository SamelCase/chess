package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemAuthDAO;
import model.*;

import java.util.List;

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
            dataAccess.insertUser(user);
            String authToken = dataAccess.generateAuthToken();
            AuthData authData = new AuthData(authToken, user.username());
            dataAccess.createAuth(authData);
            return authData;
        }
    }

    public AuthData login(String username, String password) throws DataAccessException {
        UserData user = dataAccess.getUser(username);
        if (user == null || !user.password().equals(password)) {
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
