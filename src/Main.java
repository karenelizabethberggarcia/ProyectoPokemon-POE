import javax.swing.SwingUtilities;

import gui.VentanaPrincipal;

public class Main {

    public static void main(String[] args) {
       SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
