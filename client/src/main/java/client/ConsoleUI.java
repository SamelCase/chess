package client;

import chess.ChessGame;
import model.GameData;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    public void displayHelp(boolean isLoggedIn) {
        System.out.println("Available commands:");
        if (!isLoggedIn) {
            System.out.println("  help - Display available commands");
            System.out.println("  quit - Exit the program");
            System.out.println("  login - Log in to an existing account");
            System.out.println("  register - Create a new account");
        } else {
            System.out.println("  help - Display available commands");
            System.out.println("  logout - Log out of the current account");
            System.out.println("  create game - Create a new chess game");
            System.out.println("  list games - List all available games");
            System.out.println("  join game - Join an existing game");
            System.out.println("  observe game - Observe an existing game");
        }
    }
    public void displayGameplayHelp() {
        System.out.println("Available commands:");
        System.out.println("  help - Display this help message");
        System.out.println("  redraw - Redraw the chess board");
        System.out.println("  leave - Leave the current game");
        System.out.println("  move - Make a move (e.g., 'e2 e4')");
        System.out.println("  resign - Resign from the game");
        System.out.println("  highlight - Highlight legal moves for a piece");
    }
    public String[] getLoginInfo() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        return new String[]{username, password};
    }
    public String[] getRegisterInfo() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        return new String[]{username, password, email};
    }
    public void displayGameList(List<GameData> games) {
        if (games.isEmpty()) {
            System.out.println("No games available.");
            return;
        }
        System.out.println("Available games:");
        for (int i = 0; i < games.size(); i++) {
            GameData game = games.get(i);
            System.out.printf("%d. %s (White: %s, Black: %s)%n",
                    i + 1, game.gameName(), game.whiteUsername(), game.blackUsername());
        }
    }
    public String getGameName() {
        System.out.print("Enter game name: ");
        return scanner.nextLine().trim();
    }
    public int getGameNumber() {
        System.out.print("Enter game number: ");
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Consume the invalid input
        }
        return scanner.nextInt();
    }
    public ChessGame.TeamColor getTeamColor() {
        while (true) {
            System.out.print("Enter team color (WHITE/BLACK): ");
            String input = scanner.nextLine().toUpperCase().trim();
            if (input.equals("WHITE")) {
                return ChessGame.TeamColor.WHITE;
            } else if (input.equals("BLACK")) {
                return ChessGame.TeamColor.BLACK;
            } else {
                System.out.println("Invalid color. Please enter WHITE or BLACK.");
            }
        }
    }
    public String getInput(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }
}
