package main;

import db.DBConnection;
import model.User;
import java.sql.*;

public class MainApp {

    public static void main(String[] args) {

        Connection con = DBConnection.getConnection();

        if (con == null) {
            System.out.println("Connection failed. Exiting...");
            return;
        }

        System.out.println("Connection successful!");

        // ================== SELECT ==================
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Country")) {

            System.out.println("\n=== Countries ===");
            while (rs.next()) {
                System.out.println(rs.getInt("CountryID") + " - " + rs.getString("CountryName"));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching countries: " + e.getMessage());
        }

        // ================== INSERT ==================
        int newUserId = 0;

        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO Users(Name, Email, Password) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, "TestUser");
            ps.setString(2, "test@gmail.com");
            ps.setString(3, "pass123");
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) newUserId = rs.getInt(1);
            }
            System.out.println("\nUser inserted! ID = " + newUserId);

        } catch (SQLException e) {
            System.out.println("Insert failed: " + e.getMessage());
        }

        // Guard: skip UPDATE/DELETE if insert failed
        if (newUserId == 0) {
            System.out.println("Insert failed, skipping UPDATE and DELETE.");
        } else {

            // ================== UPDATE ==================
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE Users SET Name=? WHERE UserID=?")) {

                ps.setString(1, "UpdatedUser");
                ps.setInt(2, newUserId);

                if (ps.executeUpdate() > 0)
                    System.out.println("User updated successfully!");

            } catch (SQLException e) {
                System.out.println("Update failed: " + e.getMessage());
            }

            // ================== DELETE ==================
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM Users WHERE UserID=?")) {

                ps.setInt(1, newUserId);

                if (ps.executeUpdate() > 0)
                    System.out.println("User deleted successfully!");

            } catch (SQLException e) {
                System.out.println("Delete failed: " + e.getMessage());
            }
        }

        // ================== CLOSE ==================
        try {
            con.close();
            System.out.println("\nConnection closed.");
        } catch (SQLException e) {
            System.out.println("Error closing connection.");
        }

        User u = new User("Ayush");
        u.showDetails();
    }
}