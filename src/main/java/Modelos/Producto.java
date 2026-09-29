package Modelos;

public class Producto {
    
    private int id_producto;
    private String nombre_producto;
    private int precio;
    private int stock;
    private String categoria;
    private boolean estado_producto;
    
    public Producto() {
    }

    public Producto(int id_producto, String nombre_producto, int precio, int stock, String categoria, boolean estado_producto) {
        this.id_producto = id_producto;
        this.nombre_producto = nombre_producto;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
        this.estado_producto = estado_producto;
    }

    public int getId_producto() {
        return id_producto;
    }

    public String getNombre_producto() {
        return nombre_producto;
    }

    public int getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    public String getCategoria() {
        return categoria;
    }

    public boolean isEstado_producto() {
        return estado_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public void setNombre_producto(String nombre_producto) {
        this.nombre_producto = nombre_producto;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setEstado_producto(boolean estado_producto) {
        this.estado_producto = estado_producto;
    }
        
    @Override
    public String toString() {
        return id_producto + " - " + nombre_producto;
    }
}