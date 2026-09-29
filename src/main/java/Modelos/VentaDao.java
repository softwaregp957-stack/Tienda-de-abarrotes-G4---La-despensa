package Modelos;

import java.sql.*;
import java.util.*;
import javax.swing.JOptionPane;

public class VentaDao {

    ConexionBD con = new ConexionBD();
    Connection cn;
    PreparedStatement ps;
    ResultSet rs;

    // Se reutiliza el DAO de productos ya existente para descontar/reponer stock
    ProductoDao productoDao = new ProductoDao();

    /** Lista resumida para la tabla principal de ventas (tblVentas): ID, fecha, cliente y total. */
    public List<Venta> listarVentas() {
        List<Venta> listaVentas = new ArrayList<>();
        String sql = "SELECT v.id_venta, v.fecha, v.total, v.fk_id_cliente, c.nombre_cliente "
                + "FROM venta v LEFT JOIN cliente c ON v.fk_id_cliente = c.id_cliente "
                + "ORDER BY v.id_venta DESC";
        try {
            cn = con.establecerConexion();
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Venta v = new Venta();
                v.setID_Venta(rs.getInt("id_venta"));
                v.setFecha(rs.getString("fecha"));
                v.setTotal(rs.getInt("total"));
                v.setFk_ID_Cliente(rs.getInt("fk_id_cliente"));
                v.setNombre_cliente(rs.getString("nombre_cliente"));
                listaVentas.add(v);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al listar ventas: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            con.cerrarConexion();
        }
        return listaVentas;
    }

    /** Detalle (productos, cantidad, precio unitario, subtotal) de una venta puntual. */
    public List<DetalleVenta> obtenerDetallePorVenta(int idVenta) {
        List<DetalleVenta> listaDetalle = new ArrayList<>();
        String sql = "SELECT d.id_detalle, d.cantidad, d.precio_unitario, d.subtotal, d.fk_id_venta, "
                + "d.fk_id_producto, p.nombre_producto "
                + "FROM detalle_venta d JOIN producto p ON d.fk_id_producto = p.id_producto "
                + "WHERE d.fk_id_venta = ?";
        try {
            cn = con.establecerConexion();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idVenta);
            rs = ps.executeQuery();
            while (rs.next()) {
                DetalleVenta d = new DetalleVenta();
                d.setID_Detalle(rs.getInt("id_detalle"));
                d.setCantidad(rs.getInt("cantidad"));
                d.setPrecio_Unitario(rs.getInt("precio_unitario"));
                d.setSubtotal(rs.getInt("subtotal"));
                d.setFk_ID_Venta(rs.getInt("fk_id_venta"));
                d.setFk_ID_Producto(rs.getInt("fk_id_producto"));
                d.setNombre_Producto(rs.getString("nombre_producto"));
                listaDetalle.add(d);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener el detalle de la venta: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            con.cerrarConexion();
        }
        return listaDetalle;
    }

    /**
     * Crea una venta nueva junto con todos sus detalles y descuenta el stock
     * vendido (reutilizando ProductoDao.actualizarInventario). Devuelve el
     * id_venta generado, o -1 si algo falló.
     */
    public int crearVenta(Venta venta, List<DetalleVenta> detalles) {
        int idVentaGenerado = -1;
        String sqlVenta = "INSERT INTO venta (fecha, total, fk_id_cliente) VALUES (datetime('now','localtime'), ?, ?)";
        String sqlDetalle = "INSERT INTO detalle_venta (cantidad, precio_unitario, subtotal, fk_id_venta, fk_id_producto) "
                + "VALUES (?, ?, ?, ?, ?)";
        try {
            cn = con.establecerConexion();

            ps = cn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, venta.getTotal());
            ps.setInt(2, venta.getFk_ID_Cliente());
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idVentaGenerado = rs.getInt(1);
            }

            if (idVentaGenerado != -1) {
                for (DetalleVenta d : detalles) {
                    ps = cn.prepareStatement(sqlDetalle);
                    ps.setInt(1, d.getCantidad());
                    ps.setInt(2, d.getPrecio_Unitario());
                    ps.setInt(3, d.getSubtotal());
                    ps.setInt(4, idVentaGenerado);
                    ps.setInt(5, d.getFk_ID_Producto());
                    ps.executeUpdate();

                    // Se descuenta el stock vendido usando el método que ya existía en ProductoDao
                    productoDao.actualizarInventario(d.getFk_ID_Producto(), d.getCantidad());
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al crear la venta: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            idVentaGenerado = -1;
        } finally {
            con.cerrarConexion();
        }
        return idVentaGenerado;
    }

    /**
     * Actualiza una venta existente: repone el stock de los detalles
     * antiguos, los reemplaza por los nuevos detalles y descuenta el stock
     * que corresponda a la nueva lista de productos.
     */
    public boolean actualizarVenta(int IDVenta, Venta venta, List<DetalleVenta> nuevosDetalles) {
        String sqlObtenerActuales = "SELECT fk_id_producto, cantidad FROM detalle_venta WHERE fk_id_venta = ?";
        String sqlBorrarDetalles = "DELETE FROM detalle_venta WHERE fk_id_venta = ?";
        String sqlDetalle = "INSERT INTO detalle_venta (cantidad, precio_unitario, subtotal, fk_id_venta, fk_id_producto) "
                + "VALUES (?, ?, ?, ?, ?)";
        String sqlVenta = "UPDATE venta SET total = ?, fk_id_cliente = ? WHERE id_venta = ?";
        try {
            cn = con.establecerConexion();

            // 1. Reponer el stock de los detalles actuales antes de reemplazarlos
            ps = cn.prepareStatement(sqlObtenerActuales);
            ps.setInt(1, IDVenta);
            rs = ps.executeQuery();
            List<int[]> detallesActuales = new ArrayList<>(); // {fk_id_producto, cantidad}
            while (rs.next()) {
                detallesActuales.add(new int[]{rs.getInt("fk_id_producto"), rs.getInt("cantidad")});
            }
            for (int[] d : detallesActuales) {
                productoDao.actualizarInventario(d[0], -d[1]); // cantidad negativa = repone stock
            }

            // 2. Borrar los detalles antiguos
            ps = cn.prepareStatement(sqlBorrarDetalles);
            ps.setInt(1, IDVenta);
            ps.executeUpdate();

            // 3. Insertar los nuevos detalles y descontar su stock correspondiente
            for (DetalleVenta d : nuevosDetalles) {
                ps = cn.prepareStatement(sqlDetalle);
                ps.setInt(1, d.getCantidad());
                ps.setInt(2, d.getPrecio_Unitario());
                ps.setInt(3, d.getSubtotal());
                ps.setInt(4, IDVenta);
                ps.setInt(5, d.getFk_ID_Producto());
                ps.executeUpdate();

                productoDao.actualizarInventario(d.getFk_ID_Producto(), d.getCantidad());
            }

            // 4. Actualizar la cabecera de la venta
            ps = cn.prepareStatement(sqlVenta);
            ps.setInt(1, venta.getTotal());
            ps.setInt(2, venta.getFk_ID_Cliente());
            ps.setInt(3, IDVenta);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar la venta: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            con.cerrarConexion();
        }
    }

    /** Elimina una venta, repone el stock vendido y borra sus detalles. */
    public boolean eliminarVenta(int IDVenta) {
        String sqlObtenerActuales = "SELECT fk_id_producto, cantidad FROM detalle_venta WHERE fk_id_venta = ?";
        String sqlBorrarDetalles = "DELETE FROM detalle_venta WHERE fk_id_venta = ?";
        String sqlBorrarVenta = "DELETE FROM venta WHERE id_venta = ?";
        try {
            cn = con.establecerConexion();

            ps = cn.prepareStatement(sqlObtenerActuales);
            ps.setInt(1, IDVenta);
            rs = ps.executeQuery();
            List<int[]> detallesActuales = new ArrayList<>();
            while (rs.next()) {
                detallesActuales.add(new int[]{rs.getInt("fk_id_producto"), rs.getInt("cantidad")});
            }
            for (int[] d : detallesActuales) {
                productoDao.actualizarInventario(d[0], -d[1]); // repone el stock vendido
            }

            ps = cn.prepareStatement(sqlBorrarDetalles);
            ps.setInt(1, IDVenta);
            ps.executeUpdate();

            ps = cn.prepareStatement(sqlBorrarVenta);
            ps.setInt(1, IDVenta);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar la venta: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            con.cerrarConexion();
        }
    }
}