package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.List;

public class MemDataAccess implements DataAccess {
    private final UserDAO userDAO = new MemUserDAO();
    private final GameDAO gameDAO = new MemGameDAO();
    private final AuthDAO authDAO = new MemAuthDAO();

    @Override
    public void clear() throws DataAccessException {
        // Clear all data from the DAOs
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
    }

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        userDAO.insertUser(user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return userDAO.getUser(username);
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        authDAO.createAuth(auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authDAO.getAuth(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        authDAO.deleteAuth(authToken);
    }

    @Override
    public String generateAuthToken() throws DataAccessException {
        return authDAO.generateAuthToken();
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        gameDAO.createGame(game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return gameDAO.getGame(gameID);
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        return gameDAO.listGames();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        gameDAO.updateGame(game);
    }
}
