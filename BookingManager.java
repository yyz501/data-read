import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BookingManager implements DataManager<Booking> {
    private List<Booking> bookings = new ArrayList<>();

    @Override
    public void loadData(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                Booking booking = parseBooking(line);
                if (booking != null) {
                    bookings.add(booking);
                }
            } catch (Exception e) {
                System.out.println("Skipping invalid booking data line: " + line);
            }
        }
        reader.close();
    }

    private Booking parseBooking(String line) {
        String[] parts = line.split(",");
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

    @Override
    public void saveData(String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
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
    }
    @Override
    public List<Booking> getAllRecords() {
        return new ArrayList<>(bookings); // 返回预订列表的一个副本
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

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getZoneType() {
        return zoneType;
    }

    public void setZoneType(String zoneType) {
        this.zoneType = zoneType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Getters and setters are omitted for brevity
}



