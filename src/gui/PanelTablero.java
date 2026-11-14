package gui;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

// para ver el registro de la partida en tiempo real, se crea un panel con un area de texto que se actualiza constantemente.
public class PanelTablero extends JPanel {

    public PanelTablero(VentanaPrincipal ventana, JTextArea areaRegistro) {
        setLayout(new BorderLayout());
        areaRegistro.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        add(new JScrollPane(areaRegistro), BorderLayout.CENTER);
    }
}