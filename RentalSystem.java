import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RentalSystem {
    private static RentalSystem instance;
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();

    private RentalSystem() {
        loadData();
    }

    
    public static RentalSystem getInstance() {
        if (instance == null) {
            instance = new RentalSystem();
        }
        return instance;
    }

    // File storage methods
    public void saveVehicle(Vehicle vehicle) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("vehicles.txt", true))) {
            writer.write(vehicle.getLicensePlate() + "," + vehicle.getMake() + "," + vehicle.getModel() + "," + vehicle.getYear() + "," + vehicle.getClass().getSimpleName());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving vehicle: " + e.getMessage());
        }
    }

    public void saveCustomer(Customer customer) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("customers.txt", true))) {
            writer.write(customer.getCustomerId() + "," + customer.getCustomerName());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving customer: " + e.getMessage());
        }
    }

    public void saveRecord(RentalRecord record) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("rental_records.txt", true))) {
            writer.write(record.getVehicle().getLicensePlate() + "," + record.getCustomer().getCustomerId() + "," + record.getRecordDate() + "," + record.getTotalAmount() + "," + record.getRecordType());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving record: " + e.getMessage());
        }
    }

    //  Modified to return boolean and check duplicates
    public boolean addVehicle(Vehicle vehicle) {
        if (findVehicleByPlate(vehicle.getLicensePlate()) != null) {
            System.out.println("Vehicle with license plate " + vehicle.getLicensePlate() + " already exists.");
            return false;
        }
        vehicles.add(vehicle);
        saveVehicle(vehicle);
        return true;
    }

    //  Modified to return boolean and check duplicates
    public boolean addCustomer(Customer customer) {
        if (findCustomerById(String.valueOf(customer.getCustomerId())) != null) {
            System.out.println("Customer with ID " + customer.getCustomerId() + " already exists.");
            return false;
        }
        customers.add(customer);
        saveCustomer(customer);
        return true;
    }

    //Updated to call saveRecord
    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
            vehicle.setStatus(Vehicle.VehicleStatus.RENTED);
            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(record);
            saveRecord(record);
            System.out.println("Vehicle rented to " + customer.getCustomerName());
        } else {
            System.out.println("Vehicle is not available for renting.");
        }
    }

    public void returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.RENTED) {
            vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
            RentalRecord record = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(record);
            saveRecord(record);
            System.out.println("Vehicle returned by " + customer.getCustomerName());
        } else {
            System.out.println("Vehicle is not rented.");
        }
    }

    // Load data from files
    private void loadData() {
        // Load vehicles
        try (BufferedReader reader = new BufferedReader(new FileReader("vehicles.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String plate = parts[0];
                    String make = parts[1];
                    String model = parts[2];
                    int year = Integer.parseInt(parts[3]);
                    String type = parts[4];
                    Vehicle vehicle;
                    switch (type) {
                        case "Car":
                            vehicle = new Car(make, model, year, 5);
                            break;
                        case "Motorcycle":
                            vehicle = new Motorcycle(make, model, year, false);
                            break;
                        case "Truck":
                            vehicle = new Truck(make, model, year, 1000);
                            break;
                        default:
                            continue;
                    }
                    vehicle.setLicensePlate(plate);
                    vehicles.add(vehicle);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading vehicles: " + e.getMessage());
        }

        // Load customers
        try (BufferedReader reader = new BufferedReader(new FileReader("customers.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    customers.add(new Customer(id, name));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading customers: " + e.getMessage());
        }

        // Load rental records
        try (BufferedReader reader = new BufferedReader(new FileReader("rental_records.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String plate = parts[0];
                    int customerId = Integer.parseInt(parts[1]);
                    LocalDate date = LocalDate.parse(parts[2]);
                    double amount = Double.parseDouble(parts[3]);
                    String type = parts[4];
                    Vehicle vehicle = findVehicleByPlate(plate);
                    Customer customer = findCustomerById(String.valueOf(customerId));
                    if (vehicle != null && customer != null) {
                        rentalHistory.addRecord(new RentalRecord(vehicle, customer, date, amount, type));
                        if (type.equals("RENT")) {
                            vehicle.setStatus(Vehicle.VehicleStatus.RENTED);
                        } else if (type.equals("RETURN")) {
                            vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading rental records: " + e.getMessage());
        }
    }

    
    public void displayVehicles(boolean onlyAvailable) {
        System.out.println("|     Type         |\tPlate\t|\tMake\t|\tModel\t|\tYear\t|");
        System.out.println("---------------------------------------------------------------------------------");
        for (Vehicle v : vehicles) {
            if (!onlyAvailable || v.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
                System.out.println("|     " + (v instanceof Car ? "Car          " : v instanceof Motorcycle ? "Motorcycle   " : "Truck        ") + "|\t" + v.getLicensePlate() + "\t|\t" + v.getMake() + "\t|\t" + v.getModel() + "\t|\t" + v.getYear() + "\t|\t");
            }
        }
        System.out.println();
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }

    public void displayRentalHistory() {
        for (RentalRecord record : rentalHistory.getRentalHistory()) {
            System.out.println(record.toString());
        }
    }

    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : personally {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }

    public Customer findCustomerById(String id) {
        for (Customer c : customers) {
            if (c.getCustomerId() == Integer.parseInt(id)) {
                return c;
            }
        }
        return null;
    }
}