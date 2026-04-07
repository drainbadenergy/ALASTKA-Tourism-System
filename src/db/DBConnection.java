package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection() {
        Connection con = null;

        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/ALASTKA",
                    "harman",
                    "Helsinki@6024"
            );

            System.out.println("Connected to database!");

        } catch (SQLException e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }

        return con;
    }
}