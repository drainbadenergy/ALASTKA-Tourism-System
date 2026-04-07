package main;

import db.DBConnection;
import model.InvalidUserException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class MainApp {

    // It's generally better to pass connections, but a static variable is okay for a mini-project.
    private static Connection con;

    public static void main(String[] args) {
        // 1. Initialize Database Connection First
        con = DBConnection.getConnection();

        if (con == null) {
            JOptionPane.showMessageDialog(null, "Database Connection Failed!", "Connection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Launch GUI on the Event Dispatch Thread (Swing Best Practice)
        SwingUtilities.invokeLater(MainApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("🌍 ALASTKA Tourism System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 350);
        frame.setLocationRelativeTo(null); // Centers the window on the screen

        // Use a JPanel with an EmptyBorder to add padding around the buttons
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 1, 15, 15)); // 4 rows, 1 col, with 15px gaps
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50)); // Top, Left, Bottom, Right padding

        JButton viewBtn = new JButton("View Countries");
        JButton insertBtn = new JButton("Insert New User");
        JButton updateBtn = new JButton("Update User Name");
        JButton deleteBtn = new JButton("Delete User");

        // Styling the buttons slightly
        Font btnFont = new Font("Arial", Font.BOLD, 14);
        viewBtn.setFont(btnFont);
        insertBtn.setFont(btnFont);
        updateBtn.setFont(btnFont);
        deleteBtn.setFont(btnFont);

        mainPanel.add(viewBtn);
        mainPanel.add(insertBtn);
        mainPanel.add(updateBtn);
        mainPanel.add(deleteBtn);

        frame.add(mainPanel);

        // --- ACTION LISTENERS ---

        // VIEW COUNTRIES
        viewBtn.addActionListener(e -> {
            // Using try-with-resources to automatically close Statement and ResultSet
            String query = "SELECT CountryID, CountryName FROM Country";
            try (Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                StringBuilder result = new StringBuilder("Countries in ALASTKA:\n--------------------\n");
                boolean hasData = false;

                while (rs.next()) {
                    hasData = true;
                    result.append(rs.getInt("CountryID"))
                            .append(" - ")
                            .append(rs.getString("CountryName"))
                            .append("\n");
                }

                if (!hasData) result.append("No countries found.");

                JOptionPane.showMessageDialog(frame, result.toString(), "Country List", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException ex) {
                showError(frame, "Error fetching countries: " + ex.getMessage());
            }
        });

        // INSERT USER (Improved with a single dialog panel)
        insertBtn.addActionListener(e -> {
            JTextField nameField = new JTextField();
            JTextField emailField = new JTextField();
            JPasswordField passwordField = new JPasswordField();

            Object[] inputFields = {
                    "Name:", nameField,
                    "Email:", emailField,
                    "Password:", passwordField
            };

            int option = JOptionPane.showConfirmDialog(frame, inputFields, "Register New User", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                try {
                    String name = nameField.getText().trim();
                    String email = emailField.getText().trim();
                    String password = new String(passwordField.getPassword());

                    if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                        throw new InvalidUserException("All fields must be filled!");
                    }

                    String query = "INSERT INTO Users(Name, Email, Password) VALUES (?, ?, ?)";
                    try (PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                        ps.setString(1, name);
                        ps.setString(2, email);
                        ps.setString(3, password);

                        ps.executeUpdate();

                        try (ResultSet rs = ps.getGeneratedKeys()) {
                            if (rs.next()) {
                                JOptionPane.showMessageDialog(frame, "User Inserted Successfully! ID = " + rs.getInt(1), "Success", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
                } catch (InvalidUserException ex) {
                    showError(frame, ex.getMessage());
                } catch (SQLException ex) {
                    showError(frame, "Database Error: " + ex.getMessage());
                }
            }
        });

        // UPDATE USER
        updateBtn.addActionListener(e -> {
            try {
                String idStr = JOptionPane.showInputDialog(frame, "Enter UserID to update:");
                if (idStr == null || idStr.trim().isEmpty()) return; // User clicked cancel or left blank
                int id = Integer.parseInt(idStr);

                String name = JOptionPane.showInputDialog(frame, "Enter New Name for User " + id + ":");
                if (name == null || name.trim().isEmpty()) return;

                String query = "UPDATE Users SET Name=? WHERE UserID=?";
                try (PreparedStatement ps = con.prepareStatement(query)) {
                    ps.setString(1, name.trim());
                    ps.setInt(2, id);

                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(frame, "User Updated Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        showError(frame, "User ID not found!");
                    }
                }
            } catch (NumberFormatException ex) {
                showError(frame, "Invalid ID format! Please enter a number.");
            } catch (SQLException ex) {
                showError(frame, "Database Error: " + ex.getMessage());
            }
        });

        // DELETE USER
        deleteBtn.addActionListener(e -> {
            try {
                String idStr = JOptionPane.showInputDialog(frame, "Enter UserID to Delete:");
                if (idStr == null || idStr.trim().isEmpty()) return;
                int id = Integer.parseInt(idStr);

                // Add a confirmation dialog before deleting
                int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete User " + id + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;

                String query = "DELETE FROM Users WHERE UserID=?";
                try (PreparedStatement ps = con.prepareStatement(query)) {
                    ps.setInt(1, id);

                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(frame, "User Deleted Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        showError(frame, "User ID not found!");
                    }
                }
            } catch (NumberFormatException ex) {
                showError(frame, "Invalid ID format! Please enter a number.");
            } catch (SQLException ex) {
                showError(frame, "Database Error: " + ex.getMessage());
            }
        });

        // Make the window visible after everything is set up
        frame.setVisible(true);
    }

    // Helper method for standardizing error messages
    private static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}