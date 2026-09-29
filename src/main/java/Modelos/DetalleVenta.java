package Modelos;
 
public class DetalleVenta {
 
    private int ID_Detalle;
    private int Cantidad;
    private int Precio_Unitario;
    private int Subtotal;
    private int Fk_ID_Venta;
    private int Fk_ID_Producto;
    private String Nombre_Producto; 
 
    public DetalleVenta() {
    }

    public DetalleVenta(int ID_Detalle, int Cantidad, int Precio_Unitario, int Subtotal, int Fk_ID_Venta, int Fk_ID_Producto, String Nombre_Producto) {
        this.ID_Detalle = ID_Detalle;
        this.Cantidad = Cantidad;
        this.Precio_Unitario = Precio_Unitario;
        this.Subtotal = Subtotal;
        this.Fk_ID_Venta = Fk_ID_Venta;
        this.Fk_ID_Producto = Fk_ID_Producto;
        this.Nombre_Producto = Nombre_Producto;
    }

    public int getID_Detalle() {
        return ID_Detalle;
    }

    public void setID_Detalle(int ID_Detalle) {
        this.ID_Detalle = ID_Detalle;
    }

    public int getCantidad() {
        return Cantidad;
    }

    public void setCantidad(int Cantidad) {
        this.Cantidad = Cantidad;
    }

    public int getPrecio_Unitario() {
        return Precio_Unitario;
    }

    public void setPrecio_Unitario(int Precio_Unitario) {
        this.Precio_Unitario = Precio_Unitario;
    }

    public int getSubtotal() {
        return Subtotal;
    }

    public void setSubtotal(int Subtotal) {
        this.Subtotal = Subtotal;
    }

    public int getFk_ID_Venta() {
        return Fk_ID_Venta;
    }

    public void setFk_ID_Venta(int Fk_ID_Venta) {
        this.Fk_ID_Venta = Fk_ID_Venta;
    }

    public int getFk_ID_Producto() {
        return Fk_ID_Producto;
    }

    public void setFk_ID_Producto(int Fk_ID_Producto) {
        this.Fk_ID_Producto = Fk_ID_Producto;
    }

    public String getNombre_Producto() {
        return Nombre_Producto;
    }

    public void setNombre_Producto(String Nombre_Producto) {
        this.Nombre_Producto = Nombre_Producto;
    }
 
 
    /** Recalcula el subtotal a partir de la cantidad y el precio unitario actuales. */
    public void calcularSubtotal() {
        this.Subtotal = this.Cantidad * this.Precio_Unitario;
    }
}