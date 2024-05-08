import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VenueManager implements DataManager<String> {
    private List<String> venueLayout = new ArrayList<>();

    @Override
    public void loadData(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            venueLayout.add(line);
        }
        reader.close();
    }

    @Override
    public void saveData(String filePath) throws IOException {
        // Generally, we do not save venue data back to files as mentioned
    }
    @Override
    public List<String> getAllRecords() {
        return new ArrayList<>(venueLayout); // 返回场馆布局列表的一个副本
    }
}

// No additional classes needed for VenueManager as it just handles layout strings

