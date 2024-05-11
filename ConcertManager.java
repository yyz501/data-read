import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConcertManager implements DataManager<List<Concert>> {
    private Map<Integer, Concert> concerts = new HashMap<>();
    private BookingManager bookingManager;
    private VenueManager venueManager;

    public ConcertManager(BookingManager bookingManager, VenueManager venueManager) {
        this.bookingManager = bookingManager;
        this.venueManager = venueManager;
    }
    @Override
    public void loadData(String concertFilePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(concertFilePath));
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                String[] data = line.split(",");
                if (data.length != 8) {
                    System.out.println("Skipping malformed line: " + line);
                    continue;
                }
                int concertId = Integer.parseInt(data[0].trim());
                String date = data[1].trim();
                String timing = data[2].trim();
                String artistName = data[3].trim();
                String venueName = data[4].trim();
                PriceInfo standing = parsePriceInfo(data[5].trim());
                PriceInfo seating = parsePriceInfo(data[6].trim());
                PriceInfo vip = parsePriceInfo(data[7].trim());
                concerts.put(concertId, new Concert(concertId, date, artistName,timing, venueName, standing, seating, vip,this.bookingManager, this.venueManager));
            } catch (NumberFormatException e) {
                System.out.println("Error parsing line: " + line + "; Error: " + e.getMessage());
            }
        }
        reader.close();
    }

    private PriceInfo parsePriceInfo(String priceInfo) {
        String[] parts = priceInfo.split(":");
        return new PriceInfo(Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
    }

    @Override
    public List<Concert> getAllRecords() {
        return new ArrayList<>(concerts.values());
    }
    // Method to get a customer by ID
}

class PriceInfo {
    private double leftSeatPrice;
    private double centreSeatPrice;
    private double rightSeatPrice;

    public PriceInfo(double left, double centre, double right) {
        this.leftSeatPrice = left;
        this.centreSeatPrice = centre;
        this.rightSeatPrice = right;
    }

    public double getLeftSeatPrice() {
        return leftSeatPrice;
    }

    public double getCentreSeatPrice() {
        return centreSeatPrice;
    }

    public double getRightSeatPrice() {
        return rightSeatPrice;
    }

    public String formatForCsv() {
        return String.format("%.2f:%.2f:%.2f", leftSeatPrice, centreSeatPrice, rightSeatPrice);
    }

    public void setLeftSeatPrice(double leftPrice) {
    }

    public void setCentreSeatPrice(double centerPrice) {
    }

    public void setRightSeatPrice(double rightPrice) {
    }
}

