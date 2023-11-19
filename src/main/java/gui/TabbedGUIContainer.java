package gui;
import controllers.AppContext;
import org.javatuples.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import gui.*;


public class TabbedGUIContainer extends JPanel {
    public interface ScreenRequirement {
        public boolean canOpen();
    }
    public interface TabPanel {
        public void setNotebookContainer(TabbedGUIContainer cont);
    }

    private static final int TAB_BUTTON_MARGIN = 5;

    private JPanel tabContainer = new JPanel();
    private JPanel tabButtonList;
    private GridBagConstraints tabButtonConstraints;

    private JPanel contentContainer = new JPanel();
    private GridBagConstraints contentConstraints;

    private final Map<String, Triplet<JPanel, ScreenRequirement, JButton>> panels = new HashMap<>();

    private void initPanel(float splitRatio) {
        // ensures that each screen fills the space
//        this.setBackground(Color.MAGENTA);

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        this.setLayout(gbl);
        this.contentContainer.setLayout(gbl);
        gbc.fill = GridBagConstraints.BOTH;

//        tabContainer.setBackground(Color.BLUE);
//        contentContainer.setBackground(Color.CYAN);

        gbc.weighty = 1;
        gbc.gridy = 0;

        gbc.weightx = 0.01;
        gbc.gridx = 0;
        gbl.setConstraints(tabContainer, gbc);
        this.add(tabContainer, gbc);

        gbc.weightx = splitRatio;
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
//        this.tabContainer.setMaximumSize(new Dimension(23, 23));

        this.tabButtonConstraints = new GridBagConstraints();
        this.tabButtonConstraints.insets = new Insets(TAB_BUTTON_MARGIN,TAB_BUTTON_MARGIN,TAB_BUTTON_MARGIN,TAB_BUTTON_MARGIN);
        this.tabButtonConstraints.gridwidth = GridBagConstraints.REMAINDER;
        this.tabButtonConstraints.weightx = 1;
        this.tabButtonConstraints.weighty = 1;
        this.tabButtonConstraints.fill = GridBagConstraints.HORIZONTAL;

        tabButtonList = new JPanel(new GridBagLayout());
        tabButtonList.add(new JPanel(), this.tabButtonConstraints);

        this.tabContainer.add(new JScrollPane(tabButtonList));

        // for the buttons
        this.tabButtonConstraints.weighty = 0;
    }

    private void enableAllButtons() {
        for (Triplet<JPanel, ScreenRequirement, JButton> panel : panels.values()) {
            panel.getValue2().setEnabled(true);
        }
    }

    TabbedGUIContainer(float splitRatio) {
        this.initPanel(splitRatio);
    }

    /**
     * Adds a new panel option to this GUI
     * @param name The UNIQUELY IDENTIFYING name for this panel, also the text for the button
     * @param root The root JPanel of this specific page, containing it completely
     * @param constraints A predicate function that runs a check if a user can open this screen (for controller logic)
     */
    public void insertTab(String name, JPanel root, ScreenRequirement constraints) {

        JButton tb = new JButton(name);
        tb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchTab(name);
                enableAllButtons();
                tb.setEnabled(false);
            }
        });
        this.tabButtonList.add(tb, this.tabButtonConstraints, 0);

        panels.put(name, Triplet.with(root, constraints, tb));

        if (Arrays.asList(root.getClass().getInterfaces()).contains(TabPanel.class)) {
            ((TabPanel)root).setNotebookContainer(this);
        }
        revalidate();
        repaint();
    }

    /**
     * Adds a division in the button tab list for grouping pages
     */
    public void insertDivider() {
        this.tabButtonList.add(new JSeparator(SwingConstants.HORIZONTAL), this.tabButtonConstraints, 0);
    }

    /**
     * Changes the currently active screen tab
     * @param name Must be a panel that has already been registered
     */
    public void switchTab(String name) {
        try {
            JPanel ui = panels.get(name).getValue0();
            if (ui == null)
                throw new NullPointerException();
            this.contentContainer.removeAll();
            this.contentContainer.add(ui, this.contentConstraints);
            enableAllButtons();
            panels.get(name).getValue2().setEnabled(false);
        } catch (NullPointerException e) {
            throw new NullPointerException("No panel with that name or type in this tabset!");
        }

        this.contentContainer.revalidate();
        this.contentContainer.repaint();
    }

    /**
     * For any extra controller logic you want to add to the screen buttons
     * @return Returns a mapping of a screen name to its button object
     */
    public Map<String, JButton> getTabButtons() {
        List<String> panelNames = panels.keySet().stream().toList();
        List<JButton> panelButtons = panels.values().stream().map(Triplet::getValue2).toList();
        Map<String, JButton> res = new HashMap<>();
        IntStream.range(0, panelNames.size()).boxed().forEach(i -> res.put(panelNames.get(i), panelButtons.get(i)));
        return res;
    }

    public static void generateLoginRegister() {
        JFrame frame = AppContext.getWindow();

        JPanel registertest = new Register();
//        registertest.setBackground(Color.BLACK);
        JPanel logintest = new Login();
//        logintest.setBackground(Color.GRAY);

        TabbedGUIContainer dashboards = new TabbedGUIContainer(0.2f);
        dashboards.insertTab("Register", registertest, new ScreenRequirement() {
            @Override
            public boolean canOpen() {
                return true;
            }
        });
        dashboards.insertDivider();
        dashboards.insertTab("Login", logintest, new ScreenRequirement() {
            @Override
            public boolean canOpen() {
                return true;
            }
        });
        dashboards.switchTab("Login");

        frame.getContentPane().add(dashboards);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    public static void generateDashboard() {
        JFrame frame = AppContext.getWindow();
//        destory the current window
        frame.getContentPane().removeAll();

        JPanel profile = new Profile();

        TabbedGUIContainer dashboards = new TabbedGUIContainer(0.2f);

        dashboards.insertTab("Profile", profile, new ScreenRequirement() {
            @Override
            public boolean canOpen() {
                return true;
            }
        });

        dashboards.switchTab("Profile");

//        frame.getContentPane().add(dashboards);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

}
