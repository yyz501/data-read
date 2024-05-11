import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class VenueManager implements DataManager<Map<String, Map<String, List<String>>>> {
    private Map<String, Map<String, List<String>>> venueLayouts = new HashMap<>(); // 主Map存储不同场馆的数据

    public Map<String, List<String>> getVenueLayout(String venueName) throws IOException {
        return venueLayouts.get(venueName.toLowerCase());
    }
    public void loadData(String venueFilePath) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(venueFilePath));
        String line;
        Map<String, List<String>> currentLayout = new LinkedHashMap<>();
        String venueName = extractVenueName(venueFilePath);

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\s+", 2);
            if (parts.length < 2) continue;  // Skip malformed lines

            String sectionId = parts[0];
            List<String> seats = new ArrayList<>();
            // Process remaining part as a whole, preserving spaces
            String[] groups = parts[1].split("(?<=\\])(\\s+)(?=\\[)");
            for (String group : groups) {
                seats.add(group.replaceAll("\\[|\\]", ""));
            }
            currentLayout.put(sectionId, seats);
        }
        reader.close();
        venueLayouts.put(venueName, currentLayout);
    }

    @Override
    public Map<String, Map<String, List<String>>> getAllRecords() {
        return new HashMap<>(venueLayouts); // 返回整个场馆数据的副本
    }

    // 从文件路径中提取场馆名的方法，同时处理前缀 "venue_"
    private String extractVenueName(String venueFilePath) {
        String fileName = venueFilePath.substring(venueFilePath.lastIndexOf('/') + 1); // 获取文件名 "venue_marvel.txt"
        fileName = fileName.substring(0, fileName.lastIndexOf('.')); // 移除扩展名，得到 "venue_marvel"
        if (fileName.startsWith("venue_")) {
            return fileName.substring(6); // 剔除前缀 "venue_"，返回 "marvel"
        }
        return fileName; // 如果没有预期的前缀，则返回整个文件名
    }
}
