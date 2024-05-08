import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
// Main class for managing ticket sales for concerts

public class TicketManagementEngine {
    // DataManager instances for handling different data sets
    private DataManager bookingManager;
    private DataManager concertManager;
    private DataManager venueManager;
    private DataManager customerManager;
    // File paths for various data files
    private String customerFilePath;
    private String concertFilePath;
    private String bookingFilePath;
    private String[] venueFilePaths;

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
//        tme.runMenu();

    }
    // Initialize DataManager instances for handling different types of data
    private void initManagers() {
        bookingManager =  new BookingManager();
        concertManager = new ConcertManager();
        customerManager = new CustomerManager();
        venueManager =new VenueManager();
    }

    // Handle command line arguments and initiate appropriate mode
    private void handleArguments(String[] args) {
        String mode = args[0];
        setFilePaths(args);
        loadInitialData();
        switch (mode) {
            case "--customer":
                handleCustomerMode(args);
                break;
            case "--admin":
                handleAdminMode(args);
                break;
            default:
                System.out.println("Invalid user mode. Terminating program now.");
                System.exit(1);
                break;
        }
//        displayWelcomeMessage();
//        saveData();
    }

    // Set file paths based on arguments passed
    private void setFilePaths(String[] args) {
        if (args.length == 0 || (!"--customer".equals(args[0]) && !"--admin".equals(args[0]))) {
            System.out.println("Invalid user mode. Terminating program now.");
            //System.exit(1);
        }

        if ("--customer".equals(args[0])) {
            handleCustomerMode(args);
        } else if ("--admin".equals(args[0])) {
            handleAdminMode(args);
        }
    }

    // Handle operations in customer mode
    private void handleCustomerMode(String[] args) {
        if (args.length > 5) {
            int customerId = Integer.parseInt(args[1]);
            String customerPassword = args[2];
            customerFilePath = args[3];
            concertFilePath = args[4];
            bookingFilePath = args[5];
            venueFilePaths = Arrays.copyOfRange(args, 6, args.length);

            if (!customerExists(customerId)) {
                System.out.println("Customer does not exist. Terminating Program.");
                //System.exit(1);
            } else if (!checkPassword(customerId, customerPassword)) {
                System.out.println("Incorrect Password. Terminating Program.");
                //System.exit(1);
            }
        } else {
            registerNewCustomer();
        }
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
        List<Customer> customers = customerManager.getAllRecords();
        return customers.stream().anyMatch(customer -> customer.getId() == customerId);
    }

    // Check if customer password is correct
    private boolean checkPassword(int customerId, String password) {
        List<Customer> customers = customerManager.getAllRecords();
        return customers.stream().anyMatch(customer ->
                customer.getId() == customerId && customer.getPassword().equals(password));
    }

    // Handle operations in admin mode
    private void handleAdminMode(String[] args) {
        if (args.length > 3) {
            customerFilePath = args[1];
            concertFilePath = args[2];
            bookingFilePath = args[3];
            venueFilePaths = Arrays.copyOfRange(args, 4, args.length);
        } else {
            System.exit(1);
        }
    }
    // Register a new customer
    private void registerNewCustomer() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        int newCustomerId = generateNewCustomerId();
        appendNewCustomerToCsv(newCustomerId, name, password);
        System.out.println("Welcome " + name + " to the Ticket Management System");
        scanner.close();
    }
    // Generate a new customer ID
    private int generateNewCustomerId() {
        // 强制转换为 CustomerManager 来调用 getAllRecords()
        List<Customer> customers = ((CustomerManager) customerManager).getAllRecords();
        return customers.size() + 1; // 返回下一个可用的客户 ID
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
    // Display welcome message
    private void displayWelcomeMessage() {
        System.out.println("\nWelcome to the Ticket Management System");
        displayMessage();
        displayConcert();
    }

    private void displayConcert() {

    }

    // Display the menu based on user mode and handle interactions
//    private void runMenu() {
//        MainMenu menu;
//        if ("--admin".equals(mode)){
//            menu = new AdminMenu();
//        } else {
//            menu = new CustomerMenu();
//        }
//        menu.showMenu();
//    }



    // Save data back to files
    private void saveData() {
        try {
            customerManager.saveData(customerFilePath);
            concertManager.saveData(concertFilePath);
            bookingManager.saveData(bookingFilePath);

        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

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
