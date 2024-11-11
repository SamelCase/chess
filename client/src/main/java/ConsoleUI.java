import model.*;
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
        // Display numbered list of games
    }

    public void displayCreateGamePrompt() {
        // Prompt for new game name
    }

    public void displayJoinGamePrompt() {
        // Prompt for game number and team color
    }

    public void displayMessage(String message) {
        // Display a message to the user
    }

    public String getInput(String prompt) {
        // Get user input with a given prompt
    }
}
