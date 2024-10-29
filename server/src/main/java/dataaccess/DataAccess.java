package dataaccess;

import chess.ChessGame;
import model.UserData;

public interface DataAccess extends UserDAO, GameDAO, AuthDAO {
    void clear() throws DataAccessException;
}

