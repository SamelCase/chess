package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

import java.util.List;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public List<GameData> listGames(String authToken) throws DataAccessException {
        // Verify the auth token
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        return dataAccess.listGames();
    }

    public int createGame(String authToken, String gameName) throws DataAccessException {
        // Verify the auth token
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        ChessGame newGame = new ChessGame(); // Assuming you have a ChessGame class
        GameData gameData = new GameData(0, null, null, gameName, newGame);
        return dataAccess.createGame(gameData);
    }

    public void joinGame(String authToken, int gameID, ChessGame.TeamColor playerColor) throws DataAccessException {
        // Verify the auth token
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        GameData game = dataAccess.getGame(gameID);
        if (game == null) {
            throw new DataAccessException("Error: bad request");
        }

        String username = authData.username();
        if (playerColor == ChessGame.TeamColor.WHITE) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            game = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        } else if (playerColor == ChessGame.TeamColor.BLACK) {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            game = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        }

        dataAccess.updateGame(game);
    }
}

