import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CustomerMenu extends MainMenu {
    private Concert selectedConcert;
    private VenueManager venueManager;
    private BookingManager bookingManager;
    private String bookingFilePath; // Add this line
    private Customer customer;

    public CustomerMenu(VenueManager venueManager, BookingManager bookingManager) {
        super();
        this.venueManager = venueManager;
        this.bookingManager = bookingManager;
    }

    public void setSelectedConcert(Concert concert) {
        this.selectedConcert = concert;
    }
    @Override
    public void showMenu() {
        do {
            System.out.println("Select an option to get started!");
            System.out.println("Press 1 to look at the ticket costs");
            System.out.println("Press 2 to view seats layout");
            System.out.println("Press 3 to book seats");
            System.out.println("Press 4 to view booking details");
            System.out.println("Press 5 to exit");
            System.out.print("> ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Discard non-integer input
            }
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    showTicketCosts();
                    break;
                case 2:
                    try {
                        displayVenueLayout();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case 3:
                    bookSeats();
                    break;
                case 4:
                    //viewBookingDetails();
                    break;
                case 5:
                    System.out.println("Exiting customer mode");
                    return;
                default:
                    System.out.println("Invalid Input");
                    break;
            }
        } while (true);
    }

    private void bookSeats() {
        try {
            displayVenueLayout();
            System.out.print("Enter the aisle number: ");
            String aisle = scanner.next();
            System.out.print("Enter the seat number: ");
            int startSeat = scanner.nextInt();
            System.out.print("Enter the number of seats to be booked: ");
            int numSeats = scanner.nextInt();

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
        List<Ticket> newTickets = new ArrayList<>();

        for (int i = 0; i < numSeats; i++) {
            int seatIndex = startSeat - 1 + i;
            if (seatIndex < seatsInAisle.size()) {
                String seat = seatsInAisle.get(seatIndex);
                newTickets.add(new Ticket(generateNewTicketId(),
                        Integer.parseInt(aisle.substring(1)),
                        Integer.parseInt(seat), aisle, 100.00)); // 示例价格
            }
        }

        if (!newTickets.isEmpty()) {
            Booking newBooking = new Booking(generateNewBookingId(), customer.getId(),
                    customer.getName(), selectedConcert.getId(), newTickets);
            bookingManager.addBooking(newBooking);
            bookingManager.saveData(bookingFilePath);  // Assuming this method handles the persistence
            return true;
        }
        return false;
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

    public void displayVenueLayout() throws IOException {
        String venueName = selectedConcert.getVenueName().toLowerCase();
        System.out.println("Displaying layout for venue: " + venueName);

        Map<String, Map<String, List<String>>> allLayouts = venueManager.getAllRecords();
        Map<String, List<String>> currentLayout = allLayouts.get(venueName);

        Map<String, Set<String>> bookedSeats = bookingManager.getBookedSeatsByConcertId(selectedConcert.getId());
        currentLayout.forEach((section, seatsList) -> {
            System.out.print(section + " ");
            seatsList.forEach(seat -> {
                if (bookedSeats.getOrDefault(section, Collections.emptySet()).contains(seat)) {
                    System.out.print("[X]");
                } else {
                    System.out.print("[" + seat + "]");
                }
            });
            System.out.println();  // End the line after printing all seats in the section
        });
    }


    @Override
    public void showMenu(int parameter) {
    }
    private void showTicketCosts() {

        System.out.printf("---------- %8s ----------%n", "SEATING");
        System.out.printf("Left Seats:   %.2f%n", selectedConcert.getSeating().getLeftSeatPrice());
        System.out.printf("Center Seats: %.2f%n", selectedConcert.getSeating().getCentreSeatPrice());
        System.out.printf("Right Seats:  %.2f%n", selectedConcert.getSeating().getRightSeatPrice());
        System.out.println("------------------------------");
        System.out.printf("---------- %8s ----------%n", "STANDING");
        System.out.printf("Left Seats:   %.2f%n", selectedConcert.getStanding().getLeftSeatPrice());
        System.out.printf("Center Seats: %.2f%n", selectedConcert.getStanding().getCentreSeatPrice());
        System.out.printf("Right Seats:  %.2f%n", selectedConcert.getStanding().getRightSeatPrice());
        System.out.println("-------------------------------");
        System.out.printf("---------- %8s ----------%n", "VIP");
        System.out.printf("Left Seats:   %.2f%n", selectedConcert.getVip().getLeftSeatPrice());
        System.out.printf("Center Seats: %.2f%n", selectedConcert.getVip().getCentreSeatPrice());
        System.out.printf("Right Seats:  %.2f%n", selectedConcert.getVip().getRightSeatPrice());
        System.out.println("-------------------------------");
    }
}