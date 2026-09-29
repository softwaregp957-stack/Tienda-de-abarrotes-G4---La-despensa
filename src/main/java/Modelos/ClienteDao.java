package Modelos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ClienteDao {

    ConexionBD con = new ConexionBD();

    Connection cn;
    PreparedStatement ps;
    ResultSet rs;

    public List<Cliente> listarTodos() {
        List<Cliente> listaClientes = new ArrayList<>();
        String sql = "SELECT * FROM cliente ORDER BY id_cliente ASC";
        try {
            cn = con.establecerConexion();
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Cliente c = new Cliente(rs.getInt("id_cliente"), rs.getString("nombre_cliente"), rs.getString("telefono_cliente"));
                listaClientes.add(c);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al listar clientes: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            con.cerrarConexion();
        }
        return listaClientes;
    }

    public int insertarCliente(Cliente c) {
        String sql = "INSERT INTO cliente(nombre_cliente, telefono_cliente) VALUES (?, ?)";
        int idGenerado = -1;
        try {
            cn = con.establecerConexion();
            ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, c.getNombre_cliente());
            ps.setString(2, c.getTelefono_cliente());
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al insertar cliente: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            con.cerrarConexion();
        }
        return idGenerado;
    }

    public boolean actualizarCliente(Cliente c) {
        String sql = "UPDATE cliente SET nombre_cliente = ?, telefono_cliente = ? WHERE id_cliente = ?";
        try {
            cn = con.establecerConexion();
            ps = cn.prepareStatement(sql);
            ps.setString(1, c.getNombre_cliente());
            ps.setString(2, c.getTelefono_cliente());
            ps.setInt(3, c.getId_cliente());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar cliente: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            con.cerrarConexion();
        }
    }

    public boolean eliminarCliente(int id) {
        String sql = "DELETE FROM cliente WHERE id_cliente = ?";
        try {
            cn = con.establecerConexion();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, id);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar cliente: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            con.cerrarConexion();
        }
    }

    public List<Cliente> buscarClientesPorNombre(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return listarTodos();
        }

        List<Cliente> listaClientes = new ArrayList<>();
        String sql = "SELECT * FROM cliente WHERE nombre_cliente LIKE ? ORDER BY id_cliente ASC";
        try {
            cn = con.establecerConexion();
            ps = cn.prepareStatement(sql);
            ps.setString(1, "%" + texto.trim() + "%");
            rs = ps.executeQuery();

            while (rs.next()) {
                Cliente c = new Cliente(rs.getInt("id_cliente"), rs.getString("nombre_cliente"), rs.getString("telefono_cliente"));
                listaClientes.add(c);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en la búsqueda: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            con.cerrarConexion();
        }
        return listaClientes;
    }
    
    
}
