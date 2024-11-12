package client;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import java.util.List;

public class Main {
    private static final String SERVER_URL = "http://localhost:8080";
    private static final ServerFacade SERVER = new ServerFacade(SERVER_URL);
    private static final ConsoleUI UI = new ConsoleUI();
    private static AuthData authData = null;
    private static List<GameData> gameList = null;
    public static void main(String[] args) {
        System.out.println("Welcome to the Chess Game!");
        runPreloginUI();
    }
    private static void runPreloginUI() {
        while (true) {
            String command = UI.getInput("Enter command (help, quit, login, register)").toLowerCase();
            try {
                switch (command) {
                    case "help":
                        UI.displayHelp(false);
                        break;
                    case "quit":
                        System.out.println("Thanks for playing!");
                        return;
                    case "login":
                        handleLogin();
                        break;
                    case "register":
                        handleRegister();
                        break;
                    default:
                        System.out.println("Invalid command. Type 'help' for a list of commands.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    private static void runPostloginUI() {
        while (true) {
            String command = UI.getInput("Enter command (help, logout, create game, list games, join game, observe game)").toLowerCase();
            try {
                switch (command) {
                    case "help":
                        UI.displayHelp(true);
                        break;
                    case "logout":
                        handleLogout();
                        return;
                    case "create game":
                        handleCreateGame();
                        break;
                    case "list games":
                        handleListGames();
                        break;
                    case "join game":
                        handleJoinGame();
                        break;
                    case "observe game":
                        handleObserveGame();
                        break;
                    default:
                        System.out.println("Invalid command. Type 'help' for a list of commands.");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    private static void handleLogin() throws Exception {
        String[] credentials = UI.getLoginInfo();
        authData = SERVER.login(credentials[0], credentials[1]);
        System.out.println("Login successful. Welcome, " + authData.username() + "!");
        runPostloginUI();
    }
    private static void handleRegister() throws Exception {
        String[] registrationInfo = UI.getRegisterInfo();
        authData = SERVER.register(registrationInfo[0], registrationInfo[1], registrationInfo[2]);
        System.out.println("Registration successful. Welcome, " + authData.username() + "!");
        runPostloginUI();
    }
    private static void handleLogout() throws Exception {
        SERVER.logout(authData.authToken());
        authData = null;
        System.out.println("Logout successful.");
    }
    private static void handleCreateGame() throws Exception {
        String gameName = UI.getGameName();
        SERVER.createGame(gameName, authData.authToken());
        System.out.println("Game created successfully.");
    }
    private static void handleListGames() throws Exception {
        gameList = SERVER.listGames(authData.authToken());
        UI.displayGameList(gameList);
    }
    private static void handleJoinGame() throws Exception {
        if (gameList == null) {
            System.out.println("Please list games first.");
            return;
        }
        int gameNumber = UI.getGameNumber();
        if (gameNumber > 0 && gameNumber <= gameList.size()) {
            ChessGame.TeamColor color = UI.getTeamColor();
            GameData selectedGame = gameList.get(gameNumber - 1);
            SERVER.joinGame(selectedGame.gameID(), color, authData.authToken());
            System.out.println("Joined game successfully.");
            ChessBoardUI.drawBoard(selectedGame.game().getBoard(), color == ChessGame.TeamColor.WHITE);
        } else {
            System.out.println("Invalid game number.");
        }
    }
    private static void handleObserveGame() throws Exception {
        if (gameList == null) {
            System.out.println("Please list games first.");
            return;
        }
        int gameNumber = UI.getGameNumber();
        if (gameNumber > 0 && gameNumber <= gameList.size()) {
            GameData selectedGame = gameList.get(gameNumber - 1);
//            SERVER.joinGame(selectedGame.gameID(), null, authData.authToken());
            System.out.println("Observing game.");
            ChessBoardUI.drawBoard(selectedGame.game().getBoard(), true);
            ChessBoardUI.drawBoard(selectedGame.game().getBoard(), false);
        } else {
            System.out.println("Invalid game number.");
        }
    }
}