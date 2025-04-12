import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;


import java.time.LocalDate;

public class RentalSystemGUI extends Application {

    private RentalSystem rentalSystem = RentalSystem.getInstance();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Vehicle Rental System");

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
                createAddCustomerTab(),
                createAddVehicleTab(),
                createRentVehicleTab(),
                createReturnVehicleTab(),
                createViewVehiclesTab(),
                createRentalHistoryTab()
        );

        Scene scene = new Scene(tabPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Tab createAddCustomerTab() {
        TextField idField = new TextField();
        TextField nameField = new TextField();
        Button addButton = new Button("Add Customer");
        Label resultLabel = new Label();

        addButton.setOnAction(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                Customer customer = new Customer(id, name);
                boolean success = rentalSystem.addCustomer(customer);
                resultLabel.setText(success ? "Customer added." : "Customer already exists.");
            } catch (Exception ex) {
                resultLabel.setText("Invalid ID");
            }
        });

        VBox layout = new VBox(10, new Label("Customer ID:"), idField,
                new Label("Customer Name:"), nameField, addButton, resultLabel);
        layout.setPadding(new Insets(10));

        return new Tab("Add Customer", layout);
    }

    private Tab createAddVehicleTab() {
        TextField plateField = new TextField();
        TextField makeField = new TextField();
        TextField modelField = new TextField();
        TextField yearField = new TextField();
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Car", "Motorcycle", "Truck");
        TextField extraField = new TextField();
        Button addButton = new Button("Add Vehicle");
        Label resultLabel = new Label();

        addButton.setOnAction(e -> {
            try {
                String type = typeBox.getValue();
                String plate = plateField.getText();
                String make = makeField.getText();
                String model = modelField.getText();
                int year = Integer.parseInt(yearField.getText());
                Vehicle vehicle = null;
                if (type.equals("Car")) {
                    int seats = Integer.parseInt(extraField.getText());
                    vehicle = new Car(make, model, year, seats);
                } else if (type.equals("Motorcycle")) {
                    boolean hasSidecar = Boolean.parseBoolean(extraField.getText());
                    vehicle = new Motorcycle(make, model, year, hasSidecar);
                } else if (type.equals("Truck")) {
                    double capacity = Double.parseDouble(extraField.getText());
                    vehicle = new Truck(make, model, year, capacity);
                }
                vehicle.setLicensePlate(plate);
                boolean success = rentalSystem.addVehicle(vehicle);
                resultLabel.setText(success ? "Vehicle added." : "Vehicle already exists.");
            } catch (Exception ex) {
                resultLabel.setText("Invalid input.");
            }
        });

        VBox layout = new VBox(10,
                new Label("Type:"), typeBox,
                new Label("Plate:"), plateField,
                new Label("Make:"), makeField,
                new Label("Model:"), modelField,
                new Label("Year:"), yearField,
                new Label("Seats / Sidecar (true/false) / Capacity:"), extraField,
                addButton, resultLabel);
        layout.setPadding(new Insets(10));

        return new Tab("Add Vehicle", layout);
    }

    private Tab createRentVehicleTab() {
        TextField plateField = new TextField();
        TextField customerIdField = new TextField();
        TextField amountField = new TextField();
        Button rentButton = new Button("Rent Vehicle");
        Label resultLabel = new Label();

        rentButton.setOnAction(e -> {
            Vehicle v = rentalSystem.findVehicleByPlate(plateField.getText().toUpperCase());
            Customer c = rentalSystem.findCustomerById(customerIdField.getText());
            try {
                double amt = Double.parseDouble(amountField.getText());
                if (v == null || c == null) {
                    resultLabel.setText("Vehicle or customer not found.");
                } else {
                    rentalSystem.rentVehicle(v, c, LocalDate.now(), amt);
                }
            } catch (Exception ex) {
                resultLabel.setText("Invalid amount.");
            }
        });

        VBox layout = new VBox(10, new Label("Plate:"), plateField,
                new Label("Customer ID:"), customerIdField,
                new Label("Amount:"), amountField,
                rentButton, resultLabel);
        layout.setPadding(new Insets(10));

        return new Tab("Rent Vehicle", layout);
    }

    private Tab createReturnVehicleTab() {
        TextField plateField = new TextField();
        TextField customerIdField = new TextField();
        TextField feeField = new TextField();
        Button returnButton = new Button("Return Vehicle");
        Label resultLabel = new Label();

        returnButton.setOnAction(e -> {
            Vehicle v = rentalSystem.findVehicleByPlate(plateField.getText().toUpperCase());
            Customer c = rentalSystem.findCustomerById(customerIdField.getText());
            try {
                double fee = Double.parseDouble(feeField.getText());
                if (v == null || c == null) {
                    resultLabel.setText("Vehicle or customer not found.");
                } else {
                    rentalSystem.returnVehicle(v, c, LocalDate.now(), fee);
                }
            } catch (Exception ex) {
                resultLabel.setText("Invalid fee.");
            }
        });

        VBox layout = new VBox(10, new Label("Plate:"), plateField,
                new Label("Customer ID:"), customerIdField,
                new Label("Fee:"), feeField,
                returnButton, resultLabel);
        layout.setPadding(new Insets(10));

        return new Tab("Return Vehicle", layout);
    }

    private Tab createViewVehiclesTab() {
        TextArea output = new TextArea();
        output.setEditable(false);
        Button refresh = new Button("Refresh List");

        refresh.setOnAction(e -> {
            output.clear();
            rentalSystem.displayVehicles(true);
        });

        VBox layout = new VBox(10, refresh, output);
        layout.setPadding(new Insets(10));
        return new Tab("Available Vehicles", layout);
    }

    private Tab createRentalHistoryTab() {
        TextArea output = new TextArea();
        output.setEditable(false);
        Button refresh = new Button("Refresh History");

        refresh.setOnAction(e -> {
            output.clear();
            rentalSystem.displayRentalHistory();
        });

        VBox layout = new VBox(10, refresh, output);
        layout.setPadding(new Insets(10));
        return new Tab("Rental History", layout);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
