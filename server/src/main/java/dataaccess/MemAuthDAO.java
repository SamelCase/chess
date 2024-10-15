package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemAuthDAO implements AuthDAO {
    private final Map<String, AuthData> auths = new HashMap<>();

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        auths.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        AuthData auth = auths.get(authToken);
        if (auth == null) {
            throw new DataAccessException("Auth token not found");
        }
        return auth;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (!auths.containsKey(authToken)) {
            throw new DataAccessException("Auth token not found");
        }
        auths.remove(authToken);
    }

    // You might want to add a method to generate a new auth token
    public String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
