package Controladores;

import Modelos.UsuarioDao;
import Modelos.UsuarioDaoImplt;
import Modelos.Usuario;
import Modelos.Usuario;
import Modelos.UsuarioDao;
import Modelos.UsuarioDaoImplt;

public class ControladorLogin {

    private final UsuarioDao UsuarioDao;

    public ControladorLogin() {
        this.UsuarioDao = new UsuarioDaoImplt();
    }

    /**
     * Resultado de un intento de login, para que la Vista sepa qué mostrar
     * sin tener que adivinar a partir de un booleano o un null.
     */
    public static class ResultadoLogin {
        public final boolean Exitoso;
        public final String Mensaje;
        public final Usuario Usuario; // null si no fue exitoso

        public ResultadoLogin(boolean Exitoso, String Mensaje, Usuario Usuario) {
            this.Exitoso = Exitoso;
            this.Mensaje = Mensaje;
            this.Usuario = Usuario;
        }
    }

    /**
     * Punto de entrada que la Vista debe llamar cuando el usuario presiona "Iniciar Sesión".
     * @param username
     * @param password
     * @return 
     */
    public ResultadoLogin Autenticar(String username, String password) {

        // 1. Validaciones básicas de forma (antes de tocar la BD)
        if (username.trim() == null || username.trim().isEmpty()) {
            return new ResultadoLogin(false, "El usuario no puede estar vacío.", null);
        }
        if (password == null || password.isEmpty()) {
            return new ResultadoLogin(false, "La contraseña no puede estar vacía.", null);
        }

        // 2. Validación real contra la base de datos (vía DAO)
        Usuario Usuario = UsuarioDao.ValidarCredenciales(username.trim(), password);

        if (Usuario == null) {
            return new ResultadoLogin(false, "Usuario o contraseña incorrectos.", null);
        }

        // 3. Aquí podrías agregar reglas extra: usuario inactivo, rol no permitido, etc.

        return new ResultadoLogin(true, "Bienvenido, " + Usuario.getUsername() + ".", Usuario);
    }
}