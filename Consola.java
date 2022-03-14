import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Consola extends JFrame {

    private JLabel label;

    public JLabel getLabel() {
        return label;
    }

    public void setLabel(JLabel label) {
        this.label = label;
    }

    public Consola() {
        this.setSize(800, 500);
        this.setTitle("Angel's Console");
        // creo mi main panel
        JPanel mainPanel = new JPanel(null);
        mainPanel.setSize(800, 500);
        mainPanel.setBackground(Color.BLACK);
        label = new JLabel();
        label.setBounds(0, 0, 800, 500);
        label.setBackground(Color.BLACK);
        label.setForeground(Color.WHITE);
        label.setVerticalAlignment(JLabel.TOP);
        label.setFont(label.getFont().deriveFont(15f));
        mainPanel.add(label);
        this.getContentPane().add(mainPanel);
    }

}