package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PanelFin extends JPanel {
    private final JLabel ganador = new JLabel(" ", SwingConstants.CENTER);
    private final JLabel motivo = new JLabel(" ", SwingConstants.CENTER);

    public PanelFin(VentanaPrincipal ventana){
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JLabel titulo = new JLabel("FIN DEL DUELO", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 34));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        ganador.setFont(new Font("SansSerif", Font.BOLD, 26));
        ganador.setAlignmentX(Component.CENTER_ALIGNMENT);

        motivo.setFont(new Font("SansSerif", Font.PLAIN, 18));
        motivo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton otraPartida = new JButton("Jugar otra partida");
        otraPartida.setFont(new Font("SansSerif", Font.BOLD, 18));
        otraPartida.addActionListener(e -> ventana.mostrarInicio());

        JButton salir = new JButton("Salir");
        salir.setFont(new Font("SansSerif", Font.BOLD, 18));
        salir.addActionListener(e -> System.exit(0));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        botones.add(otraPartida);
        botones.add(salir);
        botones.setAlignmentX(Component.CENTER_ALIGNMENT);
        botones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        add(Box.createVerticalGlue());
        add(titulo);
        add(Box.createRigidArea(new Dimension(0, 25)));
        add(ganador);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(motivo);
        add(Box.createRigidArea(new Dimension(0, 40)));
        add(botones);
        add(Box.createVerticalGlue());

    }

    public void mostrarResultados(String nombreGanador, String textoMorivo){
        ganador.setText(nombreGanador + " se convierte en el vencedor!");
        motivo.setText(textoMorivo);
    }
    
}
