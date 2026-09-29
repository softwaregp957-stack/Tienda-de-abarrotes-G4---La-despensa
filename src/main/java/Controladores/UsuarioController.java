package Controladores;

import Modelos.Usuario;
import Modelos.UsuarioDao;
import Modelos.UsuarioDaoImplt;
import Modelos.UsuarioRol;

import java.util.List;

/**
 * Controlador de la pantalla "Gestion de usuarios".
 * Contiene toda la logica de negocio: validaciones, conversion
 * boolean <-> String para el estado, y llamadas al DAO.
 * La vista (JFrame/JPanel) solo debe llamar a estos metodos,
 * nunca al DAO directamente.
 */
public class UsuarioController {

    private final UsuarioDao dao;

    public UsuarioController() {
        this.dao = new UsuarioDaoImplt();
    }

    // ==========================================================
    //  LISTAR / BUSCAR  -> devuelve filas listas para el JTable
    // ==========================================================

    /**
     * Devuelve todos los usuarios como matriz de Object para
     * cargar directamente en el DefaultTableModel del jTable1.
     * Columnas: ID | Nombre Completo | Correo Electronico | Rol | Estado
     */
    public Object[][] ListarParaTabla() {
        List<Usuario> lista = dao.ListarTodos();
        return ConvertirATablaFilas(lista);
    }

    /**
     * Busca aplicando los 3 filtros de la pantalla (texto libre, rol, estado).
     * Pasa null o "" en los que no apliquen. Para los combos, si el usuario
     * dejo seleccionado el item por defecto ("Item 1" / "Todos"), pasa null.
     */
    public Object[][] BuscarParaTabla(String texto, String rolFiltro, String estadoFiltro) {
        List<Usuario> lista = dao.BuscarPorFiltro(texto, rolFiltro, estadoFiltro);
        return ConvertirATablaFilas(lista);
    }

private Object[][] ConvertirATablaFilas(List<Usuario> lista) {
        // El orden debe coincidir con las columnas de la tabla en la vista:
        // "ID", "Nombre Completo", "Nombre Usuario", "Correo Electronico", "Telefono", "Rol", "Estado"
        Object[][] filas = new Object[lista.size()][7];

        for (int i = 0; i < lista.size(); i++) {
            Usuario u = lista.get(i);
            filas[i][0] = u.getId();
            filas[i][1] = u.getNombre();
            filas[i][2] = u.getUsername();
            filas[i][3] = u.getCorreo();
            filas[i][4] = u.getTelefono();
            filas[i][5] = (u.getRol() != null) ? u.getRol().name() : "";
            filas[i][6] = EstadoATexto(u.isEstado());
        }
        return filas;
    }

    // ==========================================================
    //  AGREGAR
    // ==========================================================

    /**
     * Valida y agrega un nuevo usuario.
     * @return mensaje de error, o null si se agrego correctamente.
     */
    public String Agregar(String username, String password, String rolTexto,
                           String nombre, String telefono, String correo, boolean estado) {

        String error = ValidarCampos(username, password, rolTexto, nombre, correo);
        if (error != null) {
            return error;
        }

        UsuarioRol rol = UsuarioRol.DesdeTexto(rolTexto);
        if (rol == null) {
            return "El rol ingresado no es valido. Usa: ADMIN o EMPLEADO.";
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username.trim());
        usuario.setPassword(password.trim());
        usuario.setRol(rol);
        usuario.setNombre(nombre.trim());
        usuario.setTelefono(telefono != null ? telefono.trim() : null);
        usuario.setCorreo(correo.trim());
        usuario.setEstado(estado);

        boolean ok = dao.Agregar(usuario);
        return ok ? null : "No se pudo agregar el usuario. Revisa la conexion a la BD.";
    }

    // ==========================================================
    //  ACTUALIZAR
    // ==========================================================

    /**
     * Valida y actualiza un usuario existente.
     * @param idTexto viene del campo ID (String) tal como esta en la tabla/formulario.
     * @return mensaje de error, o null si se actualizo correctamente.
     */
    public String Actualizar(String idTexto, String username, String password, String rolTexto,
                          String nombre, String telefono, String correo, boolean estado) {

    Integer id = ParsearId(idTexto);
    if (id == null) {
        return "Selecciona un usuario de la tabla antes de editar.";
    }

    Usuario actual = dao.BuscarPorId(id);
    if (actual == null) {
        return "No se encontro el usuario a editar.";
    }

    // Campos vacios conservan el valor actual de la BD
    String nombreFinal = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : actual.getNombre();
    String correoFinal = (correo != null && !correo.trim().isEmpty()) ? correo.trim() : actual.getCorreo();
    String usernameFinal = (username != null && !username.trim().isEmpty()) ? username.trim() : actual.getUsername();
    String passwordFinal = (password != null && !password.trim().isEmpty()) ? password.trim() : actual.getPassword();
    String telefonoFinal = (telefono != null && !telefono.trim().isEmpty()) ? telefono.trim() : actual.getTelefono();

    UsuarioRol rolFinal = actual.getRol();
    if (rolTexto != null && !rolTexto.trim().isEmpty()) {
        UsuarioRol rolNuevo = UsuarioRol.DesdeTexto(rolTexto);
        if (rolNuevo == null) {
            return "El rol ingresado no es valido. Usa: ADMIN o EMPLEADO.";
        }
        rolFinal = rolNuevo;
    }

    Usuario usuario = new Usuario();
    usuario.setId(id);
    usuario.setUsername(usernameFinal);
    usuario.setPassword(passwordFinal);
    usuario.setRol(rolFinal);
    usuario.setNombre(nombreFinal);
    usuario.setTelefono(telefonoFinal);
    usuario.setCorreo(correoFinal);
    usuario.setEstado(estado);

    boolean ok = dao.Actualizar(usuario);
    return ok ? null : "No se pudo actualizar el usuario. Revisa la conexion a la BD.";
}

    // ==========================================================
    //  ELIMINAR
    // ==========================================================

    /**
     * @param idTexto viene del campo ID (String).
     * @return mensaje de error, o null si se elimino correctamente.
     */
    public String Eliminar(String idTexto) {

        Integer id = ParsearId(idTexto);
        if (id == null) {
            return "Selecciona un usuario de la tabla antes de eliminar.";
        }

        boolean ok = dao.Eliminar(id);
        return ok ? null : "No se pudo eliminar el usuario. Revisa la conexion a la BD.";
    }

    // ==========================================================
    //  UTILIDADES (conversion Estado boolean <-> String)
    // ==========================================================

    public String EstadoATexto(boolean estado) {
        return estado ? "Activo" : "Inactivo";
    }

    public boolean TextoAEstado(String texto) {
        return "Activo".equalsIgnoreCase(texto != null ? texto.trim() : "");
    }

    private Integer ParsearId(String idTexto) {
        if (idTexto == null || idTexto.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(idTexto.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String ValidarCampos(String username, String password, String rolTexto,
                                  String nombre, String correo) {

        if (nombre == null || nombre.trim().isEmpty()) {
            return "El nombre completo es obligatorio.";
        }
        if (username == null || username.trim().isEmpty()) {
            return "El nombre de usuario es obligatorio.";
        }
        if (password == null || password.trim().isEmpty()) {
            return "La contrasena es obligatoria.";
        }
        if (rolTexto == null || rolTexto.trim().isEmpty()) {
            return "El rol es obligatorio.";
        }
        if (correo == null || correo.trim().isEmpty() || !correo.contains("@")) {
            return "El correo electronico no es valido.";
        }
        return null;
    }
    
    public Usuario BuscarPorId(String idTexto) {

    Integer id = ParsearId(idTexto);

    if (id == null) {
        return null;
    }

    return dao.BuscarPorId(id);
}
}