import java.io.IOException;
import java.util.*;

public class CustomerMenu extends MainMenu {
    private Concert selectedConcert;
    private VenueManager venueManager;
    private BookingManager bookingManager;
    private String bookingFilePath; // Add this line
    private Customer customer;
    private ConcertManager concertManager;
    private CustomerManager customerManager;

    public CustomerMenu(Scanner scanner, VenueManager venueManager, BookingManager bookingManager, CustomerManager customerManager, ConcertManager concertManager) {
        super(TicketManagementEngine.scanner);
        this.venueManager = venueManager;
        this.bookingManager = bookingManager;
        this.customerManager = customerManager;
        this.concertManager = concertManager;  // Correctly assign the passed concertManager
    }


    public void setSelectedConcert(Concert concert) {
        this.selectedConcert = concert;
    }
    @Override
    public void showMenu() {
        boolean keepRunning = true;
        while (keepRunning) {
            System.out.println("Select an option to get started!");
            System.out.println("Press 1 to look at the ticket costs");
            System.out.println("Press 2 to view seats layout");
            System.out.println("Press 3 to book seats");
            System.out.println("Press 4 to view booking details");
            System.out.println("Press 5 to exit");
            System.out.print("> ");

            int choice = TicketManagementEngine.scanner.nextInt();
            switch (choice) {
                case 1:
                    showTicketCosts();
                    break;
                case 2:
                    viewSeatsLayout();
                    break;
                case 3:
                    bookSeats();
                    break;
                case 4:
                    viewBookingDetails();
                    break;
                case 5:
                    System.out.println("Exiting this concert");
                    keepRunning = false;
                    return;
                default:
                    System.out.println("Invalid Input");
                    break;
            }
        } // } while (true);
    }

    private void viewBookingDetails() {
        List<Concert> concerts = concertManager.getAllRecords();
        List<Booking> bookings = bookingManager.getBookingsByConcertId(selectedConcert.getId());
        System.out.println("Bookings");
        System.out.println("--------------------------------------------" +
                "-------------------------------------------------------------------------------");
        System.out.printf("%-5s%-15s%-15s%-10s%-15s%-15s%-10s%n", "Id",
                "Concert Date", "Artist Name", "Timing", "Venue Name", "Seats Booked", "Total Price");
        System.out.println("--------------------------------------------" +
                "-------------------------------------------------------------------------------");
        for (Booking booking : bookings) {
            double totalPrice = booking.getTickets().stream().mapToDouble(Ticket::getPrice).sum();
            System.out.printf("%-5d%-15s%-15s%-10s%-15s%-15d%-10.1f%n",
                    booking.getBookingId(),
                    selectedConcert.getDate(),
                    selectedConcert.getTiming(),
                    selectedConcert.getArtistName(),
                    selectedConcert.getVenueName(),
                    booking.getTickets().size(),
                    totalPrice);
        }
        System.out.println("---------------------------------------------" +
                "------------------------------------------------------------------------------");

        // Optionally, show detailed tickets for each booking
        System.out.println("Ticket Info");
        for (Booking booking : bookings) {

            System.out.println("############### Booking Id: " + booking.getBookingId() + " ####################");
            System.out.printf("%-5s%-15s%-15s%-10s%-10s%n", "Id", "Aisle Number", "Seat Number", "Seat Type", "Price");
            System.out.println("##################################################");

            for (Ticket ticket : booking.getTickets()) {
                System.out.printf("%-5d%-15s%-15d%-10s%-10.1f%n",
                        ticket.getTicketId(),
                        ticket.getRowNumber(),
                        ticket.getSeatNumber(),
                        ticket.getZoneType(),
                        ticket.getPrice());
            }
            System.out.println("##################################################");
        }
    }

    private void bookSeats() {
        try {
            viewSeatsLayout();
            System.out.print("Enter the aisle number: ");
            String aisle = TicketManagementEngine.scanner.next();
            System.out.print("Enter the seat number: ");
            int startSeat = TicketManagementEngine.scanner.nextInt();
            System.out.print("Enter the number of seats to be booked: ");
            int numSeats = TicketManagementEngine.scanner.nextInt();

            if (bookSeatsInAisle(aisle, startSeat, numSeats)) {
                System.out.println("Booking successful.");
            } else {
                System.out.println("Booking failed. Please check the seat numbers and try again.");
            }
        } catch (Exception e) {
            System.out.println("Error during booking: " + e.getMessage());
        }
    }
    private boolean bookSeatsInAisle(String aisle, int startSeat, int numSeats) throws IOException {
        Map<String, List<String>> layout = venueManager.getAllRecords().get(selectedConcert.getVenueName().toLowerCase());
        List<String> seatsInAisle = layout.get(aisle);
        int rowNumber = Integer.parseInt(aisle.replaceAll("[^0-9]", ""));
        // Fetch seat type and price based on aisle
        String seatType = selectedConcert.getSeatTypeByAisle(aisle);
        double price = selectedConcert.getPriceByAisle(aisle);

        // Check availability and create tickets
        List<Ticket> newTickets = new ArrayList<>();
        for (int i = 0; i < numSeats; i++) {
            int seatIndex = startSeat - 1 + i;
            if (seatIndex >= seatsInAisle.size() || seatsInAisle.get(seatIndex).equals("[X]")) {
                System.out.println("One or more requested seats are not available.");
                return false;
            }
            // Create a ticket for each individual seat
            newTickets.add(new Ticket(generateNewTicketId(), rowNumber, startSeat + i, seatType, price));
            seatsInAisle.set(seatIndex, "[X]");  // Mark seat as booked
        }

        // Create a single booking for all tickets
        if (!newTickets.isEmpty()) {
            Booking newBooking = new Booking(generateNewBookingId(), customer.getId(),
                    customer.getName(), selectedConcert.getId(), newTickets);
            bookingManager.addBooking(newBooking);
            bookingManager.saveData(bookingFilePath);
        }

        return true;
    }


    private int generateNewBookingId() {
        return bookingManager.getAllRecords().stream().mapToInt(Booking::getBookingId).max().orElse(0) + 1;
    }

    private int generateNewTicketId() {
        return bookingManager.getAllRecords().stream()
                .flatMap(booking -> booking.getTickets().stream())
                .mapToInt(Ticket::getTicketId)
                .max()
                .orElse(0) + 1;
    }


    private void viewSeatsLayout() {
        if (selectedConcert == null) {
            System.out.println("No concert selected.");
            return;
        }

        try {
            String venueName = selectedConcert.getVenueName();
            Map<String, List<String>> layout = venueManager.getVenueLayout(venueName);
            if (layout == null) {
                System.out.println("No layout available for venue: " + venueName);
                return;
            }

            Map<String, Set<String>> bookedSeats = bookingManager.getBookedSeatsByConcertId(selectedConcert.getId());

            System.out.println("Venue Layout for: " + venueName);
            layout.forEach((section, seats) -> {
                System.out.print(section + " ");
                seats.forEach(row -> {
                    System.out.print("[");
                    String[] seatNumbers = row.split(",");
                    for (String seatNumber : seatNumbers) {
                        if (bookedSeats.containsKey(section) && bookedSeats.get(section).contains(seatNumber)) {
                            System.out.print("X");
                        } else {
                            System.out.print(seatNumber);
                        }
                    }
                    System.out.print("] ");
                });
                System.out.println();  // New line after each section
            });
        } catch (IOException e) {
            System.out.println("Failed to load venue layout: " + e.getMessage());
        }
    }


    @Override
    public void showMenu(int parameter) {
    }
    private void showTicketCosts() {


        System.out.printf("---------- %8s ----------%n", "STANDING");
        System.out.printf("Left Seats:   %.1f%n", selectedConcert.getStanding().getLeftSeatPrice());
        System.out.printf("Center Seats: %.1f%n", selectedConcert.getStanding().getCentreSeatPrice());
        System.out.printf("Right Seats:  %.1f%n", selectedConcert.getStanding().getRightSeatPrice());
        System.out.println("------------------------------");
        System.out.printf("---------- %8s ----------%n", "SEATING");
        System.out.printf("Left Seats:   %.1f%n", selectedConcert.getSeating().getLeftSeatPrice());
        System.out.printf("Center Seats: %.1f%n", selectedConcert.getSeating().getCentreSeatPrice());
        System.out.printf("Right Seats:  %.1f%n", selectedConcert.getSeating().getRightSeatPrice());
        System.out.println("------------------------------");
        System.out.printf("---------- %8s ----------%n", "VIP");
        System.out.printf("Left Seats:   %.1f%n", selectedConcert.getVip().getLeftSeatPrice());
        System.out.printf("Center Seats: %.1f%n", selectedConcert.getVip().getCentreSeatPrice());
        System.out.printf("Right Seats:  %.1f%n", selectedConcert.getVip().getRightSeatPrice());
        System.out.println("------------------------------");
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
    public void handleConcertSelection() {
        List<Concert> allConcerts = (List<Concert>) concertManager.getAllRecords();
        while (true) {
            if (!TicketManagementEngine.scanner.hasNextInt()) {
                break; // Exit if no input is available
            }
            int selection = TicketManagementEngine.scanner.nextInt();
            if (selection == 0) {
                System.out.println("Exiting customer mode");
                break;  // Breaks the loop, thus exiting the selection and potentially the program.
            } else if (selection > 0 && selection <= allConcerts.size()) {
                Concert selectedConcert = allConcerts.get(selection - 1);
                CustomerMenu customerMenu = new CustomerMenu(TicketManagementEngine.scanner, (VenueManager) venueManager,
                        (BookingManager) bookingManager, (CustomerManager) customerManager, (ConcertManager) concertManager);
                customerMenu.setSelectedConcert(selectedConcert);
                customerMenu.showMenu();
                // After exiting a specific concert menu, display all concerts again to allow further selections.
                displayConcerts();
            } else {
                System.out.println("Invalid Input.");
                continue; // Continue asking for input
            }
        }
    }
}