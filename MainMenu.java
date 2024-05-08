import java.util.Scanner;

public abstract class MainMenu {
    protected Scanner scanner;

    public MainMenu() {
        this.scanner = new Scanner(System.in);
    }

    public abstract void showMenu();

    public void close() {
        scanner.close();
    }
}
