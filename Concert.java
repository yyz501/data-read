import java.util.List;
import java.util.Map;

public class Concert {
    private int id;
    private String date;
    private String timing;
    private String artistName;
    private String venueName;
    private PriceInfo standing;
    private PriceInfo seating;
    private PriceInfo vip;
    private BookingManager bookingManager;
    private VenueManager venueManager;

    public Concert(int id, String date, String timing, String artistName, String venueName,
                   PriceInfo standing, PriceInfo seating, PriceInfo vip,
                   BookingManager bookingManager, VenueManager venueManager) {
        this.id = id;
        this.date = date;
        this.timing = timing;
        this.artistName = artistName;
        this.venueName = venueName;
        this.standing = standing;
        this.seating = seating;
        this.vip = vip;
        this.bookingManager = bookingManager;
        this.venueManager = venueManager;

    }


    // Getters for fields are omitted for brevity

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }


    public String getTiming() {
        return timing;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getVenueName() {
        return venueName;
    }

    public PriceInfo getStanding() {
        return standing;
    }

    public PriceInfo getSeating() {
        return seating;
    }

    public PriceInfo getVip() {
        return vip;
    }

    public double getPriceByAisle(String aisle) {
        // Example logic to determine the price based on aisle
        if (aisle.startsWith("V")) {
            return vip.getRightSeatPrice(); // Assuming aisles starting with 'A' are VIP
        } else if (aisle.startsWith("S")) {
            return seating.getCentreSeatPrice(); // and so on
        } else {
            return standing.getLeftSeatPrice(); // Default or other cases
        }
    }

    public String getSeatTypeByAisle(String aisle) {
        if (aisle.startsWith("V")) {
            return "VIP";  // 假设以'V'开头的是VIP区
        } else if (aisle.startsWith("S")) {
            return "Seating";  // 假设以'S'开头的是普通座位区
        } else {
            return "Standing";  // 其他假定为站票区
        }
    }

    public int getBookedSeats() {

        int totalBookedSeats = 0;
        List<Booking> allBookings = bookingManager.getAllRecords();  // Ensure bookingManager is accessible
        for (Booking booking : allBookings) {
            if (booking.getConcertId() == this.id) {
                totalBookedSeats += booking.getTickets().size();
            }
        }
        return totalBookedSeats;
    }

    public int getTotalSeats() {
        try {
            Map<String, List<String>> venueLayout = venueManager.getVenueLayout(this.venueName);
            int totalSeats = 0;
            for (List<String> rows : venueLayout.values()) {
                for (String seat : rows) {
                    // Assuming seats are represented as single characters or a list of seat strings
                    totalSeats += seat.length();  // If seat is a string of available seat IDs
                }
            }
            return totalSeats;
        } catch (Exception e) {
            System.err.println("Error retrieving layout for venue " + this.venueName + ": " + e.getMessage());
            return 0;
        }
//    }
    }
}
