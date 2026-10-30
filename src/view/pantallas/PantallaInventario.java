package view.pantallas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import controller.BatallaController;
import model.DTOs.CombatienteDTO;
import model.DTOs.ItemInventarioDTO;
import model.objetos.TipoRareza;
import view.componentes.BotonRPG;
import view.componentes.PanelMarcoDorado;

/** Inventario modal: solo un uso válido consume el turno del personaje activo. */
public class PantallaInventario extends JDialog {
    private static final Color FONDO = new Color(22, 19, 16);
    private static final Color TEXTO = new Color(232, 225, 207);
    private static final Color DORADO = new Color(212, 175, 55);
    private static final Color SECUNDARIO = new Color(180, 173, 156);
    private static final Color ERROR = new Color(235, 150, 135);

    private final BatallaController controller;
    private final int indicePersonaje;
    private final CombatienteDTO personajeEnTurno;
    private final DefaultListModel<ItemInventarioDTO> modeloItems = new DefaultListModel<>();
    private final JList<ItemInventarioDTO> listaItems = new JList<>(modeloItems);
    private final JLabel lblNombreItem = etiqueta("Elegí un objeto", 20, DORADO);
    private final JLabel lblRareza = etiqueta("", 12, SECUNDARIO);
    private final JLabel lblEstadoPersonaje = etiqueta("", 12, TEXTO);
    private final JTextArea descripcion = texto(0);
    private final JTextArea mensaje = texto(2);
    private final BotonRPG btnUsar = new BotonRPG("USAR Y TERMINAR TURNO");
    private final BotonRPG btnCancelar = new BotonRPG("CANCELAR");
    private boolean accionRealizada;
    private String mensajeAccion;

    public PantallaInventario(JFrame propietario, BatallaController controller, int indicePersonaje) {
        super(propietario, "Chronicles of UADE - Inventario", true);
        this.controller = Objects.requireNonNull(controller);
        this.indicePersonaje = indicePersonaje;
        List<CombatienteDTO> party = controller.getPartyActual();
        if (indicePersonaje < 0 || indicePersonaje >= party.size()
                || indicePersonaje != controller.getIndicePersonajeEnTurno()) {
            throw new IllegalStateException("El inventario solo se puede abrir durante un turno del jugador.");
        }
        this.personajeEnTurno = party.get(indicePersonaje);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 540);
        setMinimumSize(new Dimension(760, 510));
        setContentPane(crearContenido(personajeEnTurno));
        configurarEventos();
        cargarItems();
        setLocationRelativeTo(propietario);
    }

    private JPanel crearContenido(CombatienteDTO actor) {
        JPanel principal = new JPanel(new BorderLayout(0, 18));
        principal.setBackground(FONDO);
        principal.setBorder(BorderFactory.createEmptyBorder(22, 24, 20, 24));

        JPanel cabecera = new JPanel(new BorderLayout(0, 7));
        cabecera.setOpaque(false);
        cabecera.add(etiqueta("INVENTARIO", 27, DORADO), BorderLayout.NORTH);
        cabecera.add(etiqueta("Turno de " + actor.getNombre() + " · " + actor.getClase(), 14, TEXTO),
                BorderLayout.CENTER);
        cabecera.add(etiqueta(controller.getCantidadItemsInventario() + " / " + controller.getCapacidadInventario()
                + " objetos · Usar una poción consume la acción de este personaje.", 12, SECUNDARIO),
                BorderLayout.SOUTH);
        principal.add(cabecera, BorderLayout.NORTH);

        JPanel contenido = new JPanel(new GridLayout(1, 2, 18, 0));
        contenido.setOpaque(false);
        contenido.add(crearLista());
        contenido.add(crearDetalle());
        principal.add(contenido, BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout(0, 10));
        pie.setOpaque(false);
        mensaje.setForeground(SECUNDARIO);
        pie.add(mensaje, BorderLayout.CENTER);
        JPanel acciones = new JPanel(new BorderLayout(12, 0));
        acciones.setOpaque(false);
        btnCancelar.setPreferredSize(new Dimension(155, 42));
        btnUsar.setPreferredSize(new Dimension(310, 42));
        acciones.add(btnCancelar, BorderLayout.WEST);
        acciones.add(btnUsar, BorderLayout.EAST);
        pie.add(acciones, BorderLayout.SOUTH);
        principal.add(pie, BorderLayout.SOUTH);
        return principal;
    }

    private JPanel crearLista() {
        JPanel panel = new PanelMarcoDorado("OBJETOS");
        panel.setLayout(new BorderLayout());
        listaItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaItems.setBackground(FONDO);
        listaItems.setForeground(TEXTO);
        listaItems.setFont(new Font("Georgia", Font.PLAIN, 14));
        listaItems.setFixedCellHeight(52);
        listaItems.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                    boolean seleccionado, boolean foco) {
                JLabel etiqueta = (JLabel) super.getListCellRendererComponent(lista, valor, indice, seleccionado, foco);
                ItemInventarioDTO item = (ItemInventarioDTO) valor;
                etiqueta.setText(item.getNombre() + " · " + nombreRareza(item.getRareza()));
                etiqueta.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                etiqueta.setBackground(seleccionado ? new Color(65, 49, 25) : FONDO);
                etiqueta.setForeground(item.isConsumible() ? colorRareza(item.getRareza()) : SECUNDARIO);
                return etiqueta;
            }
        });
        JScrollPane scroll = new JScrollPane(listaItems);
        scroll.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        scroll.getViewport().setBackground(FONDO);
        scroll.setBackground(FONDO);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearDetalle() {
        JPanel panel = new PanelMarcoDorado("USAR OBJETO");
        panel.setLayout(new BorderLayout(0, 14));
        JPanel interior = new JPanel(new BorderLayout(0, 16));
        interior.setOpaque(false);
        interior.setBorder(BorderFactory.createEmptyBorder(12, 14, 14, 14));

        JPanel titulo = new JPanel(new BorderLayout(0, 6));
        titulo.setOpaque(false);
        titulo.add(lblNombreItem, BorderLayout.NORTH);
        titulo.add(lblRareza, BorderLayout.CENTER);
        interior.add(titulo, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(descripcion);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        interior.add(scroll, BorderLayout.CENTER);

        JPanel personaje = new JPanel(new GridLayout(3, 1, 0, 8));
        personaje.setOpaque(false);
        personaje.add(etiqueta("PERSONAJE QUE USA LA POCIÓN", 12, DORADO));
        personaje.add(etiqueta(personajeEnTurno.getNombre() + " (" + personajeEnTurno.getClase() + ")", 14, TEXTO));
        personaje.add(lblEstadoPersonaje);
        interior.add(personaje, BorderLayout.SOUTH);
        panel.add(interior, BorderLayout.CENTER);
        return panel;
    }

    private void configurarEventos() {
        listaItems.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarSeleccion();
            }
        });
        btnUsar.addActionListener(e -> usarSeleccion());
        btnCancelar.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnUsar);
        getRootPane().registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void cargarItems() {
        for (ItemInventarioDTO item : controller.getItemsInventario()) {
            modeloItems.addElement(item);
        }
        if (!modeloItems.isEmpty()) {
            listaItems.setSelectedIndex(0);
        }
        actualizarSeleccion();
    }

    private void actualizarSeleccion() {
        ItemInventarioDTO item = listaItems.getSelectedValue();
        lblEstadoPersonaje.setText("Vida " + personajeEnTurno.getVidaActual() + "/" + personajeEnTurno.getVidaMax()
                + " · " + personajeEnTurno.getNombreRecurso() + " " + personajeEnTurno.getRecursoActual()
                + "/" + personajeEnTurno.getRecursoMax());
        if (item == null) {
            lblNombreItem.setText(modeloItems.isEmpty() ? "Inventario vacío" : "Elegí un objeto");
            lblRareza.setText("");
            descripcion.setText(modeloItems.isEmpty()
                    ? "Podés obtener pociones al ganar niveles. Cuando tengas objetos, aparecerán en esta lista."
                    : "Seleccioná un objeto para consultar su efecto.");
            btnUsar.setEnabled(false);
            mostrarMensaje("Podés cerrar el inventario y conservar tu turno.", false);
            return;
        }
        lblNombreItem.setText(item.getNombre());
        lblNombreItem.setToolTipText(item.getNombre());
        lblRareza.setText(nombreRareza(item.getRareza()) + " · " + (item.isConsumible() ? "Consumible" : "Equipable"));
        lblRareza.setForeground(colorRareza(item.getRareza()));
        descripcion.setText(item.getDescripcion());
        descripcion.setCaretPosition(0);
        String motivo = controller.getMotivoNoPuedeUsarItem(indicePersonaje, item.getIndice());
        btnUsar.setEnabled(motivo == null);
        btnUsar.setToolTipText(motivo);
        mostrarMensaje(motivo == null
                ? "Al confirmar se consume el objeto y termina el turno del personaje activo."
                : motivo, motivo != null);
    }

    private void usarSeleccion() {
        if (accionRealizada) {
            return;
        }
        ItemInventarioDTO item = listaItems.getSelectedValue();
        if (item == null) {
            return;
        }
        try {
            controller.resolverUsoItem(indicePersonaje, item.getIndice());
            accionRealizada = true;
            mensajeAccion = item.getNombre() + " aplicada a " + personajeEnTurno.getNombre() + ". Turno finalizado.";
            dispose();
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarMensaje(e.getMessage(), true);
        }
    }

    private void mostrarMensaje(String texto, boolean error) {
        mensaje.setText(texto);
        mensaje.setForeground(error ? ERROR : SECUNDARIO);
    }

    private static JLabel etiqueta(String texto, int tamano, Color color) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("Georgia", Font.BOLD, tamano));
        etiqueta.setForeground(color);
        return etiqueta;
    }

    private static JTextArea texto(int filas) {
        JTextArea texto = new JTextArea(filas, 0);
        texto.setEditable(false);
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setOpaque(false);
        texto.setFont(new Font("Georgia", Font.PLAIN, 14));
        texto.setForeground(TEXTO);
        return texto;
    }

    private static String nombreRareza(TipoRareza rareza) {
        if (rareza == null) {
            return "Sin rareza";
        }
        switch (rareza) {
            case RARO:
                return "Raro";
            case EPICO:
                return "Épico";
            default:
                return "Común";
        }
    }

    private static Color colorRareza(TipoRareza rareza) {
        if (rareza == TipoRareza.RARO) {
            return new Color(123, 187, 235);
        }
        if (rareza == TipoRareza.EPICO) {
            return new Color(200, 152, 235);
        }
        return TEXTO;
    }

    public boolean isAccionRealizada() {
        return accionRealizada;
    }

    public String getMensajeAccion() {
        return mensajeAccion;
    }

    public BotonRPG getBtnUsar() {
        return btnUsar;
    }

    public BotonRPG getBtnCancelar() {
        return btnCancelar;
    }

    public JList<ItemInventarioDTO> getListaItems() {
        return listaItems;
    }

}
