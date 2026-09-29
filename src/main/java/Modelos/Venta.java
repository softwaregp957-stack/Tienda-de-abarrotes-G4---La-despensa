package Modelos;

public class Venta {

    private int ID_Venta;
    private String Fecha;
    private int Total;
    private int Fk_ID_Cliente;
    private String Nombre_cliente; 

    public Venta() {
    }

    public Venta(int ID_Venta, String Fecha, int Total, int Fk_ID_Cliente, String Nombre_cliente) {
        this.ID_Venta = ID_Venta;
        this.Fecha = Fecha;
        this.Total = Total;
        this.Fk_ID_Cliente = Fk_ID_Cliente;
        this.Nombre_cliente = Nombre_cliente;
    }

    public int getID_Venta() {
        return ID_Venta;
    }

    public void setID_Venta(int ID_Venta) {
        this.ID_Venta = ID_Venta;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String Fecha) {
        this.Fecha = Fecha;
    }

    public int getTotal() {
        return Total;
    }

    public void setTotal(int Total) {
        this.Total = Total;
    }

    public int getFk_ID_Cliente() {
        return Fk_ID_Cliente;
    }

    public void setFk_ID_Cliente(int Fk_ID_Cliente) {
        this.Fk_ID_Cliente = Fk_ID_Cliente;
    }

    public String getNombre_cliente() {
        return Nombre_cliente;
    }

    public void setNombre_cliente(String Nombre_cliente) {
        this.Nombre_cliente = Nombre_cliente;
    }

}