import java.io.*;
import java.util.Arrays;
import java.util.InputMismatchException;
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

    }

    // Initialize DataManager instances for handling different types of data
    private void initManagers() {
        bookingManager = new BookingManager();
        concertManager = new ConcertManager();
        customerManager = new CustomerManager();
        venueManager = new VenueManager();
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
                break;
            case "--admin":
                if (args.length > 4) {
                } else {
                    System.exit(1);
                }
                MainMenu adminMenu = new AdminMenu();
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
        try {
            if ("--customer".equals(args[0])) {
                int customerId = Integer.parseInt(args[1]);
                String customerPassword = args[2];
                customerFilePath = args[3];
                concertFilePath = args[4];
                bookingFilePath = args[5];
                venueFilePaths = Arrays.copyOfRange(args, 6, args.length);
            } else if ("--admin".equals(args[0])) {
                customerFilePath = args[1];
                concertFilePath = args[2];
                bookingFilePath = args[3];
                venueFilePaths = Arrays.copyOfRange(args, 4, args.length);
            }
        } catch (NumberFormatException e) {
            System.out.println("Customer Id is in incorrect format. Skipping this line.");
            return false;
        }
        return true;
    }

    // Handle operations in customer mode
    private void handleCustomerMode(String[] args) {
        if (args.length < 5 ) {
            // If ID and password are not provided, proceed to register a new customer.
            registerNewCustomer();
        } else {
            // Parse the customer ID and handle potential NumberFormatException.
            int customerId = Integer.parseInt(args[1]);
            String customerPassword = args[2];

            if (!customerExists(customerId)) {
                System.out.println("Customer does not exist. Terminating Program.");
                System.exit(1);
            } else if (!checkPassword(customerId, customerPassword)) {
                System.out.println("Incorrect Password. Terminating Program.");
                System.exit(1);
            } else {
                // Correct scenario: The ID and password are correct.
                loadCustomerSession(customerId);
                MainMenu menu = new CustomerMenu((VenueManager) venueManager, (BookingManager) bookingManager);
                displayConcerts(); // Assuming you want to display concerts after showing the menu


            }
        }
    }
    // Load or perform operations intended for an authenticated customer
    private void loadCustomerSession(int customerId) {
        System.out.println("Welcome " + getCustomerName(customerId)+" to Ticket Management System");
        displayMessage();

        // Proceed with further customer operations such as displaying menu, etc.
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
        loadCustomerSession(newCustomerId); // Load session for the new customer
    }

    private int generateNewCustomerId() {
        // 强制转换为 CustomerManager 来调用 getAllRecords()
        List<Customer> customers = (List<Customer>) ((CustomerManager) customerManager).getAllRecords();
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

    public void displayConcerts() {
        List<Concert> concerts = (List<Concert>) (List<Concert>) concertManager.getAllRecords();

        System.out.println("Select a concert or 0 to exit");
        System.out.println("----------------------------------------" +
                "-----------------------------------------------------------------------------------");
        System.out.printf("%-5s%-15s%-15s%-15s%-30s%-15s%-15s%-15s%n", "#", "Date",
                "Artist Name", "Timing", "Venue Name", "Total Seats", "Seats Booked", "Seats Left");
        System.out.println("------------------------------------------" +
                "---------------------------------------------------------------------------------");

        for (Concert concert : concerts) {
            int totalSeats = concert.getTotalSeats();
            int bookedSeats = concert.getBookedSeats();
            int seatsLeft = totalSeats - bookedSeats;

            System.out.printf("%-5d%-15s%-15s%-15s%-30s%-15d%-15d%-15d%n",
                    concert.getId(), concert.getDate(), concert.getTiming(), concert.getArtistName(),
                    concert.getVenueName(), totalSeats, bookedSeats, seatsLeft);
        }
        System.out.println("----------------------------------------------------------" +
                "-----------------------------------------------------------------");
        System.out.print("> ");
        handleConcertSelection();
    }
    private void handleConcertSelection() {
        Scanner scanner = new Scanner(System.in);
        List<Concert> allConcerts = (List<Concert>) concertManager.getAllRecords();
        int selection;

//        System.out.println("Select a concert (number) or 0 to exit:");
        while (true) {
            try {
                selection = scanner.nextInt();
                if (selection == 0) {
                    System.out.println("Exiting customer mode.");
                    System.exit(0);
                } else if (selection > 0 && selection <= allConcerts.size()) {
                    // Adjusting index to 0-based by subtracting 1
                    Concert selectedConcert = allConcerts.get(selection - 1);
                    if (selectedConcert != null) {
                        CustomerMenu customerMenu = new CustomerMenu((VenueManager) venueManager, (BookingManager) bookingManager);
                        customerMenu.setSelectedConcert(selectedConcert);
                        customerMenu.showMenu();
                        break;
                    } else {
                        System.out.println("Invalid concert selection. Please try again.");
                    }
                } else {
                    System.out.println("Invalid concert selection. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Clear the invalid input
            }
        }
//        System.out.println("Available concerts: " + allConcerts.size());
//        System.out.println("User selection index: " + (selection - 1));

    }

    // Save data back to files
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
