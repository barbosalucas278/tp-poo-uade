package view.componentes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.KeyStroke;

import model.DTOs.CombatienteDTO;

/** Una ficha seleccionable de la formación, sin lógica de combate. */
public class PanelCombatienteBatalla extends JButton {

    private static final long serialVersionUID = 1L;
    private static final Color DORADO = new Color(212, 175, 85);
    private static final Color TEXTO = new Color(244, 236, 218);
    private static final Color TEXTO_SECUNDARIO = new Color(193, 181, 160);
    private static final Color VIDA = new Color(155, 48, 48);
    private static final Color RECURSO = new Color(44, 104, 151);
    private static final Color ENERGIA = new Color(76, 122, 53);

    private final CombatienteDTO combatiente;
    private final boolean enemigo;
    private final Image retrato;

    public PanelCombatienteBatalla(CombatienteDTO combatiente, boolean enemigo) {
        this.combatiente = Objects.requireNonNull(combatiente, "El combatiente es obligatorio.");
        this.enemigo = enemigo;
        this.retrato = cargarRetrato(combatiente.getRutaImagen());

        setPreferredSize(new Dimension(170, 190));
        setMinimumSize(new Dimension(115, 155));
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setActionCommand(combatiente.getNombre());
        String descripcion = combatiente.getNombre() + " — " + combatiente.getClase()
                + ". HP " + combatiente.getVidaActual() + "/" + combatiente.getVidaMax()
                + (combatiente.getRecursoMax() > 0
                        ? ", " + combatiente.getNombreRecurso() + " "
                                + combatiente.getRecursoActual() + "/" + combatiente.getRecursoMax()
                        : "")
                + ", energía " + combatiente.getEnergiaActual() + "/" + combatiente.getEnergiaMax()
                + (retrato == null ? ". Ilustración pendiente." : "");
        setToolTipText(descripcion);
        getAccessibleContext().setAccessibleName(combatiente.getNombre() + ", " + combatiente.getClase());
        getAccessibleContext().setAccessibleDescription(descripcion + " Presioná espacio o Enter para seleccionar.");
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "seleccionarCombatiente");
        getActionMap().put("seleccionarCombatiente", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                doClick();
            }
        });
    }

    public CombatienteDTO getCombatiente() {
        return combatiente;
    }

    public void setSeleccionado(boolean seleccionado) {
        setSelected(seleccionado);
        repaint();
    }

    public boolean isSeleccionado() {
        return isSelected();
    }

    private Image cargarRetrato(String ruta) {
        if (ruta == null || ruta.trim().isEmpty()) {
            return null;
        }
        URL recurso = getClass().getResource(ruta.startsWith("/") ? ruta : "/" + ruta);
        return recurso == null ? null : new ImageIcon(recurso).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int ancho = getWidth();
        int alto = getHeight();
        if (ancho < 25 || alto < 50) {
            g2.dispose();
            return;
        }

        boolean resaltado = isSeleccionado() || hasFocus();
        g2.setPaint(new GradientPaint(0, 0, new Color(21, 26, 26, 218),
                0, alto, new Color(11, 17, 18, 235)));
        g2.fillRoundRect(2, 2, ancho - 4, alto - 4, 14, 14);
        if (getModel().isRollover() && isEnabled()) {
            g2.setColor(new Color(212, 175, 85, 15));
            g2.fillRoundRect(2, 2, ancho - 4, alto - 4, 14, 14);
        }
        g2.setStroke(new BasicStroke(resaltado ? 2f : 1f));
        g2.setColor(resaltado ? DORADO : new Color(128, 116, 87, 100));
        g2.drawRoundRect(2, 2, ancho - 5, alto - 5, 14, 14);

        boolean compacto = alto <= 190;
        int margen = compacto ? (ancho < 140 ? 6 : 8) : (ancho < 140 ? 7 : 10);
        int anchoContenido = ancho - margen * 2;
        int tamNombre = ancho < 140 ? 12 : 14;
        dibujarTextoCentrado(g2, combatiente.getNombre(), margen, compacto ? 6 : 8, anchoContenido,
                new Font("Georgia", Font.BOLD, tamNombre), TEXTO);
        if (!compacto) {
            dibujarTextoCentrado(g2, combatiente.getClase(), margen, 8 + tamNombre + 3, anchoContenido,
                    new Font("SansSerif", Font.PLAIN, 11), TEXTO_SECUNDARIO);
        }

        int cantidadBarras = combatiente.getRecursoMax() > 0 ? 3 : 2;
        int altoBarra = compacto ? 14 : (alto >= 240 ? 20 : 16);
        int espacioBarras = compacto ? 2 : 3;
        int altoEstados = cantidadBarras * altoBarra + (cantidadBarras - 1) * espacioBarras;
        int inicioBarras = alto - margen - altoEstados;
        // La clase sigue disponible en el tooltip y en la descripción accesible.
        // Al compactar el encabezado se conserva espacio para el retrato completo.
        int inicioRetrato = compacto ? 26 : 40;
        int altoRetrato = Math.max(12, inicioBarras - inicioRetrato - (compacto ? 4 : 6));
        dibujarRetrato(g2, margen, inicioRetrato, anchoContenido, altoRetrato);

        dibujarBarra(g2, "HP", combatiente.getVidaActual(), combatiente.getVidaMax(), VIDA,
                margen, inicioBarras, anchoContenido, altoBarra);
        int yBarra = inicioBarras + altoBarra + espacioBarras;
        if (combatiente.getRecursoMax() > 0) {
            dibujarBarra(g2, combatiente.getNombreRecurso(), combatiente.getRecursoActual(),
                    combatiente.getRecursoMax(), RECURSO, margen, yBarra, anchoContenido, altoBarra);
            yBarra += altoBarra + espacioBarras;
        }
        dibujarBarra(g2, "EN", combatiente.getEnergiaActual(), combatiente.getEnergiaMax(), ENERGIA,
                margen, yBarra, anchoContenido, altoBarra);
        if (!isEnabled()) {
            g2.setColor(new Color(10, 13, 15, 105));
            g2.fillRoundRect(2, 2, ancho - 4, alto - 4, 14, 14);
        }
        g2.dispose();
    }

    private void dibujarRetrato(Graphics2D g2, int x, int y, int ancho, int alto) {
        int anchoImagen = retrato == null ? -1 : retrato.getWidth(this);
        int altoImagen = retrato == null ? -1 : retrato.getHeight(this);
        if (anchoImagen > 0 && altoImagen > 0) {
            double escala = Math.min((double) ancho / anchoImagen, (double) alto / altoImagen);
            int destinoAncho = Math.max(1, (int) Math.round(anchoImagen * escala));
            int destinoAlto = Math.max(1, (int) Math.round(altoImagen * escala));
            int destinoX = x + (ancho - destinoAncho) / 2;
            int destinoY = y + (alto - destinoAlto) / 2;
            Shape clipAnterior = g2.getClip();
            g2.clip(new RoundRectangle2D.Double(destinoX, destinoY, destinoAncho, destinoAlto, 8, 8));
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(retrato, destinoX, destinoY, destinoAncho, destinoAlto, this);
            g2.setClip(clipAnterior);
            return;
        }

        // Fallback para combatientes sin un sprite disponible.
        int diametro = Math.max(12, Math.min(ancho - 12, alto - 4));
        int medallonX = x + (ancho - diametro) / 2;
        int medallonY = y + (alto - diametro) / 2;
        g2.setColor(new Color(0, 0, 0, 65));
        g2.fillOval(medallonX + diametro / 8, medallonY + diametro - 3, diametro * 3 / 4, 5);
        g2.setPaint(new GradientPaint(medallonX, medallonY,
                enemigo ? new Color(63, 42, 47) : new Color(37, 58, 61),
                medallonX, medallonY + diametro, new Color(18, 24, 28)));
        g2.fillOval(medallonX, medallonY, diametro, diametro);
        g2.setColor(new Color(157, 135, 90, 155));
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawOval(medallonX + 1, medallonY + 1, diametro - 2, diametro - 2);
        g2.setColor(new Color(142, 132, 124, 50));
        int cabeza = diametro / 4;
        g2.fillOval(medallonX + (diametro - cabeza) / 2, medallonY + diametro / 5, cabeza, cabeza);
        g2.fillArc(medallonX + diametro / 5, medallonY + diametro * 2 / 5,
                diametro * 3 / 5, diametro * 3 / 5, 0, 180);
        dibujarTextoCentrado(g2, iniciales(combatiente.getNombre()), medallonX,
                medallonY + diametro / 2 - Math.max(11, diametro / 4) / 2,
                diametro, new Font("Georgia", Font.BOLD, Math.max(11, diametro / 4)), TEXTO_SECUNDARIO);
    }

    private void dibujarBarra(Graphics2D g2, String etiqueta, int actual, int maximo,
            Color color, int x, int y, int ancho, int alto) {
        g2.setColor(new Color(23, 24, 24));
        g2.fillRoundRect(x, y, ancho, alto, 6, 6);
        double proporcion = maximo <= 0 ? 0 : Math.max(0, Math.min(1, (double) actual / maximo));
        int relleno = (int) Math.round((ancho - 2) * proporcion);
        if (relleno > 0) {
            g2.setPaint(new GradientPaint(x, y, color.brighter(), x, y + alto, color));
            g2.fillRoundRect(x + 1, y + 1, relleno, alto - 2, 5, 5);
        }
        g2.setStroke(new BasicStroke(1f));
        g2.setColor(new Color(155, 133, 91, 150));
        g2.drawRoundRect(x, y, ancho - 1, alto - 1, 6, 6);
        String texto = etiqueta + " " + actual + "/" + maximo;
        Font fuente = new Font("SansSerif", Font.BOLD, 11);
        g2.setFont(fuente);
        FontMetrics fm = g2.getFontMetrics();
        texto = ajustarTexto(texto, fm, ancho - 6);
        int textoX = x + (ancho - fm.stringWidth(texto)) / 2;
        int textoY = y + (alto - fm.getHeight()) / 2 + fm.getAscent();
        g2.setColor(new Color(0, 0, 0, 190));
        g2.drawString(texto, textoX + 1, textoY + 1);
        g2.setColor(Color.WHITE);
        g2.drawString(texto, textoX, textoY);
    }

    private void dibujarTextoCentrado(Graphics2D g2, String texto, int x, int y, int ancho,
            Font fuente, Color color) {
        g2.setFont(fuente);
        FontMetrics fm = g2.getFontMetrics();
        String visible = ajustarTexto(texto, fm, ancho);
        g2.setColor(color);
        g2.drawString(visible, x + (ancho - fm.stringWidth(visible)) / 2, y + fm.getAscent());
    }

    private String ajustarTexto(String texto, FontMetrics fm, int ancho) {
        String visible = texto == null ? "" : texto;
        if (fm.stringWidth(visible) <= ancho) {
            return visible;
        }
        while (!visible.isEmpty() && fm.stringWidth(visible + "…") > ancho) {
            visible = visible.substring(0, visible.length() - 1);
        }
        return visible + "…";
    }

    private String iniciales(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "?";
        }
        String[] palabras = nombre.trim().split("\\s+");
        String primera = palabras[0].substring(0, 1);
        return (palabras.length > 1 ? primera + palabras[palabras.length - 1].substring(0, 1)
                : primera).toUpperCase(java.util.Locale.ROOT);
    }
}
