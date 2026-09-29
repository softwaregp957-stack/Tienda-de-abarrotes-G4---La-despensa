package Controladores;

import Modelos.Cliente;
import Modelos.ClienteDao;
import interfazes.interfaz_empleado;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ClientesControladorEmpleados implements ActionListener {

    private final interfaz_empleado vista;
    private final ClienteDao cDAO;
    private DefaultTableModel modelo = new DefaultTableModel();

    public ClientesControladorEmpleados(interfaz_empleado vista, ClienteDao cDAO) {
        this.vista = vista;
        this.cDAO = cDAO;

        this.vista.btnAgregarClientes.addActionListener(this);
        this.vista.btnEliminarClientes.addActionListener(this);
        this.vista.btnEditarClientes.addActionListener(this);

        this.vista.tblClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarFilaTabla();
            }
        });

        this.vista.txtBuscarClientes.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String textoBusqueda = vista.txtBuscarClientes.getText();
                buscarClientes(textoBusqueda);
            }
        });

        refrescarTabla();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnAgregarClientes) {
            guardarCliente();
        } else if (e.getSource() == vista.btnEliminarClientes) {
            eliminarCliente();
        } else if (e.getSource() == vista.btnEditarClientes) {
            actualizarCliente();
        }
    }

    public void mostrarClientes() {
        modelo = (DefaultTableModel) vista.tblClientes.getModel();
        modelo.setRowCount(0);
        List<Cliente> lista = cDAO.listarTodos();
        Object[] fila = new Object[3];
        for (int i = 0; i < lista.size(); i++) {
            fila[0] = lista.get(i).getId_cliente();
            fila[1] = lista.get(i).getNombre_cliente();
            fila[2] = lista.get(i).getTelefono_cliente();
            modelo.addRow(fila);
        }
        vista.tblClientes.setModel(modelo);
    }

    private void guardarCliente() {
        if (vista.txtNombreCliente.getText().trim().isEmpty() ||
            vista.txtTelefono_cliente.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(vista, "Todos los campos son obligatorios", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = vista.txtNombreCliente.getText().trim();
        String telefono = vista.txtTelefono_cliente.getText().trim();

        Cliente cliente = new Cliente(0, nombre, telefono);

        int nuevoId = cDAO.insertarCliente(cliente);

        if (nuevoId != -1) {
            JOptionPane.showMessageDialog(vista, "Cliente creado exitosamente con el ID: " + nuevoId, "InvSystem", JOptionPane.INFORMATION_MESSAGE);
            refrescarTabla();
            limpiarCampos();
        }
    }

    private void seleccionarFilaTabla() {
        int fila = vista.tblClientes.getSelectedRow();
        if (fila >= 0) {
            vista.txtIDCliente.setText(vista.tblClientes.getValueAt(fila, 0).toString());
            vista.txtNombreCliente.setText(vista.tblClientes.getValueAt(fila, 1).toString());
            if (vista.tblClientes.getValueAt(fila, 2) != null) {
                vista.txtTelefono_cliente.setText(vista.tblClientes.getValueAt(fila, 2).toString());
            }
        }
    }

    private void actualizarCliente() {
        if (vista.txtIDCliente.getText().trim().isEmpty() ||
            vista.txtNombreCliente.getText().trim().isEmpty() ||
            vista.txtTelefono_cliente.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(vista, "Seleccione un cliente de la tabla y llene todos los campos", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(vista.txtIDCliente.getText().trim());
            String nombre = vista.txtNombreCliente.getText().trim();
            String telefono = vista.txtTelefono_cliente.getText().trim();

            Cliente cliente = new Cliente(id, nombre, telefono);

            boolean editadoExitoso = cDAO.actualizarCliente(cliente);

            if (editadoExitoso) {
                JOptionPane.showMessageDialog(vista, "Cliente actualizado exitosamente", "InvSystem", JOptionPane.INFORMATION_MESSAGE);
                refrescarTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(vista, "No se pudo actualizar el cliente en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "El ID debe ser numérico válido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarCliente() {
        int fila = vista.tblClientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Debe seleccionar un cliente de la tabla", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(vista.tblClientes.getValueAt(fila, 0).toString());
        int confirmacion = JOptionPane.showConfirmDialog(vista, "¿Desea eliminar el cliente ID " + id + "?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean borradoExitoso = cDAO.eliminarCliente(id);

            if (borradoExitoso) {
                JOptionPane.showMessageDialog(vista, "Cliente eliminado correctamente", "Información", JOptionPane.INFORMATION_MESSAGE);
                refrescarTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(vista, "No se pudo eliminar el cliente de la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void limpiarCampos() {
        vista.txtIDCliente.setText(null);
        vista.txtNombreCliente.setText(null);
        vista.txtTelefono_cliente.setText(null);
        vista.txtIDCliente.requestFocus();
    }

    public void limpiarTabla() {
        if (modelo != null) {
            modelo.setRowCount(0);
        }
    }

    public void refrescarTabla() {
        limpiarTabla();
        mostrarClientes();
    }

    public void buscarClientes(String texto) {
        limpiarTabla();
        modelo = (DefaultTableModel) vista.tblClientes.getModel();

        List<Cliente> lista = cDAO.buscarClientesPorNombre(texto);
        Object[] fila = new Object[3];

        for (int i = 0; i < lista.size(); i++) {
            fila[0] = lista.get(i).getId_cliente();
            fila[1] = lista.get(i).getNombre_cliente();
            fila[2] = lista.get(i).getTelefono_cliente();
            modelo.addRow(fila);
        }
        vista.tblClientes.setModel(modelo);
    }
    
    
}
