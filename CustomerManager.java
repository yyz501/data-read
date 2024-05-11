import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerManager implements DataManager<List<Customer>> {
    private List<Customer> customers = new ArrayList<>();
    private Customer currentCustomer;

    @Override
    public void loadData(String customerFilePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(customerFilePath));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            if (data.length == 3) {
                int id = Integer.parseInt(data[0].trim());
                String name = data[1].trim();
                String password = data[2].trim();
                customers.add(new Customer(id, name, password));
            }
        }
        reader.close();
    }

    @Override
    public List<Customer> getAllRecords() {
        return new ArrayList<>(customers);
    }

    public void setCurrentCustomer(int customerId) {
        this.currentCustomer = customers.stream()
                .filter(customer -> customer.getId() == customerId)
                .findFirst()
                .orElse(null);
    }

    public int getCurrentCustomerId() {
        return currentCustomer != null ? currentCustomer.getId() : -1;
    }

    public String getCurrentCustomerName() {
        return currentCustomer != null ? currentCustomer.getName() : "Unknown";
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

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
