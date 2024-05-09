public class AdminMenu extends MainMenu {
    @Override
    public void showMenu(int parameter) {
        // This method can remain for general admin menu access without parameters
        showMenu(0); // Default behavior without specific parameters
    }

    @Override
    public void showMenu() {
        int choice;
        do {
            System.out.println("Select an option to get started!");
            System.out.println("Press 1 to view all the concert details");
            System.out.println("Press 2 to update the ticket costs");
            System.out.println("Press 3 to view booking details");
            System.out.println("Press 4 to view total payment received for a concert");
            System.out.println("Press 5 to exit");
            System.out.print("> ");

            choice = scanner.nextInt();  // Get user choice
            switch (choice) {
                case 1:
                    // Implement showTicketsDetails();
                    break;
                case 2:
                    // Implement updatedTicketsCost();
                    break;
                case 3:
                    // Implement viewBookingDetails();
                    break;
                case 4:
                    // Implement viewTotalPaymentReceived();
                    break;
                case 5:
                    System.out.println("Exiting Admin mode");
                    close();  // Close the scanner before exiting
                    return;
                default:
                    System.out.println("Invalid Input");
                    break;
            }
        } while (true);
    }
}
