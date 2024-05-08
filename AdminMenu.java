public class AdminMenu extends MainMenu {

    @Override
    public void showMenu() {
        System.out.println("Admin Menu:");
        System.out.println("1. View all data");
        System.out.println("2. Modify data");
        System.out.println("3. Exit");

        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                System.out.println("Viewing all data...");
                break;
            case 2:
                System.out.println("Modifying data...");
                break;
            case 3:
                System.out.println("Exiting...");
                close();
                break;
            default:
                System.out.println("Invalid choice.");
                showMenu();
                break;
        }
    }
}
