import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

// No package declaration here — add one if needed
public class VehicleRentalTest {

    private RentalSystem rentalSystem;

    // ✅ Concrete subclass of abstract Vehicle for testing purposes
    static class TestVehicle extends Vehicle {
        public TestVehicle(String make, String model, int year) {
            super(make, model, year);
        }
    }

    @BeforeEach
    void setUp() {
        rentalSystem = RentalSystem.getInstance();
    }

    @Test
    void testLicensePlateValidation() {
        // Valid license plates
        Vehicle v1 = new TestVehicle("Ford", "Fusion", 2020);
        assertDoesNotThrow(() -> v1.setLicensePlate("AAA100"));

        Vehicle v2 = new TestVehicle("Toyota", "Camry", 2019);
        assertDoesNotThrow(() -> v2.setLicensePlate("ABC567"));

        Vehicle v3 = new TestVehicle("Honda", "Civic", 2021);
        assertDoesNotThrow(() -> v3.setLicensePlate("ZZZ999"));

        // Invalid license plates
        Vehicle invalid1 = new TestVehicle("Test", "Invalid1", 2020);
        assertThrows(IllegalArgumentException.class, () -> invalid1.setLicensePlate(""));

        Vehicle invalid2 = new TestVehicle("Test", "Invalid2", 2020);
        assertThrows(IllegalArgumentException.class, () -> invalid2.setLicensePlate(null));

        Vehicle invalid3 = new TestVehicle("Test", "Invalid3", 2020);
        assertThrows(IllegalArgumentException.class, () -> invalid3.setLicensePlate("AAA1000"));

        Vehicle invalid4 = new TestVehicle("Test", "Invalid4", 2020);
        assertThrows(IllegalArgumentException.class, () -> invalid4.setLicensePlate("ZZZ99"));
    }
    
    @Test
    void testRentAndReturnVehicle() {
        TestVehicle vehicle = new TestVehicle("Nissan", "Altima", 2022);
        vehicle.setLicensePlate("XYZ123");

        Customer customer = new Customer(1, "John Doe");

        // Initially available
        assertEquals(Vehicle.VehicleStatus.AVAILABLE, vehicle.getStatus());

        boolean rentSuccess = rentalSystem.rentVehicle(vehicle, customer, LocalDate.now(), 500.0);
        assertTrue(rentSuccess);
        assertEquals(Vehicle.VehicleStatus.RENTED, vehicle.getStatus());

        // Attempt to rent again should fail
        boolean rentAgain = rentalSystem.rentVehicle(vehicle, customer, LocalDate.now(), 500.0);
        assertFalse(rentAgain);

        // Return the vehicle
        boolean returnSuccess = rentalSystem.returnVehicle(vehicle, customer, LocalDate.now(), 0.0);
        assertTrue(returnSuccess);
        assertEquals(Vehicle.VehicleStatus.AVAILABLE, vehicle.getStatus());

        // Attempt to return again should fail
        boolean returnAgain = rentalSystem.returnVehicle(vehicle, customer, LocalDate.now(), 0.0);
        assertFalse(returnAgain);
    }
   
}