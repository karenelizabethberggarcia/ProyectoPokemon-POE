package gui;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class VentanaPrincipal extends JFrame {

    private JPanel panelInicio = new JPanel();
    private CardLayout cardLayout = new CardLayout();
    private JPanel contenedor = new JPanel(cardLayout);

    public VentanaPrincipal() {
        setTitle("Pokemon TCG Pocket");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        contenedor.add(panelInicio, "INICIO");
        setContentPane(contenedor);
        
    }
}
