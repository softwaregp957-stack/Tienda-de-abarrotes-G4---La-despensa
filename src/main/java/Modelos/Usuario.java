package Modelos;

import Modelos.UsuarioRol;

public class Usuario {
    private int Id;
    private String Username;
    private String Password;
    private UsuarioRol Rol;
    private String Nombre;
    private  String Telefono;
    private String Correo;
    private boolean Estado;
    
    // Consatructores //
    
    public Usuario(){
        
    }
    
    public Usuario(int Id, String Username, String Password, UsuarioRol Rol, String Nombre, String Telefono, String Correo, boolean Estado) {
        this.Id = Id;
        this.Username = Username;
        this.Password = Password;
        this.Rol = Rol;
        this.Nombre = Nombre;
        this.Telefono = Telefono;
        this.Correo = Correo;
        this.Estado = Estado;
    }
 
    public Usuario(String Username, String Password) {
        this.Username = Username;
        this.Password = Password;
    }
    
    // Getters y Setters //
    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public UsuarioRol getRol() {
        return Rol;
    }

    public void setRol(UsuarioRol Rol) {
        this.Rol = Rol;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String Telefono) {
        this.Telefono = Telefono;
    }

    public String getCorreo() {
        return Correo;
    }

    public void setCorreo(String Correo) {
        this.Correo = Correo;
    }

    public boolean isEstado() {
        return Estado;
    }

    public void setEstado(boolean Estado) {
        this.Estado = Estado;
    }
    
}
