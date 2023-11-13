package gui;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class TabsetGUIContainer extends JPanel {
    Map<String, JPanel> panels = new HashMap<>();


    TabsetGUIContainer(String rootName, JPanel rootPanel) {
        panels.put(rootName, rootPanel);
        add(rootPanel);
    }

    public void addTab(String name, JPanel root) {
        panels.put(name, root);
    }

    public void switchTab(String name) {
        try {
            JPanel ui = panels.get(name);
            if (ui == null)
                throw new NullPointerException();
            removeAll();
            add(ui);
        } catch (NullPointerException e) {
            throw new NullPointerException("No panel with that name or type in this tabset!");
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tabset Notebook");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel maintest = new JPanel();
        maintest.setBackground(Color.BLACK);
        frame.getContentPane().add(new TabsetGUIContainer("start", maintest));
        frame.pack();
        frame.setVisible(true);
    }
}
