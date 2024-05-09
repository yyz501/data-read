import java.util.Scanner;

public abstract class MainMenu {
    protected Scanner scanner;

    public MainMenu() {
        this.scanner = new Scanner(System.in);
    }
    public void close() {
        scanner.close();
    }

    public abstract void showMenu();

    public abstract void showMenu(int parameter);
}

