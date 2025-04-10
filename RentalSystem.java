import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class RentalSystem {
    // ... (previous code)

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

    // Update existing methods to call these
    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        saveVehicle(vehicle); // Call save method
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
        saveCustomer(customer); // Call save method
    }

    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
            vehicle.setStatus(Vehicle.VehicleStatus.RENTED);
            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(record);
            saveRecord(record); // Call save method
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
            saveRecord(record); // Call save method
            System.out.println("Vehicle returned by " + customer.getCustomerName());
        } else {
            System.out.println("Vehicle is not rented.");
        }
    }

    // Add getters for testing purposes later
    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public RentalHistory getRentalHistory() {
        return rentalHistory;
    }
}