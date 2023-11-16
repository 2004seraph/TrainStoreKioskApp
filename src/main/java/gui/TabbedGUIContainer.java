package gui;
import controllers.AppContext;

import javax.crypto.KeyGenerator;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;



public class TabbedGUIContainer extends JPanel {
    public interface ScreenRequirement {
        public boolean canOpen();
    }

    JPanel tabContainer = new JPanel();
    JPanel contentContainer = new JPanel();

    private JPanel tabButtonList;

    GridBagConstraints contentConstraints;

    Map<String, Map.Entry<JPanel, ScreenRequirement>> panels = new HashMap<>();

    private void initPanel(float splitRatio) {
        // ensures that each screen fills the space
        this.setBackground(Color.MAGENTA);

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        this.setLayout(gbl);
        this.contentContainer.setLayout(gbl);
        gbc.fill = GridBagConstraints.BOTH;

        tabContainer.setBackground(Color.BLUE);
        contentContainer.setBackground(Color.CYAN);

        gbc.weighty = 1;
        gbc.gridy = 0;

        gbc.weightx = splitRatio;
        gbc.gridx = 0;
        gbl.setConstraints(tabContainer, gbc);
        this.add(tabContainer, gbc);

        gbc.weightx = 1;
        gbc.gridx = 1;
        gbl.setConstraints(contentContainer, gbc);
        this.add(contentContainer, gbc);

        this.contentConstraints = new GridBagConstraints();
        this.contentConstraints.fill = GridBagConstraints.BOTH;
        this.contentConstraints.weighty = 1;
        this.contentConstraints.weightx = 1;

        initTabButtonContainer();
    }

    private void initTabButtonContainer() {
        // this function creates a simple listed view layout of the buttons to switch tabs
        this.tabContainer.setLayout(new BorderLayout());

        tabButtonList = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        tabButtonList.add(new JPanel(), gbc);

        this.tabContainer.add(new JScrollPane(tabButtonList));
    }

    TabbedGUIContainer(float splitRatio) {
        this.initPanel(splitRatio);
    }

    public void insertTab(String name, JPanel root, ScreenRequirement constraints) {
        panels.put(name, Map.entry(root, constraints));

        JButton tb = new JButton(name);
        tb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchTab(name);
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        this.tabButtonList.add(tb, gbc, 0);

        revalidate();
        repaint();
    }

    public void switchTab(String name) {
        try {
            JPanel ui = panels.get(name).getKey();
            if (ui == null)
                throw new NullPointerException();
            this.contentContainer.removeAll();
            this.contentContainer.add(ui, this.contentConstraints);
        } catch (NullPointerException e) {
            throw new NullPointerException("No panel with that name or type in this tabset!");
        }

        this.contentContainer.revalidate();
        this.contentContainer.repaint();
    }

    public static void main(String[] args) {
        JFrame frame = AppContext.getWindow();

        JPanel maintest = new JPanel();
        maintest.setBackground(Color.BLACK);
        JPanel maintest2 = new JPanel();
        maintest2.setBackground(Color.GRAY);

        TabbedGUIContainer dashboards = new TabbedGUIContainer(0.2f);
        dashboards.insertTab("Main", maintest, new ScreenRequirement() {
            @Override
            public boolean canOpen() {
                return true;
            }
        });
        dashboards.insertTab("Main2", maintest2, new ScreenRequirement() {
            @Override
            public boolean canOpen() {
                return true;
            }
        });
        dashboards.switchTab("Main");

        frame.getContentPane().add(dashboards);
        frame.pack();
        frame.setVisible(true);
    }
}
