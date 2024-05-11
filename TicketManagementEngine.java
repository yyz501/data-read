import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
// Main class for managing ticket sales for concerts

public class TicketManagementEngine {
    // DataManager instances for handling different data sets
    public static final Scanner scanner = new Scanner(System.in);
    private DataManager bookingManager;
    private DataManager concertManager;
    private DataManager venueManager;
    private DataManager customerManager;
    // File paths for various data files
    private String customerFilePath;
    private String concertFilePath;
    private String bookingFilePath;
    private String[] venueFilePaths;

//    Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Ensure valid mode is passed, else terminate
        if (args.length == 0 || (!args[0].equals("--admin") && !args[0].equals("--customer"))) {
            System.out.println("Invalid user mode. Terminating program now.");
            System.exit(1);
        }
        // Initialize and run the management engine
        TicketManagementEngine tme = new TicketManagementEngine();
        tme.initManagers();
        tme.handleArguments(args);
//        tme.saveData();

    }

    // Initialize DataManager instances for handling different types of data
    private void initManagers() {
        bookingManager = new BookingManager();
        venueManager = new VenueManager();
        concertManager = new ConcertManager((BookingManager) bookingManager, (VenueManager) venueManager);
        customerManager = new CustomerManager();
    }


    // Handle command line arguments and initiate appropriate mode
    private void handleArguments(String[] args) {
        String mode = args[0];
        if (!setFilePaths(args)) {
            System.exit(1);  // Exit if file paths are not set correctly due to missing arguments
        }
        loadInitialData(); // Presumably loads data from the file paths set in setFilePaths method

        // Declare the menu variable outside the switch to ensure it's visible for later use

        switch (mode) {
            case "--customer":
                handleCustomerMode(args);
//                MainMenu customerMenu = new CustomerMenu((VenueManager) venueManager,
//                        (BookingManager) bookingManager, (CustomerManager) customerManager);
//
//                displayMessage();
//                customerMenu.showMenu();
                break;
            case "--admin":
                if (args.length > 4) {
                } else {
                    System.exit(1);
                }

                AdminMenu adminMenu = new AdminMenu(scanner, (ConcertManager) concertManager, (BookingManager) bookingManager, (VenueManager) venueManager);
                System.out.println("Welcome to Ticket Management System Admin Mode.");
                displayMessage();
                adminMenu.showMenu();
                break;
            default:
                System.out.println("Invalid user mode. Terminating program now.");
                System.exit(1);
                return; // Terminate if the mode is neither admin nor customer
        }
    }

    // Set file paths based on arguments passed
    private boolean setFilePaths(String[] args) {
        if ("--customer".equals(args[0])) {
            if (args.length > 2 && args[1].matches("\\d+")) {
                // with ID and password
                int customerId = Integer.parseInt(args[1]);
                String customerPassword = args[2];
                customerFilePath = args[3];
                concertFilePath = args[4];
                bookingFilePath = args[5];
                venueFilePaths = Arrays.copyOfRange(args, 6, args.length);
            } else {
                // without ID and password
                customerFilePath = args[1];
                concertFilePath = args[2];
                bookingFilePath = args[3];
                venueFilePaths = Arrays.copyOfRange(args, 4, args.length);

            }
        } else if ("--admin".equals(args[0])) {
            customerFilePath = args[1];
            concertFilePath = args[2];
            bookingFilePath = args[3];
            venueFilePaths = Arrays.copyOfRange(args, 4, args.length);
        }
        return true;
    }

    // Handle operations in customer mode
    private void handleCustomerMode(String[] args) {
        if (args[1].matches("\\d+")) {
            int customerId = Integer.parseInt(args[1]);
            String customerPassword = args[2];

            if (!customerExists(customerId)) {
                System.out.println("Customer does not exist. Terminating Program");
                System.exit(1);
            } else if (!checkPassword(customerId, customerPassword)) {
                System.out.println("Incorrect Password. Terminating Program");
                System.exit(1);
            } else {
                // ID and Password all correct
                loadCustomerSession(customerId);
                CustomerMenu customerMenu = new CustomerMenu(scanner, (VenueManager) venueManager, (BookingManager) bookingManager, (CustomerManager) customerManager, (ConcertManager) concertManager);
                customerMenu.displayConcerts();
            }
        } else {
            // No id and password, need to register as new customer
            registerNewCustomer();
            CustomerMenu customerMenu = new CustomerMenu(scanner, (VenueManager) venueManager, (BookingManager) bookingManager, (CustomerManager) customerManager, (ConcertManager) concertManager);

            customerMenu.displayConcerts();
        }
    }

    // Method to load customer session by customer ID
    private void loadCustomerSession(int customerId) {
        System.out.println("Welcome " + getCustomerName(customerId) + " to Ticket Management System");
        displayMessage();
        // Additional steps to proceed with customer session
    }

    // Utility to get customer name by ID, assuming you have such a method.
    private String getCustomerName(int customerId) {
        List<Customer> customers = (List<Customer>) customerManager.getAllRecords();
        Customer customer = customers.stream()
                .filter(c -> c.getId() == customerId)
                .findFirst()
                .orElse(null);
        return (customer != null) ? customer.getName() : "Unknown";
    }

    // Load initial data from files
    private void loadInitialData() {
        try {
            customerManager.loadData(customerFilePath);
            concertManager.loadData(concertFilePath);
            bookingManager.loadData(bookingFilePath);
            for (String venueFile : venueFilePaths) {
                venueManager.loadData(venueFile);
            }
        } catch (IOException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }

    }

    // Check if customer exists in the system
    private boolean customerExists(int customerId) {
        List<Customer> customers = (List<Customer>) customerManager.getAllRecords();
        return customers.stream().anyMatch(customer -> customer.getId() == customerId);
    }

    // Check if customer password is correct
    private boolean checkPassword(int customerId, String password) {
        List<Customer> customers = (List<Customer>) customerManager.getAllRecords();
        return customers.stream().anyMatch(customer ->
                customer.getId() == customerId && customer.getPassword().equals(password));
    }

    // Register a new customer and reload customer data
    private void registerNewCustomer() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        int newCustomerId = generateNewCustomerId();
        appendNewCustomerToCsv(newCustomerId, name, password);

        // Reload customer data to include the newly registered customer
        reloadCustomerData();
        loadCustomerSession(newCustomerId); // Load session for the new customer
    }

    private int generateNewCustomerId() {
        // 强制转换为 CustomerManager 来调用 getAllRecords()
        List<Customer> customers = (List<Customer>) ((CustomerManager) customerManager).getAllRecords();
        // Return the next available ID
        return customers.size() + 1;
    }

    // Append new customer data to CSV file
    private void appendNewCustomerToCsv(int id, String name, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(customerFilePath, true))) {
            writer.newLine();
            writer.write(id + "," + name + "," + password);
        } catch (IOException e) {
            System.err.println("Failed to register new customer: " + e.getMessage());
        }
    }

    // Method to reload customer data from CSV
    private void reloadCustomerData() {
        try {
            customerManager.loadData(customerFilePath);
        } catch (IOException e) {
            System.err.println("Error reloading customer data: " + e.getMessage());
        }
    }

//     Save data back to files
//    private void saveData() {
//        try {
//            customerManager.saveData(customerFilePath);
//            concertManager.saveData(concertFilePath);
//            bookingManager.saveData(bookingFilePath);
//
//        } catch (IOException e) {
//            System.err.println("Error saving data: " + e.getMessage());
//        }
//    }

    public void displayMessage(){


        System.out.print("\n" +
                " ________  ___ _____ \n" +
                "|_   _|  \\/  |/  ___|\n" +
                "  | | | .  . |\\ `--. \n" +
                "  | | | |\\/| | `--. \\\n" +
                "  | | | |  | |/\\__/ /\n" +
                "  \\_/ \\_|  |_/\\____/ \n" +
                "                    \n" +
                "                    \n");
    }

}
