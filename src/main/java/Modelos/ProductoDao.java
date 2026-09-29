package Modelos;

import java.sql.*;
import java.util.*;
import javax.swing.JOptionPane;

public class ProductoDao {
    
    ConexionBD con = new ConexionBD();
    Connection cn;
    PreparedStatement ps;
    ResultSet rs;
    
    public List<Producto> listarTodos() {
        List<Producto> listaProductos = new ArrayList<>();
        String sql = "SELECT * FROM producto ORDER BY id_producto ASC";
        try {
            cn = con.establecerConexion();
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Producto p = new Producto();
                p.setId_producto(rs.getInt("id_producto"));
                p.setNombre_producto(rs.getString("nombre_producto"));
                p.setPrecio(rs.getInt("precio"));
                p.setStock(rs.getInt("stock"));
                p.setCategoria(rs.getString("categoria"));
                p.setEstado_producto(rs.getBoolean("estado_producto"));
                listaProductos.add(p);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al listar productos: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            con.cerrarConexion();
        }
        return listaProductos;
    }
    
    public List<Producto> listarDisponibles() {
        List<Producto> listaProductos = new ArrayList<>();
        String sql = "SELECT * FROM producto WHERE stock > 0 AND estado_producto = 1 ORDER BY id_producto";
        try {
            cn = con.establecerConexion();
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Producto p = new Producto();
                p.setId_producto(rs.getInt("id_producto")); // Se agregó la ID que faltaba
                p.setNombre_producto(rs.getString("nombre_producto"));
                p.setPrecio(rs.getInt("precio"));
                p.setStock(rs.getInt("stock"));
                p.setCategoria(rs.getString("categoria"));
                p.setEstado_producto(rs.getBoolean("estado_producto"));
                listaProductos.add(p);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al listar disponibles: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            con.cerrarConexion();
        }
        return listaProductos;
    }
    
    public int insertarProductos(Producto p) {
        // No enviamos id_producto porque la BD lo autogenera
        String sql = "INSERT INTO producto(nombre_producto, precio, stock, categoria, estado_producto) VALUES(?, ?, ?, ?, ?)";
        int idGenerado = -1;

        try {
            cn = con.establecerConexion();
            // Le indicamos a JDBC que devuelva las llaves autogeneradas
            ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, p.getNombre_producto());
            ps.setInt(2, p.getPrecio());
            ps.setInt(3, p.getStock());
            ps.setString(4, p.getCategoria());
            ps.setBoolean(5, p.isEstado_producto());

            ps.executeUpdate();

            // Obtenemos el ID asignado por la BD
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al insertar producto: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            con.cerrarConexion();
        }
        return idGenerado; // Devuelve el nuevo ID
    }
    
    public boolean actualizarInventario(int id, int cantidad) {
        String sql = "UPDATE producto SET stock = stock - ? WHERE id_producto = ?";
        try {
            cn = con.establecerConexion();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, cantidad);
            ps.setInt(2, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar stock: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            con.cerrarConexion();
        }
    }
    
    // Método para ACTUALIZAR un producto existente en la BD
    public boolean actualizarProducto(Producto p) {
        String sql = "UPDATE producto SET nombre_producto = ?, precio = ?, stock = ?, categoria = ?, estado_producto = ? WHERE id_producto = ?";
        try {
            cn = con.establecerConexion();
            ps = cn.prepareStatement(sql);
            ps.setString(1, p.getNombre_producto());
            ps.setInt(2, p.getPrecio());
            ps.setInt(3, p.getStock());
            ps.setString(4, p.getCategoria());
            ps.setBoolean(5, p.isEstado_producto());
            ps.setInt(6, p.getId_producto());
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar producto: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            con.cerrarConexion();
        }
    }

    // Método para ELIMINAR un producto por su ID
    public boolean eliminarProducto(int id) {
        String sql = "DELETE FROM producto WHERE id_producto = ?";
        try {
            cn = con.establecerConexion();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, id);
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar producto: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            con.cerrarConexion();
        }
    }
    
    // Busca productos por nombre. Si el parámetro está vacío o es null, devuelve todos.
public List<Producto> buscarProductosPorNombre(String texto) {
    List<Producto> listaProductos = new ArrayList<>();
    
    if (texto == null || texto.trim().isEmpty()) {
        return listarTodos(); // Si está vacío, devuelve la lista completa
    }

    String sql = "SELECT * FROM producto WHERE nombre_producto LIKE ? ORDER BY id_producto ASC";
    
    try {
        cn = con.establecerConexion();
        ps = cn.prepareStatement(sql);
        // El operador % permite buscar cualquier texto que contenga la palabra ingresada
        ps.setString(1, "%" + texto.trim() + "%");
        rs = ps.executeQuery();

        while (rs.next()) {
            Producto p = new Producto();
            p.setId_producto(rs.getInt("id_producto"));
            p.setNombre_producto(rs.getString("nombre_producto"));
            p.setPrecio(rs.getInt("precio"));
            p.setStock(rs.getInt("stock"));
            p.setCategoria(rs.getString("categoria"));
            p.setEstado_producto(rs.getBoolean("estado_producto"));
            listaProductos.add(p);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error en la búsqueda: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
    } finally {
        con.cerrarConexion();
    }
    return listaProductos;
}
}

