
package Controladores;

import Modelos.Cliente;
import Modelos.ClienteDao;
import Modelos.DetalleVenta;
import Modelos.Producto;
import Modelos.ProductoDao;
import Modelos.Venta;
import Modelos.VentaDao;
import interfazes.interfaz_empleado;

import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class VentasControladorEmpleados implements ActionListener {

    private static final String OPCION_NUEVO_CLIENTE = "+ Nuevo cliente...";

    // DAOs
    private final VentaDao ventaDAO;
    private final ProductoDao productoDAO;
    private final ClienteDao clienteDAO;

    // Componentes de la vista
    private final JTable tblVentasListVentas;
    private final JTable tblVentasDetalleVentaSeleccionada;
    private final JTable tblVentasRegistroDetalleVenta;

    private final JComboBox<String> cboVentasCliente;
    private final JComboBox<String> cboVentasSeleccionarProducto;

    private final JTextField txtVentasSeleccionarCantidad;

    private final JButton btnVentasRestablecer;
    private final JButton btnVentasGuardar;
    private final JButton btnVentasActualizar;
    private final JButton btnVentasEliminar;
    private final JButton btnVentasAgregarProducto;

    private final Label lblTotalValor;

    private final Consumer<String> MostrarTotalCarrito;

    // Métodos para refrescar las tablas externas
    private final Runnable refrescarClientes;
    private final Runnable refrescarProductos;

    // Estado en memoria
    private final List<Producto> productosDisponibles = new ArrayList<>();
    private final List<Cliente> clientesDisponibles = new ArrayList<>();
    private final List<Venta> ventasListadas = new ArrayList<>();
    private final List<DetalleVenta> carritoActual = new ArrayList<>();

    private Integer idVentaEnEdicion = null;

    /**
     * Constructor para el panel de Ventas.
     */
    public VentasControladorEmpleados(
            interfaz_empleado vista,
            VentaDao ventaDAO,
            ProductoDao productoDAO,
            ClienteDao clienteDAO,
            Runnable refrescarClientes,
            Runnable refrescarProductos
    ) {

        this(
                ventaDAO,
                productoDAO,
                clienteDAO,

                vista.tblVentasListVentas,
                vista.tblVentasDetalleVentaSeleccionada,
                vista.tblVentasRegistroDetalleVenta,

                vista.cboVentasCliente,
                vista.cboVentasSeleccionarProducto,

                vista.txtVentasSeleccionarCantidad,

                vista.btnVentasRestablecer,
                vista.btnVentasGuardar,
                vista.btnVentasActualizar,
                vista.btnVentasEliminar,
                vista.btnVentasAgregarProducto,

                vista.lblTotalValor,

                texto -> vista.txtVentasResultadoTotal.setText(texto),

                refrescarClientes,
                refrescarProductos
        );
    }

    /**
     * Constructor principal.
     */
    public VentasControladorEmpleados(
            VentaDao ventaDAO,
            ProductoDao productoDAO,
            ClienteDao clienteDAO,

            JTable tblVentasListVentas,
            JTable tblVentasDetalleVentaSeleccionada,
            JTable tblVentasRegistroDetalleVenta,

            JComboBox<String> cboVentasCliente,
            JComboBox<String> cboVentasSeleccionarProducto,

            JTextField txtVentasSeleccionarCantidad,

            JButton btnVentasRestablecer,
            JButton btnVentasGuardar,
            JButton btnVentasActualizar,
            JButton btnVentasEliminar,
            JButton btnVentasAgregarProducto,

            Label lblTotalValor,

            Consumer<String> MostrarTotalCarrito,

            Runnable refrescarClientes,
            Runnable refrescarProductos
    ) {

        this.ventaDAO = ventaDAO;
        this.productoDAO = productoDAO;
        this.clienteDAO = clienteDAO;

        this.tblVentasListVentas = tblVentasListVentas;
        this.tblVentasDetalleVentaSeleccionada = tblVentasDetalleVentaSeleccionada;
        this.tblVentasRegistroDetalleVenta = tblVentasRegistroDetalleVenta;

        this.cboVentasCliente = cboVentasCliente;
        this.cboVentasSeleccionarProducto = cboVentasSeleccionarProducto;

        this.txtVentasSeleccionarCantidad = txtVentasSeleccionarCantidad;

        this.btnVentasRestablecer = btnVentasRestablecer;
        this.btnVentasGuardar = btnVentasGuardar;
        this.btnVentasActualizar = btnVentasActualizar;
        this.btnVentasEliminar = btnVentasEliminar;
        this.btnVentasAgregarProducto = btnVentasAgregarProducto;

        this.lblTotalValor = lblTotalValor;

        this.MostrarTotalCarrito = MostrarTotalCarrito;

        this.refrescarClientes = refrescarClientes;
        this.refrescarProductos = refrescarProductos;

        // Eventos
        this.btnVentasGuardar.addActionListener(this);
        this.btnVentasRestablecer.addActionListener(this);
        this.btnVentasActualizar.addActionListener(this);
        this.btnVentasEliminar.addActionListener(this);
        this.btnVentasAgregarProducto.addActionListener(this);
        this.cboVentasCliente.addActionListener(this);

        // Seleccionar una venta
        this.tblVentasListVentas.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                mostrarDetalleVentaSeleccionada();
            }
        });

        // Seleccionar producto del carrito
        this.tblVentasRegistroDetalleVenta.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                quitarDelCarritoSiSeConfirma();
            }
        });

        // Carga inicial
        cargarClientesEnCombo();
        cargarProductosEnCombo();
        refrescarTablaVentas();
        refrescarCarritoUI();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnVentasGuardar) {

            guardarVenta();

        } else if (e.getSource() == btnVentasRestablecer) {

            restablecerFormulario();

        } else if (e.getSource() == btnVentasActualizar) {

            cargarVentaSeleccionadaParaEditar();

        } else if (e.getSource() == btnVentasEliminar) {

            eliminarVentaSeleccionada();

        } else if (e.getSource() == btnVentasAgregarProducto) {

            agregarProductoAlCarrito();

        } else if (e.getSource() == cboVentasCliente) {

            manejarSeleccionCliente();
        }
    }

    // =========================================================
    // CLIENTES
    // =========================================================

    private void cargarClientesEnCombo() {

        clientesDisponibles.clear();

        clientesDisponibles.addAll(
                clienteDAO.listarTodos()
        );

        DefaultComboBoxModel<String> modelo =
                new DefaultComboBoxModel<>();

        for (Cliente c : clientesDisponibles) {

            modelo.addElement(
                    c.getNombre_cliente()
                    + " - "
                    + c.getTelefono_cliente()
            );
        }

        modelo.addElement(OPCION_NUEVO_CLIENTE);

        cboVentasCliente.setModel(modelo);
        cboVentasCliente.setSelectedIndex(-1);
    }

    // =========================================================
    // PRODUCTOS
    // =========================================================

    private void cargarProductosEnCombo() {

        productosDisponibles.clear();

        productosDisponibles.addAll(
                productoDAO.listarDisponibles()
        );

        DefaultComboBoxModel<String> modelo =
                new DefaultComboBoxModel<>();

        for (Producto p : productosDisponibles) {

            modelo.addElement(
                    p.getNombre_producto()
                    + " - $"
                    + p.getPrecio()
                    + " (Stock: "
                    + p.getStock()
                    + ")"
            );
        }

        cboVentasSeleccionarProducto.setModel(modelo);
    }
    
    public void actualizarDatosVentas() {
    cargarClientesEnCombo();
    cargarProductosEnCombo();
}

    // =========================================================
    // TABLA DE VENTAS
    // =========================================================

    private void refrescarTablaVentas() {

        ventasListadas.clear();

        ventasListadas.addAll(
                ventaDAO.listarVentas()
        );

        DefaultTableModel modelo =
                new DefaultTableModel(
                        new Object[]{
                            "ID",
                            "Fecha",
                            "Cliente",
                            "Total"
                        },
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        int totalGeneral = 0;

        for (Venta v : ventasListadas) {

            modelo.addRow(
                    new Object[]{
                        v.getID_Venta(),
                        v.getFecha(),
                        v.getNombre_cliente(),
                        v.getTotal()
                    }
            );

            totalGeneral += v.getTotal();
        }

        tblVentasListVentas.setModel(modelo);

        lblTotalValor.setText(
                "$" + totalGeneral
        );
    }

    // =========================================================
    // DETALLE DE VENTA
    // =========================================================

    private void mostrarDetalleVentaSeleccionada() {

        int fila =
                tblVentasListVentas.getSelectedRow();

        if (fila == -1) {
            return;
        }

        int idVenta =
                (int) tblVentasListVentas
                        .getModel()
                        .getValueAt(fila, 0);

        List<DetalleVenta> detalle =
                ventaDAO.obtenerDetallePorVenta(idVenta);

        DefaultTableModel modelo =
                new DefaultTableModel(
                        new Object[]{
                            "Productos",
                            "Cantidad",
                            "P.unitario",
                            "Subtotal"
                        },
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        for (DetalleVenta d : detalle) {

            modelo.addRow(
                    new Object[]{
                        d.getNombre_Producto(),
                        d.getCantidad(),
                        d.getPrecio_Unitario(),
                        d.getSubtotal()
                    }
            );
        }

        tblVentasDetalleVentaSeleccionada
                .setModel(modelo);
    }

    // =========================================================
    // AGREGAR PRODUCTO AL CARRITO
    // =========================================================

    private void agregarProductoAlCarrito() {

        int indice =
                cboVentasSeleccionarProducto
                        .getSelectedIndex();

        if (indice == -1
                || indice >= productosDisponibles.size()) {

            JOptionPane.showMessageDialog(
                    null,
                    "Selecciona un producto.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Producto producto =
                productosDisponibles.get(indice);

        int cantidad;

        try {

            cantidad = Integer.parseInt(
                    txtVentasSeleccionarCantidad
                            .getText()
                            .trim()
            );

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    null,
                    "La cantidad debe ser un número.",
                    "Cantidad inválida",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (cantidad <= 0) {

            JOptionPane.showMessageDialog(
                    null,
                    "La cantidad debe ser mayor a 0.",
                    "Cantidad inválida",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        DetalleVenta existente = null;

        for (DetalleVenta d : carritoActual) {

            if (d.getFk_ID_Producto()
                    == producto.getId_producto()) {

                existente = d;
                break;
            }
        }

        int enCarrito =
                existente != null
                ? existente.getCantidad()
                : 0;

        int limiteDisponible =
                producto.getStock()
                + enCarrito;

        if (enCarrito + cantidad
                > limiteDisponible) {

            JOptionPane.showMessageDialog(
                    null,
                    "Stock insuficiente. Disponible: "
                    + limiteDisponible,
                    "Stock insuficiente",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (existente != null) {

            existente.setCantidad(
                    existente.getCantidad()
                    + cantidad
            );

            existente.calcularSubtotal();

        } else {

            DetalleVenta nuevo =
                    new DetalleVenta();

            nuevo.setFk_ID_Producto(
                    producto.getId_producto()
            );

            nuevo.setNombre_Producto(
                    producto.getNombre_producto()
            );

            nuevo.setCantidad(cantidad);

            nuevo.setPrecio_Unitario(
                    producto.getPrecio()
            );

            nuevo.calcularSubtotal();

            carritoActual.add(nuevo);
        }

        txtVentasSeleccionarCantidad.setText("");

        refrescarCarritoUI();
    }

    // =========================================================
    // QUITAR PRODUCTO
    // =========================================================

    private void quitarDelCarritoSiSeConfirma() {

        int fila =
                tblVentasRegistroDetalleVenta
                        .getSelectedRow();

        if (fila == -1
                || fila >= carritoActual.size()) {

            return;
        }

        DetalleVenta seleccionado =
                carritoActual.get(fila);

        int confirmacion =
                JOptionPane.showConfirmDialog(
                        null,
                        "¿Quitar \""
                        + seleccionado.getNombre_Producto()
                        + "\" de la venta?",
                        "Quitar producto",
                        JOptionPane.YES_NO_OPTION
                );

        if (confirmacion
                == JOptionPane.YES_OPTION) {

            carritoActual.remove(fila);

            refrescarCarritoUI();
        }
    }

    // =========================================================
    // TABLA DEL CARRITO
    // =========================================================

    private void refrescarCarritoUI() {

        DefaultTableModel modelo =
                new DefaultTableModel(
                        new Object[]{
                            "Producto",
                            "Cantidad",
                            "Precio Unitario",
                            "Subtotal"
                        },
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        int total = 0;

        for (DetalleVenta d : carritoActual) {

            modelo.addRow(
                    new Object[]{
                        d.getNombre_Producto(),
                        d.getCantidad(),
                        d.getPrecio_Unitario(),
                        d.getSubtotal()
                    }
            );

            total += d.getSubtotal();
        }

        tblVentasRegistroDetalleVenta
                .setModel(modelo);

        MostrarTotalCarrito.accept(
                "Total: $" + total
        );
    }

    // =========================================================
    // RESTABLECER
    // =========================================================

    private void restablecerFormulario() {

        carritoActual.clear();

        idVentaEnEdicion = null;

        cboVentasCliente.setSelectedIndex(-1);

        txtVentasSeleccionarCantidad.setText("");

        refrescarCarritoUI();
    }

    // =========================================================
    // GUARDAR VENTA
    // =========================================================

    private void guardarVenta() {

        int indiceCliente =
                cboVentasCliente.getSelectedIndex();

        if (indiceCliente == -1
                || indiceCliente >= clientesDisponibles.size()) {

            JOptionPane.showMessageDialog(
                    null,
                    "Selecciona un cliente.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (carritoActual.isEmpty()) {

            JOptionPane.showMessageDialog(
                    null,
                    "Agrega al menos un producto a la venta.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Cliente cliente =
                clientesDisponibles.get(indiceCliente);

        int total = 0;

        for (DetalleVenta d : carritoActual) {

            total += d.getSubtotal();
        }

        Venta venta = new Venta();

        venta.setFk_ID_Cliente(
                cliente.getId_cliente()
        );

        venta.setTotal(total);

        boolean exito;

        if (idVentaEnEdicion == null) {

            int idGenerado =
                    ventaDAO.crearVenta(
                            venta,
                            carritoActual
                    );

            exito = idGenerado != -1;

            if (exito) {

                JOptionPane.showMessageDialog(
                        null,
                        "Venta registrada correctamente.",
                        "InvSystem",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } else {

            exito =
                    ventaDAO.actualizarVenta(
                            idVentaEnEdicion,
                            venta,
                            carritoActual
                    );

            if (exito) {

                JOptionPane.showMessageDialog(
                        null,
                        "Venta actualizada correctamente.",
                        "InvSystem",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        }

        if (exito) {

            restablecerFormulario();

            // Actualizar combo de productos
            cargarProductosEnCombo();

            // ACTUALIZAR TABLA DE PRODUCTOS
            if (refrescarProductos != null) {
                refrescarProductos.run();
            }

            // Actualizar tabla de ventas
            refrescarTablaVentas();
        }
    }

    // =========================================================
    // EDITAR VENTA
    // =========================================================

    private void cargarVentaSeleccionadaParaEditar() {

        int fila =
                tblVentasListVentas.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    null,
                    "Selecciona una venta de la tabla.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int idVenta =
                (int) tblVentasListVentas
                        .getModel()
                        .getValueAt(fila, 0);

        Integer fkIdCliente = null;

        for (Venta v : ventasListadas) {

            if (v.getID_Venta() == idVenta) {

                fkIdCliente =
                        v.getFk_ID_Cliente();

                break;
            }
        }

        carritoActual.clear();

        carritoActual.addAll(
                ventaDAO.obtenerDetallePorVenta(idVenta)
        );

        idVentaEnEdicion = idVenta;

        if (fkIdCliente != null) {

            for (int i = 0;
                    i < clientesDisponibles.size();
                    i++) {

                if (clientesDisponibles
                        .get(i)
                        .getId_cliente()
                        == fkIdCliente) {

                    cboVentasCliente
                            .setSelectedIndex(i);

                    break;
                }
            }
        }

        refrescarCarritoUI();

        JOptionPane.showMessageDialog(
                null,
                "Editando la venta #" + idVenta
                + ". Ajusta los productos y presiona Guardar para confirmar los cambios.",
                "Editar venta",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =========================================================
    // ELIMINAR VENTA
    // =========================================================

    private void eliminarVentaSeleccionada() {

        int fila =
                tblVentasListVentas.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    null,
                    "Selecciona una venta de la tabla.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int idVenta =
                (int) tblVentasListVentas
                        .getModel()
                        .getValueAt(fila, 0);

        int confirmacion =
                JOptionPane.showConfirmDialog(
                        null,
                        "¿Eliminar la venta #"
                        + idVenta
                        + "? Esto repondrá el stock vendido.",
                        "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION
                );

        if (confirmacion
                != JOptionPane.YES_OPTION) {

            return;
        }

        boolean exito =
                ventaDAO.eliminarVenta(idVenta);

        if (exito) {

            JOptionPane.showMessageDialog(
                    null,
                    "Venta eliminada correctamente.",
                    "InvSystem",
                    JOptionPane.INFORMATION_MESSAGE
            );

            if (idVentaEnEdicion != null
                    && idVentaEnEdicion == idVenta) {

                restablecerFormulario();
            }

            // Actualizar combo
            cargarProductosEnCombo();

            // ACTUALIZAR TABLA DE PRODUCTOS
            if (refrescarProductos != null) {
                refrescarProductos.run();
            }

            // Actualizar tabla de ventas
            refrescarTablaVentas();

            tblVentasDetalleVentaSeleccionada
                    .setModel(
                            new DefaultTableModel(
                                    new Object[]{
                                        "Productos",
                                        "Cantidad",
                                        "P.unitario",
                                        "Subtotal"
                                    },
                                    0
                            )
                    );

        } else {

            JOptionPane.showMessageDialog(
                    null,
                    "No se pudo eliminar la venta.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // CREAR CLIENTE DESDE VENTAS
    // =========================================================

    private void manejarSeleccionCliente() {

        Object seleccionado =
                cboVentasCliente.getSelectedItem();

        if (seleccionado == null
                || !OPCION_NUEVO_CLIENTE.equals(
                        seleccionado.toString())) {

            return;
        }

        String nombre =
                JOptionPane.showInputDialog(
                        null,
                        "Nombre del cliente:",
                        "Nuevo cliente",
                        JOptionPane.QUESTION_MESSAGE
                );

        if (nombre == null
                || nombre.trim().isEmpty()) {

            cboVentasCliente.setSelectedIndex(-1);

            return;
        }

        String telefono =
                JOptionPane.showInputDialog(
                        null,
                        "Teléfono del cliente:",
                        "Nuevo cliente",
                        JOptionPane.QUESTION_MESSAGE
                );

        /*
         * Usamos el constructor de 3 parámetros
         * que ya tienes en Cliente.java.
         *
         * El ID se coloca en 0 porque la base de datos
         * será la encargada de generarlo.
         */
        Cliente nuevoCliente =
                new Cliente(
                        0,
                        nombre.trim(),
                        telefono == null
                                ? ""
                                : telefono.trim()
                );

        int idGenerado =
                clienteDAO.insertarCliente(
                        nuevoCliente
                );

        if (idGenerado == -1) {

            JOptionPane.showMessageDialog(
                    null,
                    "No se pudo registrar el cliente.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            cboVentasCliente.setSelectedIndex(-1);

            return;
        }

        // Actualizar el combo de clientes
        cargarClientesEnCombo();

        // ACTUALIZAR TABLA DE CLIENTES
        if (refrescarClientes != null) {
            refrescarClientes.run();
        }

        // Seleccionar automáticamente
        // el cliente recién creado
        for (int i = 0;
                i < clientesDisponibles.size();
                i++) {

            if (clientesDisponibles
                    .get(i)
                    .getId_cliente()
                    == idGenerado) {

                cboVentasCliente
                        .setSelectedIndex(i);

                break;
            }
        }
    }
}