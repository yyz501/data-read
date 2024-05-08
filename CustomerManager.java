import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerManager implements DataManager<Customer> {
    private List<Customer> customers = new ArrayList<>();

    @Override
    public void loadData(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            if (data.length == 3) {
                int id = Integer.parseInt(data[0].trim());
                String name = data[1].trim();
                String password = data[2].trim();
                Customer customer = new Customer(id, name, password);
                customers.add(customer);
               // System.out.println("Loaded customer: ID=" + id + ", Name=" + name);
            } else {
               // System.out.println("Invalid customer data line. Skipping: " + line);
            }
        }
        reader.close();
    }

    @Override
    public void saveData(String customerFilePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(customerFilePath));
        for (Customer customer : customers) {
            writer.write(customer.getId() + "," + customer.getName() + "," + customer.getPassword() + "\n");
        }
        writer.close();
    }

    @Override
    public List<Customer> getAllRecords() {
        return new ArrayList<>(customers);
    }


}

class Customer {
    private int id;
    private String name;
    private String password;

    public Customer(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}