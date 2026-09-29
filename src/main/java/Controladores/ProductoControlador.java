package Controladores;

import Modelos.Producto;
import Modelos.ProductoDao;
import interfazes.interfaz;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ProductoControlador implements ActionListener {

    private final interfaz vista;
    private final ProductoDao pDAO;
    private DefaultTableModel modelo = new DefaultTableModel();

    // Constructor que recibe la Vista y el DAO
    public ProductoControlador(interfaz vista, ProductoDao pDAO) {
        this.vista = vista;
        this.pDAO = pDAO;
        

        // Registrar los eventos de los botones
        this.vista.btnGUARDAR.addActionListener(this);
        this.vista.btnELIMINAR.addActionListener(this);
        this.vista.btnACTUALIZAR.addActionListener(this);

        // Registrar evento al hacer clic sobre la tabla
        this.vista.tblProductos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarFilaTabla();
            }
        });
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

    // Escuchador central de los botones
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnGUARDAR) {
            guardarProducto();
        } else if (e.getSource() == vista.btnELIMINAR) {
            eliminarProducto();
        } else if (e.getSource() == vista.btnACTUALIZAR) {
            actualizarProducto();
        }
    }

    // --- Métodos de Lógica ---

    public void mostrarProductos() {
        modelo = (DefaultTableModel) vista.tblProductos.getModel();
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
        vista.tblProductos.setModel(modelo);
    }
    private void guardarProducto() {
        // 1. Validar solo los campos que ingresa el usuario (ya no validamos txtIDProducto)
        if (vista.txtNombredelProducto.getText().trim().isEmpty() || 
            vista.txtPrecio.getText().trim().isEmpty() || 
            vista.txtStockProductos.getText().trim().isEmpty() ||
            vista.cmbCategoriaProductos.getSelectedItem() == null ||
            vista.cmbEstadoProductos.getSelectedItem() == null) {

            JOptionPane.showMessageDialog(vista, "Todos los campos son obligatorios", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String nombre = vista.txtNombredelProducto.getText().trim();
            int precio = Integer.parseInt(vista.txtPrecio.getText().trim());
            int stock = Integer.parseInt(vista.txtStockProductos.getText().trim());
            String categoria = vista.cmbCategoriaProductos.getSelectedItem().toString();
            boolean estado = vista.cmbEstadoProductos.getSelectedItem().toString().equalsIgnoreCase("Activo");

            // Creamos el producto (pasamos id = 0 temporalmente)
            Producto producto = new Producto(0, nombre, precio, stock, categoria, estado);

            // Insertamos y recibimos el ID autogenerado
            int nuevoId = pDAO.insertarProductos(producto);

            if (nuevoId != -1) {
                JOptionPane.showMessageDialog(vista, "Producto creado exitosamente con el ID: " + nuevoId, "InvSystem", JOptionPane.INFORMATION_MESSAGE);
                refrescarTabla();
                limpiarCampos();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "El Precio y el Stock deben ser números válidos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Evento que se ejecuta al hacer clic sobre una fila de la tabla tblProductos
    private void seleccionarFilaTabla() {
        int fila = vista.tblProductos.getSelectedRow();
        if (fila >= 0) {
            // Carga los datos de la fila seleccionada a las cajas de texto y comboboxes
            vista.txtIDProducto.setText(vista.tblProductos.getValueAt(fila, 0).toString());
            vista.txtNombredelProducto.setText(vista.tblProductos.getValueAt(fila, 1).toString());
            vista.txtPrecio.setText(vista.tblProductos.getValueAt(fila, 2).toString());
            vista.txtStockProductos.setText(vista.tblProductos.getValueAt(fila, 3).toString());

            if (vista.tblProductos.getValueAt(fila, 4) != null) {
                vista.cmbCategoriaProductos.setSelectedItem(vista.tblProductos.getValueAt(fila, 4).toString());
            }
            if (vista.tblProductos.getValueAt(fila, 5) != null) {
                vista.cmbEstadoProductos.setSelectedItem(vista.tblProductos.getValueAt(fila, 5).toString());
            }
        }
    }

private void actualizarProducto() {
        if (vista.txtIDProducto.getText().trim().isEmpty() || 
            vista.txtNombredelProducto.getText().trim().isEmpty() || 
            vista.txtPrecio.getText().trim().isEmpty() || 
            vista.txtStockProductos.getText().trim().isEmpty() ||
            vista.cmbCategoriaProductos.getSelectedItem() == null ||
            vista.cmbEstadoProductos.getSelectedItem() == null) {

            JOptionPane.showMessageDialog(vista, "Seleccione un producto de la tabla y llene todos los campos", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(vista.txtIDProducto.getText().trim());
            String nombre = vista.txtNombredelProducto.getText().trim();
            int precio = Integer.parseInt(vista.txtPrecio.getText().trim());
            int stock = Integer.parseInt(vista.txtStockProductos.getText().trim());
            String categoria = vista.cmbCategoriaProductos.getSelectedItem().toString();
            boolean estado = vista.cmbEstadoProductos.getSelectedItem().toString().equalsIgnoreCase("Activo");

            Producto producto = new Producto(id, nombre, precio, stock, categoria, estado);
            
            // Se invoca el método del DAO y se verifica su resultado
            boolean editadoExitoso = pDAO.actualizarProducto(producto);

            if (editadoExitoso) {
                JOptionPane.showMessageDialog(vista, "Producto actualizado exitosamente", "InvSystem", JOptionPane.INFORMATION_MESSAGE);
                refrescarTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(vista, "No se pudo actualizar el producto en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "El ID, Precio y Stock deben ser numéricos válidos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarProducto() {
        int fila = vista.tblProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Debe seleccionar un producto de la tabla", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(vista.tblProductos.getValueAt(fila, 0).toString());
        int confirmacion = JOptionPane.showConfirmDialog(vista, "¿Desea eliminar el producto ID " + id + "?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            // Se invoca la eliminación en el DAO
            boolean borradoExitoso = pDAO.eliminarProducto(id);

            if (borradoExitoso) {
                JOptionPane.showMessageDialog(vista, "Producto eliminado correctamente", "Información", JOptionPane.INFORMATION_MESSAGE);
                refrescarTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(vista, "No se pudo eliminar el producto de la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    public void limpiarCampos() {
        vista.txtIDProducto.setText(null);
        vista.txtNombredelProducto.setText(null);
        vista.cmbCategoriaProductos.setSelectedIndex(-1);
        vista.txtPrecio.setText(null);
        vista.txtStockProductos.setText(null);
        vista.cmbEstadoProductos.setSelectedIndex(-1);
        vista.txtIDProducto.requestFocus();
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
    modelo = (DefaultTableModel) vista.tblProductos.getModel();
    
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
    vista.tblProductos.setModel(modelo);
}
}