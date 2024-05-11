import java.io.*;
import java.util.*;

public class BookingManager implements DataManager<ArrayList<Booking>> {
    private List<Booking> bookings = new ArrayList<>();

    @Override
    public void loadData(String bookingFilePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(bookingFilePath));
        String line;
        bookings.clear();  // Clear existing bookings before loading new ones
        while ((line = reader.readLine()) != null) {
            try {
                Booking booking = parseBooking(line);
                if (booking != null) {
                    bookings.add(booking);
                    //System.out.println("Loaded booking: " + booking);
                }
            } catch (Exception e) {
                System.out.println("Skipping invalid booking data line: " + line);
            }
        }
        reader.close();
        //  System.out.println("All bookings successfully loaded from " + bookingFilePath);
    }

    public void addBooking(Booking newBooking) {
        bookings.add(newBooking);
    }
    private Booking parseBooking(String line) {
        String[] parts = line.split(",");
        if (parts.length < 10) return null; // Basic validation
        int index = 0;
        int bookingId = Integer.parseInt(parts[index++].trim());
        int customerId = Integer.parseInt(parts[index++].trim());
        String customerName = parts[index++].trim();
        int concertId = Integer.parseInt(parts[index++].trim());
        int totalTickets = Integer.parseInt(parts[index++].trim());

        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < totalTickets; i++) {
            int ticketId = Integer.parseInt(parts[index++].trim());
            int rowNumber = Integer.parseInt(parts[index++].trim());
            int seatNumber = Integer.parseInt(parts[index++].trim());
            String zoneType = parts[index++].trim();
            double price = Double.parseDouble(parts[index++].trim());
            tickets.add(new Ticket(ticketId, rowNumber, seatNumber, zoneType, price));
        }
        return new Booking(bookingId, customerId, customerName, concertId, tickets);
    }

    public void saveData(String bookingFilePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(bookingFilePath, true)); // 确保以追加模式写入
        for (Booking booking : bookings) {
            StringBuilder builder = new StringBuilder();
            builder.append(booking.getBookingId()).append(",");
            builder.append(booking.getCustomerId()).append(",");
            builder.append(booking.getCustomerName()).append(",");
            builder.append(booking.getConcertId()).append(",");
            builder.append(booking.getTickets().size());
            for (Ticket ticket : booking.getTickets()) {
                builder.append(",").append(ticket.getTicketId());
                builder.append(",").append(ticket.getRowNumber());
                builder.append(",").append(ticket.getSeatNumber());
                builder.append(",").append(ticket.getZoneType());
                builder.append(",").append(ticket.getPrice());
            }
            writer.write(builder.toString());
            writer.newLine();
        }
        writer.close();
        System.out.println("Bookings saved to " + bookingFilePath);
    }

    @Override
    public ArrayList<Booking> getAllRecords() {
        ArrayList<Booking> bookings1 = new ArrayList<>(bookings);
        return bookings1; // 返回预订列表的一个副本
    }
    public Map<String, Set<String>> getBookedSeatsByConcertId(int concertId) {
        Map<String, Set<String>> bookedSeatsMap = new HashMap<>();
        for (Booking booking : bookings) {
            if (booking.getConcertId() == concertId) {
                for (Ticket ticket : booking.getTickets()) {
                    String seatLabel = String.valueOf(ticket.getSeatNumber()); // 使用座位号
                    bookedSeatsMap.computeIfAbsent(ticket.getZoneType(), k -> new HashSet<>()).add(seatLabel);
                }
            }
        }
        return bookedSeatsMap;
    }
    public List<Booking> getBookingsByConcertId(int concertId) {
        List<Booking> filteredBookings = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getConcertId() == concertId) {
                filteredBookings.add(booking);
            }
        }
        return filteredBookings;
    }

    public int generateNewBookingId() {
        return getAllRecords().stream().mapToInt(Booking::getBookingId).max().orElse(0) + 1;
    }

    public int generateNewTicketId() {
        return getAllRecords().stream()
                .flatMap(booking -> booking.getTickets().stream())
                .mapToInt(Ticket::getTicketId)
                .max()
                .orElse(0) + 1;
    }

}

class Booking {
    private int bookingId;
    private int customerId;
    private String customerName;
    private int concertId;
    private List<Ticket> tickets;

    public Booking(int bookingId, int customerId, String customerName, int concertId, List<Ticket> tickets) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.concertId = concertId;
        this.tickets = tickets;
    }

    // Getters
    public int getBookingId() {
        return bookingId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getConcertId() {
        return concertId;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }
}

class Ticket {
    private int ticketId;
    private int rowNumber;
    private int seatNumber;
    private String zoneType;
    private double price;

    public Ticket(int ticketId, int rowNumber, int seatNumber, String zoneType, double price) {
        this.ticketId = ticketId;
        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;
        this.zoneType = zoneType;
        this.price = price;
    }

    public int getTicketId() {
        return ticketId;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public String getZoneType() {
        return zoneType;
    }

    public double getPrice() {
        return price;
    }

}
