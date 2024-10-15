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
    }

    // Delegate all other methods to the appropriate DAO
    @Override
    public void insertUser(UserData user) throws DataAccessException {
        userDAO.insertUser(user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void createGame(GameData game) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {

    }

    // Implement all other methods from UserDAO, GameDAO, and AuthDAO
}

