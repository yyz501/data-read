import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConcertManager implements DataManager<List<Concert>> {
    private Map<Integer, Concert> concerts = new HashMap<>();

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
                concerts.put(concertId, new Concert(concertId, date, timing, artistName, venueName, standing, seating, vip));
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
}

class Concert {
    private int id;
    private String date;
    private String timing;
    private String artistName;
    private String venueName;
    private PriceInfo standing;
    private PriceInfo seating;
    private PriceInfo vip;

    public Concert(int id, String date, String timing, String artistName, String venueName, PriceInfo standing, PriceInfo seating, PriceInfo vip) {
        this.id = id;
        this.date = date;
        this.timing = timing;
        this.artistName = artistName;
        this.venueName = venueName;
        this.standing = standing;
        this.seating = seating;
        this.vip = vip;
    }

    // Getters for fields are omitted for brevity

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public PriceInfo getStanding() {
        return standing;
    }

    public void setStanding(PriceInfo standing) {
        this.standing = standing;
    }

    public PriceInfo getSeating() {
        return seating;
    }

    public void setSeating(PriceInfo seating) {
        this.seating = seating;
    }

    public PriceInfo getVip() {
        return vip;
    }

    public void setVip(PriceInfo vip) {
        this.vip = vip;
    }

    public int getBookedSeats() {

        return 0;
    }

    public int getTotalSeats() {
        return 0;
    }
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
}

