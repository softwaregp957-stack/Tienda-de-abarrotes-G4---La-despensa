package Modelos;

import java.util.List;

public interface UsuarioDao {

    Usuario ValidarCredenciales(String Username, String Password);
    Usuario BuscarPorUsername(String Username);

    List<Usuario> ListarTodos();
    List<Usuario> BuscarPorFiltro(String texto, String rol, String estado);

    boolean Agregar(Usuario usuario);
    boolean Actualizar(Usuario usuario);
    boolean Eliminar(int id);
    Usuario BuscarPorId(int id);
}