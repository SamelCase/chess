package dataaccess;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemGameDAO implements GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextGameId = 1;

    @Override
    public int createGame(GameData game) throws DataAccessException {
        int gameId = nextGameId++;
        GameData newGame = new GameData(gameId, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        games.put(gameId, newGame);
        return gameId;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = games.get(gameID);
        if (game == null) {
            throw new DataAccessException("Game not found");
        }
        return game;
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (!games.containsKey(game.gameID())) {
            throw new DataAccessException("Game not found");
        }
        games.put(game.gameID(), game);
    }
    @Override
    public void clear() throws DataAccessException {
        games.clear();
        nextGameId = 1;
    }

    // You might want to add additional methods like deleteGame if needed
}
