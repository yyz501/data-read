import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
public class AdminMenu extends MainMenu {
    private ConcertManager concertManager;
    private BookingManager bookingManager;
    private VenueManager venueManager;

    public AdminMenu(Scanner scanner, ConcertManager concertManager, BookingManager bookingManager, VenueManager venueManager) {
        super(scanner);
        this.concertManager = concertManager;
        this.bookingManager = bookingManager;
        this.venueManager = venueManager;
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


            try {
                System.out.print("> ");
                choice = TicketManagementEngine.scanner.nextInt();

                switch (choice) {
                    case 1:
                        displayConcerts();
                        break;
                    case 2:
                        updateTicketCosts();
                        break;
                    case 3:
                        viewBookingDetails();
                        break;
                    case 4:
                        displayTotalPayments();
                        break;
                    case 5:
                        System.out.println("Exiting admin mode");
                        return;
                    default:
                        System.out.println("Invalid Input");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input, please enter a number.");
                TicketManagementEngine.scanner.nextLine();
            }
        } while (true);
    }

    @Override
    public void showMenu(int parameter) {

    }

    private void displayConcerts() {
        System.out.println("Select a concert or 0 to exit");
        List<Concert> concerts = concertManager.getAllRecords();
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

    }

    private void updateTicketCosts() {
        displayConcerts();  // Show all concerts for the admin to choose
        System.out.print("> ");
        int concertChoice = TicketManagementEngine.scanner.nextInt();

        if (concertChoice == 0) {
            return;  // Exit the update process
        }

        List<Concert> concerts = concertManager.getAllRecords();
        Concert selectedConcert = concerts.get(concertChoice - 1);
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


        // Allow admin to update prices
        System.out.println("Enter the zone : VIP, SEATING, STANDING: ");
        String zone = TicketManagementEngine.scanner.next().toUpperCase();
        PriceInfo priceInfo;

        switch (zone) {
            case "SEATING":
                priceInfo = selectedConcert.getSeating();
                break;
            case "STANDING":
                priceInfo = selectedConcert.getStanding();
                break;
            case "VIP":
                priceInfo = selectedConcert.getVip();
                break;
            default:
                System.out.println("Invalid zone.");
                return;
        }

        System.out.print("Left zone price: ");
        double leftPrice = TicketManagementEngine.scanner.nextDouble();
        System.out.print("Centre zone price: ");
        double centerPrice = TicketManagementEngine.scanner.nextDouble();
        System.out.print("Right zone price: ");
        double rightPrice = TicketManagementEngine.scanner.nextDouble();

        // Update the prices
        priceInfo.setLeftSeatPrice(leftPrice);
        priceInfo.setCentreSeatPrice(centerPrice);
        priceInfo.setRightSeatPrice(rightPrice);

    }

    private void viewBookingDetails() {
        displayConcerts();
        System.out.print("> ");
        int concertChoice = TicketManagementEngine.scanner.nextInt();
        if (concertChoice == 0) {
            return;
        }

        List<Concert> concerts = concertManager.getAllRecords();
        Concert selectedConcert = concerts.get(concertChoice - 1);

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

    private void displayTotalPayments() {
        System.out.println("Select a concert or 0 to exit");
        displayConcerts();  // Display all concerts for selection
        System.out.print("> ");
        int concertChoice = TicketManagementEngine.scanner.nextInt();
        if (concertChoice == 0) {
            return;  // Exit the method if the user selects 0
        }

        List<Concert> concerts = concertManager.getAllRecords();
        if (concertChoice < 1 || concertChoice > concerts.size()) {
            System.out.println("Invalid concert selection.");
            return;
        }

        Concert selectedConcert = concerts.get(concertChoice - 1);
        List<Booking> bookings = bookingManager.getBookingsByConcertId(selectedConcert.getId());

        double totalPayment = 0;
        for (Booking booking : bookings) {
            totalPayment += booking.getTickets().stream().mapToDouble(Ticket::getPrice).sum();
        }

        System.out.println("Total payment received for concert " + "is AUD " + String.format("%.1f", totalPayment));
    }

}
