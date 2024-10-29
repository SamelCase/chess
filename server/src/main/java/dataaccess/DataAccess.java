package dataaccess;

import chess.ChessGame;
import model.AuthData;

public interface DataAccess extends UserDAO, GameDAO, AuthDAO {
    void clear() throws DataAccessException;

    void insertUser(User user) throws DataAccessException;

    AuthData createAuth(String username) throws DataAccessException;

    int createGame(String gameName) throws DataAccessException;

    void joinGame(String username, int gameID, ChessGame.TeamColor playerColor) throws DataAccessException;
}

