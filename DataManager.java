import java.io.IOException;
import java.util.List;

public interface DataManager<T> {
    void loadData(String filePath) throws IOException;
    void saveData(String filePath) throws IOException;
    List<T> getAllRecords();
}

