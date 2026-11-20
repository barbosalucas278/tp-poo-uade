package view.pantallas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import controller.EquipamientoController;
import model.DTOs.EquipableDTO;
import model.objetos.TipoObjeto;
import view.componentes.BotonRPG;
import view.componentes.PanelMarcoDorado;

/** Permite administrar el equipo antes de entrar al combate. */
public class PantallaEquipamiento extends JDialog {
    private static final Color FONDO = new Color(22, 19, 16);
    private static final Color TEXTO = new Color(232, 225, 207);
    private static final Color DORADO = new Color(212, 175, 55);
    private static final Color ERROR = new Color(235, 150, 135);

    private final EquipamientoController controller;
    private final JList<String> selectorPersonaje = new JList<>();
    private final DefaultListModel<String> modeloDisponibles = new DefaultListModel<>();
    private final JList<String> listaDisponibles = new JList<>(modeloDisponibles);
    private final JLabel lblEstadisticas = crearEtiqueta("", 14, DORADO);
    private final JLabel lblArma = crearEtiqueta("", 14, TEXTO);
    private final JLabel lblArmadura = crearEtiqueta("", 14, TEXTO);
    private final JLabel lblMensaje = crearEtiqueta(" ", 13, TEXTO);
    private final BotonRPG btnEquipar = new BotonRPG("EQUIPAR");
    private final BotonRPG btnQuitarArma = new BotonRPG("DESEQUIPAR ARMA");
    private final BotonRPG btnQuitarArmadura = new BotonRPG("DESEQUIPAR ARMADURA");

    public PantallaEquipamiento(JFrame propietario, EquipamientoController controller) {
        super(propietario, "Chronicles of UADE - Equipamiento", true);
        this.controller = controller;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(790, 420);
        setMinimumSize(new Dimension(720, 390));

        DefaultListModel<String> personajes = new DefaultListModel<>();
        for (String nombre : controller.getNombresPersonajes()) {
            personajes.addElement((personajes.size() + 1) + ". " + nombre);
        }
        selectorPersonaje.setModel(personajes);
        selectorPersonaje.setSelectedIndex(0);
        setContentPane(crearContenido());
        configurarEventos();
        actualizarPantalla();
        if (modeloDisponibles.isEmpty() && !btnQuitarArma.isEnabled() && !btnQuitarArmadura.isEnabled()) {
            lblMensaje.setText("Ganá una batalla para conseguir armas y armaduras.");
        }
        setLocationRelativeTo(propietario);
    }

    private JPanel crearContenido() {
        JPanel principal = new JPanel(new BorderLayout(0, 15));
        principal.setBackground(FONDO);
        principal.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JPanel cabecera = new JPanel(new BorderLayout(12, 0));
        cabecera.setOpaque(false);
        JLabel lblPersonaje = crearEtiqueta("Personaje", 15, TEXTO);
        lblPersonaje.setLabelFor(selectorPersonaje);
        configurarLista(selectorPersonaje, "Personajes");
        selectorPersonaje.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        selectorPersonaje.setVisibleRowCount(1);
        selectorPersonaje.setFixedCellWidth(115);
        JScrollPane selector = new JScrollPane(selectorPersonaje, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        selector.setPreferredSize(new Dimension(350, 45));
        selector.setBorder(BorderFactory.createEmptyBorder());
        cabecera.add(lblPersonaje, BorderLayout.WEST);
        cabecera.add(selector, BorderLayout.CENTER);
        cabecera.add(lblEstadisticas, BorderLayout.EAST);
        principal.add(cabecera, BorderLayout.NORTH);

        JPanel contenido = new JPanel(new GridLayout(1, 2, 14, 0));
        contenido.setOpaque(false);
        contenido.add(crearInventario());
        contenido.add(crearEquipoActual());
        principal.add(contenido, BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout(12, 0));
        pie.setOpaque(false);
        BotonRPG btnCerrar = new BotonRPG("CERRAR");
        btnCerrar.setPreferredSize(new Dimension(120, 38));
        btnCerrar.addActionListener(e -> dispose());
        pie.add(lblMensaje, BorderLayout.CENTER);
        pie.add(btnCerrar, BorderLayout.EAST);
        principal.add(pie, BorderLayout.SOUTH);
        return principal;
    }

    private JPanel crearInventario() {
        JPanel panel = crearPanel("DISPONIBLES EN INVENTARIO");
        configurarLista(listaDisponibles, "Equipables disponibles");

        JScrollPane scroll = new JScrollPane(listaDisponibles);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);
        btnEquipar.setPreferredSize(new Dimension(140, 38));
        panel.add(btnEquipar, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearEquipoActual() {
        JPanel panel = crearPanel("EQUIPO ACTUAL");
        panel.setLayout(new GridLayout(2, 1, 0, 13));
        panel.add(crearRanura("Arma", lblArma, btnQuitarArma));
        panel.add(crearRanura("Armadura", lblArmadura, btnQuitarArmadura));
        return panel;
    }

    private JPanel crearRanura(String nombre, JLabel etiqueta, BotonRPG boton) {
        JPanel ranura = new JPanel(new BorderLayout(0, 4));
        ranura.setOpaque(false);
        ranura.add(crearEtiqueta(nombre, 14, DORADO), BorderLayout.NORTH);
        ranura.add(etiqueta, BorderLayout.CENTER);
        boton.setPreferredSize(new Dimension(180, 34));
        ranura.add(boton, BorderLayout.SOUTH);
        return ranura;
    }

    private void configurarEventos() {
        selectorPersonaje.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (selectorPersonaje.isSelectionEmpty()) {
                    selectorPersonaje.setSelectedIndex(0);
                } else {
                    actualizarPantalla();
                    mostrarMensaje(" ", TEXTO);
                }
            }
        });
        listaDisponibles.addListSelectionListener(e -> btnEquipar.setEnabled(!listaDisponibles.isSelectionEmpty()));
        btnEquipar.addActionListener(e -> ejecutar(() ->
                controller.equipar(selectorPersonaje.getSelectedIndex(), listaDisponibles.getSelectedIndex()),
                "Objeto equipado."));
        btnQuitarArma.addActionListener(e -> ejecutar(() ->
                controller.desequipar(selectorPersonaje.getSelectedIndex(), TipoObjeto.ARMA), "Arma devuelta al inventario."));
        btnQuitarArmadura.addActionListener(e -> ejecutar(() ->
                controller.desequipar(selectorPersonaje.getSelectedIndex(), TipoObjeto.ARMADURA),
                "Armadura devuelta al inventario."));
        getRootPane().registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void ejecutar(Runnable accion, String confirmacion) {
        try {
            accion.run();
            actualizarPantalla();
            mostrarMensaje(confirmacion, TEXTO);
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarMensaje(e.getMessage(), ERROR);
        }
    }

    private void actualizarPantalla() {
        int personaje = selectorPersonaje.getSelectedIndex();
        EquipableDTO arma = controller.getEquipado(personaje, TipoObjeto.ARMA);
        EquipableDTO armadura = controller.getEquipado(personaje, TipoObjeto.ARMADURA);
        actualizarRanura(arma, lblArma, btnQuitarArma);
        actualizarRanura(armadura, lblArmadura, btnQuitarArmadura);
        lblEstadisticas.setText("Ataque " + controller.getAtaque(personaje) + "  ·  Defensa " + controller.getDefensa(personaje));

        modeloDisponibles.clear();
        for (EquipableDTO item : controller.getEquipables()) {
            modeloDisponibles.addElement(describir(item));
        }
        if (!modeloDisponibles.isEmpty()) {
            listaDisponibles.setSelectedIndex(0);
        }
        btnEquipar.setEnabled(!listaDisponibles.isSelectionEmpty());
    }

    private void actualizarRanura(EquipableDTO item, JLabel etiqueta, BotonRPG boton) {
        etiqueta.setText(item == null ? "Sin equipar" : describir(item));
        etiqueta.setToolTipText(item == null ? null : describir(item));
        boton.setEnabled(item != null);
    }

    private static String describir(EquipableDTO item) {
        String bonus = item.getTipo() == TipoObjeto.ARMA
                ? String.format("%+d ataque", item.getBonusAtaque())
                : String.format("%+d defensa", item.getBonusDefensa());
        return item.getNombre() + "  ·  " + RarezaVisual.nombre(item.getRareza()) + "  ·  " + bonus;
    }

    private void mostrarMensaje(String mensaje, Color color) {
        lblMensaje.setForeground(color);
        lblMensaje.setText(mensaje);
    }

    private static void configurarLista(JList<String> lista, String nombreAccesible) {
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setBackground(FONDO);
        lista.setForeground(TEXTO);
        lista.setSelectionBackground(new Color(91, 69, 32));
        lista.setSelectionForeground(Color.WHITE);
        lista.setFont(new Font("Georgia", Font.PLAIN, 14));
        lista.setFixedCellHeight(32);
        lista.getAccessibleContext().setAccessibleName(nombreAccesible);
    }

    private static JPanel crearPanel(String titulo) {
        JPanel panel = new PanelMarcoDorado(titulo);
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(panel.getBorder(),
                BorderFactory.createEmptyBorder(10, 12, 12, 12)));
        return panel;
    }

    private static JLabel crearEtiqueta(String texto, int tamano, Color color) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("Georgia", Font.PLAIN, tamano));
        etiqueta.setForeground(color);
        return etiqueta;
    }
}
