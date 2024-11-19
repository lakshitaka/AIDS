import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MileageCalculator {
    private static final double BASE_FACTOR = 5000;
    private static final String SYSTEM_PASSWORD = "14-Feb-05";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Password Protection
        System.out.print("Enter the system password: ");
        String inputPassword = scanner.nextLine();
        if (!inputPassword.equals(SYSTEM_PASSWORD)) {
            System.out.println("Access Denied: Incorrect password!");
            return;
        }
        System.out.println("Access Granted: Welcome to Mileage Calculator!");

        while (true) {
            try {
                System.out.println("\n=== Mileage Calculator Menu ===");
                System.out.println("1. Add Vehicle");
                System.out.println("2. View All Vehicles");
                System.out.println("3. Update Mileage");
                System.out.println("4. Delete Vehicle");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");

                int choice = scanner.nextInt();

                switch (choice) {
                    case 1 -> addVehicle(scanner);
                    case 2 -> retrieveFromDatabase();
                    case 3 -> updateMileage(scanner);
                    case 4 -> deleteVehicle(scanner);
                    case 5 -> {
                        System.out.println("Exiting...");
                        scanner.close();
                        return;
                    }
                    default -> System.out.println("Invalid choice! Please enter a number between 1 and 5.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the invalid input
            }
        }
    }

    private static void addVehicle(Scanner scanner) {
        System.out.println("\n=== Add Vehicle ===");
        System.out.print("Enter the type of vehicle (e.g., Car, Bike, Truck): ");
        scanner.nextLine(); // Consume newline
        String type = scanner.nextLine();

        System.out.print("Enter the engine capacity (in CC): ");
        int engineCC = scanner.nextInt();

        if (engineCC <= 0) {
            System.out.println("Error: Engine CC must be greater than zero.");
            return;
        }

        double mileage = calculateMileage(engineCC);
        System.out.printf("Calculated Mileage for %s with %d CC engine: %.2f km/l%n", type, engineCC, mileage);

        saveToDatabase(type, engineCC, mileage);
    }

    private static void retrieveFromDatabase() {
        String url = "jdbc:mysql://localhost:3306/mileage_calculator";
        String user = "root";
        String password = "14-Feb-05";

        String sql = "SELECT * FROM vehicles";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            System.out.println("\n=== Vehicle Data ===");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String type = resultSet.getString("type");
                int engineCC = resultSet.getInt("engine_cc");
                double mileage = resultSet.getDouble("mileage");

                System.out.printf("ID: %d, Type: %s, Engine CC: %d, Mileage: %.2f km/l%n", id, type, engineCC, mileage);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void updateMileage(Scanner scanner) {
        System.out.println("\n=== Update Mileage ===");
        System.out.print("Enter the vehicle ID to update: ");
        int id = scanner.nextInt();

        System.out.print("Enter the new mileage: ");
        double newMileage = scanner.nextDouble();

        String url = "jdbc:mysql://localhost:3306/mileage_calculator";
        String user = "root";
        String password = "14-Feb-05";

        String sql = "UPDATE vehicles SET mileage = ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDouble(1, newMileage);
            statement.setInt(2, id);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Mileage updated successfully!");
            } else {
                System.out.println("No record found with the specified ID.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void deleteVehicle(Scanner scanner) {
        System.out.println("\n=== Delete Vehicle ===");
        System.out.print("Enter the vehicle ID to delete: ");
        int id = scanner.nextInt();

        String url = "jdbc:mysql://localhost:3306/mileage_calculator";
        String user = "root";
        String password = "14-Feb-05";

        String sql = "DELETE FROM vehicles WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Vehicle deleted successfully!");
            } else {
                System.out.println("No record found with the specified ID.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static double calculateMileage(int engineCC) {
        return BASE_FACTOR / engineCC;
    }

    private static void saveToDatabase(String type, int engineCC, double mileage) {
        String url = "jdbc:mysql://localhost:3306/mileage_calculator";
        String user = "root";
        String password = "14-Feb-05";

        String sql = "INSERT INTO vehicles (type, engine_cc, mileage) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, type);
            statement.setInt(2, engineCC);
            statement.setDouble(3, mileage);
            statement.executeUpdate();

            System.out.println("Vehicle data saved to the database!");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}