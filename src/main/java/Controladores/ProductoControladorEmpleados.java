package Controladores;

import Modelos.Producto;
import Modelos.ProductoDao;
import interfazes.interfaz_empleado;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ProductoControladorEmpleados {

    private final interfaz_empleado vista;
    private final ProductoDao pDAO;
    private DefaultTableModel modelo = new DefaultTableModel();

    // Constructor que recibe la Vista y el DAO
    public ProductoControladorEmpleados(interfaz_empleado vista, ProductoDao pDAO) {
        this.vista = vista;
        this.pDAO = pDAO;


        // Evento para la búsqueda en tiempo real al escribir ---
        this.vista.txtBuscarProductos.addKeyListener(new java.awt.event.KeyAdapter() {
        @Override
        public void keyReleased(java.awt.event.KeyEvent evt) {
            String textoBusqueda = vista.txtBuscarProductos.getText();
            buscarProductos(textoBusqueda);
        }
    });

        // Cargar los productos inicialmente
        refrescarTabla();
    }

    // --- Métodos de Lógica ---

    public void mostrarProductos() {
        modelo = (DefaultTableModel) vista.tblProductosEmpleados.getModel();
        modelo.setRowCount(0);
        List<Producto> lista = pDAO.listarTodos();
        Object[] pr = new Object[6];
        for (int i = 0; i < lista.size(); i++) {
            pr[0] = lista.get(i).getId_producto();
            pr[1] = lista.get(i).getNombre_producto();
            pr[2] = lista.get(i).getPrecio();
            pr[3] = lista.get(i).getStock();
            pr[4] = lista.get(i).getCategoria();
            pr[5] = lista.get(i).isEstado_producto() ? "Activo" : "Inactivo";
            modelo.addRow(pr);
        }
        vista.tblProductosEmpleados.setModel(modelo);
    }



    public void limpiarTabla() {
        if (modelo != null) {
            modelo.setRowCount(0);
        }
    }

    public void refrescarTabla() {
        limpiarTabla();
        mostrarProductos();
    }
    
    // Filtra y muestra los productos según el término ingresado
public void buscarProductos(String texto) {
    limpiarTabla();
    modelo = (DefaultTableModel) vista.tblProductosEmpleados.getModel();
    
    List<Producto> lista = pDAO.buscarProductosPorNombre(texto);
    Object[] pr = new Object[6];
    
    for (int i = 0; i < lista.size(); i++) {
        pr[0] = lista.get(i).getId_producto();
        pr[1] = lista.get(i).getNombre_producto();
        pr[2] = lista.get(i).getPrecio();
        pr[3] = lista.get(i).getStock();
        pr[4] = lista.get(i).getCategoria();
        pr[5] = lista.get(i).isEstado_producto() ? "Activo" : "Inactivo";
        modelo.addRow(pr);
    }
    vista.tblProductosEmpleados.setModel(modelo);
}

}