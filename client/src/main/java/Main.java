public class Main {
    private static ServerFacade server;
    private static ConsoleUI ui;
    private static String authToken;

    public static void main(String[] args) {
        server = new ServerFacade(8080); // Use the actual server port
        ui = new ConsoleUI();

        boolean running = true;
        while (running) {
            String command = ui.getInput("Enter command");
            switch (command.toLowerCase()) {
                case "help":
                    ui.displayHelp(authToken != null);
                    break;
                case "quit":
                    running = false;
                    break;
                case "login":
                    handleLogin();
                    break;
                case "register":
                    handleRegister();
                    break;
                case "logout":
                    handleLogout();
                    break;
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
                    ui.displayMessage("Invalid command. Type 'help' for a list of commands.");
            }
        }
    }

    private static void handleLogin() {
        // Implement login logic
    }

    private static void handleRegister() {
        // Implement register logic
    }

    private static void handleLogout() {
        // Implement logout logic
    }

    private static void handleCreateGame() {
        // Implement create game logic
    }

    private static void handleListGames() {
        // Implement list games logic
    }

    private static void handleJoinGame() {
        // Implement join game logic
    }

    private static void handleObserveGame() {
        // Implement observe game logic (just draw the board for now)
    }
}
