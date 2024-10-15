package dataaccess;

public interface DataAccess extends UserDAO, GameDAO, AuthDAO {
    void clear() throws DataAccessException;
}

