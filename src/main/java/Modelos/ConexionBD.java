package Modelos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ConexionBD {

    Connection con = null;

    public Connection establecerConexion() {

        try {
            Class.forName("org.sqlite.JDBC");

            con = DriverManager.getConnection(
                "jdbc:sqlite:C:/SQLite/BD_abarrotes.db"
            );

            // JOptionPane.showMessageDialog(null, "BD conectada!!");

        } catch (ClassNotFoundException | SQLException e) {

            JOptionPane.showMessageDialog(
                null,
                "Error: " + e.toString(),
                "Abarrotes",
                JOptionPane.ERROR_MESSAGE
            );
        }

        return con;
    }

    public void cerrarConexion() {

        try {

            if (con != null) {
                con.close();
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                null,
                "Error: " + e.toString(),
                "Abarrotes",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}