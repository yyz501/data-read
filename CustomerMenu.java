public class CustomerMenu extends MainMenu {

    @Override
    public void showMenu() {
        System.out.println("Customer Menu:");
        System.out.println("1. View concerts");
        System.out.println("2. Book tickets");
        System.out.println("3. Exit");

        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                System.out.println("Viewing concerts...");
                break;
            case 2:
                System.out.println("Booking tickets...");
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
