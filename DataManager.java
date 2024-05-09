
import java.io.*;
public interface DataManager<T> {
    void loadData(String filePath) throws IOException;
    T getAllRecords();

}

