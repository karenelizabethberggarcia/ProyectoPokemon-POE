package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Desktop.Action;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PanelTransicion extends JPanel {

    private final JLabel mensaje = new JLabel(" ", SwingConstants.CENTER);
    private final JButton continuar = new JButton("Continuar");

    public PanelTransicion(VentanaPrincipal ventana) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Cambio de turno ", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 34));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        mensaje.setFont(new Font("SansSerif", Font.PLAIN, 24));
        mensaje.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel aviso = new JLabel("Pasa al siguiente jugador", SwingConstants.CENTER);
        aviso.setFont(new Font("SansSerif", Font.PLAIN, 14));
        aviso.setAlignmentX(Component.CENTER_ALIGNMENT);

        continuar.setFont(new Font("SansSerif", Font.BOLD, 20));
        continuar.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        add(Box.createVerticalGlue());
        add(titulo);
        add(Box.createRigidArea(new Dimension(0, 25)));
        add(mensaje);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(aviso);
        add(Box.createRigidArea(new Dimension(0, 35)));
        add(continuar);
        add(Box.createVerticalGlue());
        
    }

    public void mostrar(String texto, Runnable alContinuar){
        mensaje.setText(texto);
        for(ActionListener o : continuar.getActionListeners()){
            continuar.removeActionListener(o);
        }
        continuar.setEnabled(true);
        continuar.addActionListener(e -> {
            continuar.setEnabled(false);
            alContinuar.run();
        });
    }
}
