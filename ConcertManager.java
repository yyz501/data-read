import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConcertManager implements DataManager<Concert> {
    private List<Concert> concerts = new ArrayList<>();

    @Override
    public void loadData(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            if (data.length == 8) {
                int concertId = Integer.parseInt(data[0].trim());
                String date = data[1].trim();
                String timing = data[2].trim();
                String artistName = data[3].trim();
                String venueName = data[4].trim();
                PriceInfo standing = parsePriceInfo(data[5].trim());
                PriceInfo seating = parsePriceInfo(data[6].trim());
                PriceInfo vip = parsePriceInfo(data[7].trim());
                concerts.add(new Concert(concertId, date, timing, artistName, venueName, standing, seating, vip));
            }
        }
        reader.close();
    }

    private PriceInfo parsePriceInfo(String priceInfo) {
        String[] parts = priceInfo.split(":");
        return new PriceInfo(Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
    }

    @Override
    public void saveData(String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        for (Concert concert : concerts) {
            String line = String.format("%-5s%-15s%-15s%-15s%-30s%-15s%-15s%-15s%n",
                    concert.getId(), concert.getDate(), concert.getTiming(), concert.getArtistName(), concert.getVenueName(),
                    concert.getStanding().formatForCsv(), concert.getSeating().formatForCsv(), concert.getVip().formatForCsv());
            writer.write(line);
            writer.newLine();
        }
        writer.close();
    }
    @Override
    public List<Concert> getAllRecords() {
        return new ArrayList<>(concerts); // 返回演唱会列表的一个副本
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
}

class PriceInfo {
    private double leftSeatPrice;
    private double centreSeatPrice;
    private double rightSeatPrice;

    public PriceInfo(double leftSeatPrice, double centreSeatPrice, double rightSeatPrice) {
        this.leftSeatPrice = leftSeatPrice;
        this.centreSeatPrice = centreSeatPrice;
        this.rightSeatPrice = rightSeatPrice;
    }

    public String formatForCsv() {
        return String.format("STANDING:%.2f:%.2f:%.2f", leftSeatPrice, centreSeatPrice, rightSeatPrice);
    }
}
