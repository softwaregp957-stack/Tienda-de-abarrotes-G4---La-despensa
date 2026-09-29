package Modelos;

import Modelos.UsuarioRol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDaoImplt implements UsuarioDao {

    @Override
    public Usuario ValidarCredenciales(String Username, String Password) {

        String sql = "SELECT id_usuario, nombre, nombre_usuario, password, rol, "
                   + "telefono, correo, estado_usuario "
                   + "FROM usuario WHERE nombre_usuario = ?";

        ConexionBD conexion = new ConexionBD();

        try {
            Connection con = conexion.establecerConexion();

            if (con == null) {
                return null;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, Username);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {

                        String PasswordGuardada = rs.getString("password");

                        if (PasswordGuardada.equals(Password)) {

                            boolean Estado = rs.getBoolean("estado_usuario");

                            if (!Estado) {
                                return null;
                            }

                            return MapearUsuario(rs);
                        }
                    }
                }

            }

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            conexion.cerrarConexion();
        }

        return null;
    }

    @Override
    public Usuario BuscarPorUsername(String Username) {

        String sql = "SELECT id_usuario, nombre, nombre_usuario, password, rol, "
                   + "telefono, correo, estado_usuario "
                   + "FROM usuario WHERE nombre_usuario = ?";

        ConexionBD conexion = new ConexionBD();

        try {
            Connection con = conexion.establecerConexion();

            if (con == null) {
                return null;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, Username);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        return MapearUsuario(rs);
                    }
                }

            }

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            conexion.cerrarConexion();
        }

        return null;
    }

    @Override
    public List<Usuario> ListarTodos() {

        List<Usuario> lista = new ArrayList<>();

        String sql = "SELECT id_usuario, nombre, nombre_usuario, password, rol, "
                   + "telefono, correo, estado_usuario FROM usuario ORDER BY id_usuario";

        ConexionBD conexion = new ConexionBD();

        try {
            Connection con = conexion.establecerConexion();
            if (con == null) return lista;

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(MapearUsuario(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conexion.cerrarConexion();
        }

        return lista;
    }

    @Override
    public List<Usuario> BuscarPorFiltro(String texto, String rol, String estado) {

        List<Usuario> listaUsuarios = new ArrayList<>();

        String sql = "SELECT id_usuario, nombre, nombre_usuario, password, rol, "
                   + "telefono, correo, estado_usuario "
                   + "FROM usuario WHERE 1=1 ";

        boolean tieneTexto = texto != null && !texto.trim().isEmpty();
        boolean tieneRol = rol != null
                && !rol.trim().isEmpty()
                && !rol.equalsIgnoreCase("Todos");

        boolean tieneEstado = estado != null
                && !estado.trim().isEmpty()
                && !estado.equalsIgnoreCase("Todos");

        // Buscar por nombre o nombre de usuario
        if (tieneTexto) {
            sql += "AND (nombre LIKE ? OR nombre_usuario LIKE ?) ";
        }

        // Filtrar por rol
        if (tieneRol) {
            sql += "AND rol = ? ";
        }

        // Filtrar por estado
        if (tieneEstado) {
            sql += "AND estado_usuario = ? ";
        }

        sql += "ORDER BY id_usuario ASC";

        ConexionBD conexion = new ConexionBD();

        try {
            Connection cn = conexion.establecerConexion();

            if (cn == null) {
                return listaUsuarios;
            }

            try (PreparedStatement ps = cn.prepareStatement(sql)) {

                int posicion = 1;

                // Texto: nombre o nombre_usuario
                if (tieneTexto) {

                    String textoBusqueda = "%" + texto.trim() + "%";

                    ps.setString(posicion++, textoBusqueda);
                    ps.setString(posicion++, textoBusqueda);
                }

                // Rol
                if (tieneRol) {
                    ps.setString(posicion++, rol.trim().toUpperCase());
                }

                // Estado
                if (tieneEstado) {

                    boolean estadoBool =
                            estado.trim().equalsIgnoreCase("Activo");

                    ps.setBoolean(posicion++, estadoBool);
                }

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {

                        Usuario u = new Usuario();

                        u.setId(rs.getInt("id_usuario"));
                        u.setNombre(rs.getString("nombre"));
                        u.setUsername(rs.getString("nombre_usuario"));
                        u.setPassword(rs.getString("password"));
                        u.setRol(
                            UsuarioRol.DesdeTexto(
                                rs.getString("rol")
                            )
                        );
                        u.setTelefono(rs.getString("telefono"));
                        u.setCorreo(rs.getString("correo"));
                        u.setEstado(rs.getBoolean("estado_usuario"));

                        listaUsuarios.add(u);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conexion.cerrarConexion();
        }

        return listaUsuarios;
    }
    @Override
    public boolean Agregar(Usuario usuario) {

        String sql = "INSERT INTO usuario (nombre, nombre_usuario, password, rol, "
                   + "telefono, correo, estado_usuario) VALUES (?, ?, ?, ?, ?, ?, ?)";

        ConexionBD conexion = new ConexionBD();

        try {
            Connection con = conexion.establecerConexion();
            if (con == null) return false;

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, usuario.getNombre());
                ps.setString(2, usuario.getUsername());
                ps.setString(3, usuario.getPassword());
                ps.setString(4, usuario.getRol() != null ? usuario.getRol().name() : null);
                ps.setString(5, usuario.getTelefono());
                ps.setString(6, usuario.getCorreo());
                ps.setBoolean(7, usuario.isEstado());

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            conexion.cerrarConexion();
        }
    }

    @Override
    public boolean Actualizar(Usuario usuario) {

        String sql = "UPDATE usuario SET nombre = ?, nombre_usuario = ?, password = ?, "
                   + "rol = ?, telefono = ?, correo = ?, estado_usuario = ? "
                   + "WHERE id_usuario = ?";

        ConexionBD conexion = new ConexionBD();

        try {
            Connection con = conexion.establecerConexion();
            if (con == null) return false;

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, usuario.getNombre());
                ps.setString(2, usuario.getUsername());
                ps.setString(3, usuario.getPassword());
                ps.setString(4, usuario.getRol() != null ? usuario.getRol().name() : null);
                ps.setString(5, usuario.getTelefono());
                ps.setString(6, usuario.getCorreo());
                ps.setBoolean(7, usuario.isEstado());
                ps.setInt(8, usuario.getId());

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            conexion.cerrarConexion();
        }
    }

    @Override
    public boolean Eliminar(int id) {

        String sql = "DELETE FROM usuario WHERE id_usuario = ?";

        ConexionBD conexion = new ConexionBD();

        try {
            Connection con = conexion.establecerConexion();
            if (con == null) return false;

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            conexion.cerrarConexion();
        }
    }
    
    @Override
public Usuario BuscarPorId(int id) {

    String sql = "SELECT id_usuario, nombre, nombre_usuario, password, rol, "
               + "telefono, correo, estado_usuario FROM usuario WHERE id_usuario = ?";

    ConexionBD conexion = new ConexionBD();

    try {
        Connection con = conexion.establecerConexion();
        if (con == null) return null;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return MapearUsuario(rs);
                }
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        conexion.cerrarConexion();
    }

    return null;
}

    private Usuario MapearUsuario(ResultSet rs) throws SQLException {

        Usuario usuario = new Usuario();

        usuario.setId(rs.getInt("id_usuario"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setUsername(rs.getString("nombre_usuario"));
        usuario.setPassword(rs.getString("password"));
        usuario.setRol(UsuarioRol.DesdeTexto(rs.getString("rol")));
        usuario.setTelefono(rs.getString("telefono"));
        usuario.setCorreo(rs.getString("correo"));
        usuario.setEstado(rs.getBoolean("estado_usuario"));

        return usuario;
    }
}