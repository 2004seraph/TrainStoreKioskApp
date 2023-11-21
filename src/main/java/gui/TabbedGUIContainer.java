package gui;
import controllers.AppContext;
import org.javatuples.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;


public class TabbedGUIContainer extends JPanel {
    public interface ScreenRequirement {
        public boolean canOpen();
    }
    public interface TabPanel {
        public void setNotebookContainer(TabbedGUIContainer cont);
    }

    private static final int TAB_BUTTON_MARGIN = 5;

    private final JPanel tabContainer = new JPanel();
    private final JPanel tabButtonList = new JPanel(new GridBagLayout());
    private GridBagConstraints tabButtonConstraints;

    private final JPanel contentContainer = new JPanel();
    private GridBagConstraints contentConstraints;

    private final Map<String, Triplet<JPanel, ScreenRequirement, JButton>> panels = new HashMap<>();

    private String currentTab = null;

    private void initPanel(float splitRatio) {
        // ensures that each screen fills the space

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        this.setLayout(gbl);
        this.contentContainer.setLayout(gbl);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.01;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;

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
        resetTabButtonDisplay();
    }

    /**
     * Sets up the layout settings for the tab sidebar and the child buttons
     */
    private void initTabButtonContainer() {
        this.tabContainer.setLayout(new BorderLayout());

        this.tabButtonConstraints = new GridBagConstraints();
        this.tabButtonConstraints.insets = new Insets(TAB_BUTTON_MARGIN,TAB_BUTTON_MARGIN,TAB_BUTTON_MARGIN,TAB_BUTTON_MARGIN);
        this.tabButtonConstraints.gridwidth = GridBagConstraints.REMAINDER;
        this.tabButtonConstraints.fill = GridBagConstraints.HORIZONTAL;
        this.tabButtonConstraints.weightx = 1;
        this.tabButtonConstraints.weighty = 1;
    }

    private void resetTabButtonDisplay() {
        // this function makes the buttons display in a nice list from the top within the sidebar
        this.tabContainer.removeAll();
        this.tabButtonList.removeAll();

        this.tabButtonConstraints.weightx = 1;
        this.tabButtonConstraints.weighty = 1;
        tabButtonList.add(new JPanel(), this.tabButtonConstraints);
        this.tabContainer.add(new JScrollPane(tabButtonList));
    }

    private void enableAllButtons() {
        for (Triplet<JPanel, ScreenRequirement, JButton> panel : panels.values()) {
            panel.getValue2().setEnabled(true);
        }
    }

    public TabbedGUIContainer(float splitRatio) {
        this.initPanel(splitRatio);
    }

    /**
     * Adds a new panel option to this GUI
     * @param name The UNIQUELY IDENTIFYING name for this panel, also the text for the button
     * @param root The root JPanel of this specific page, containing it completely
     * @param constraints A predicate function that runs a check if a user can open this screen (for controller logic)
     */
    public void insertTab(String name, JPanel root, ScreenRequirement constraints) {
        // do not add a panel with the same name as another
        if (!panels.containsKey(name)) {
            throw new IllegalArgumentException("[GUITabs] You cannot have two screens with the same name.");
        }

        JButton tb = new JButton(name);
        tb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchTab(name);
                enableAllButtons();
                tb.setEnabled(false);
            }
        });
        this.tabButtonConstraints.weighty = 0;
        this.tabButtonList.add(tb, this.tabButtonConstraints, 0);

        panels.put(name, Triplet.with(root, constraints, tb));

        if (Arrays.asList(root.getClass().getInterfaces()).contains(TabPanel.class)) {
            ((TabPanel)root).setNotebookContainer(this);
        }

        if (!panels.containsKey(currentTab)) {
            switchTab(name);
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

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    contentContainer.removeAll();
                    contentContainer.add(ui, contentConstraints);
                    enableAllButtons();
                    panels.get(name).getValue2().setEnabled(false);

                    revalidate();
                    repaint();
                }
            });

            this.currentTab = name;

        } catch (NullPointerException e) {
            throw new NullPointerException("[GUITabs] No panel with that name or type in this tabset!");
        }
    }

    /**
     * Resets the internal state of the tab manager
     */
    public void removeAllTabs() {
        this.panels.clear();
        resetTabButtonDisplay();

        this.revalidate();
        this.repaint();
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

    // VVV DO NOT USE THIS CODE VVV

//    public static void main(String[] args) {
//        JFrame window = AppContext.getWindow();
//      DO NOT USE THIS CODE
//        TabbedGUIContainer screenManager = new TabbedGUIContainer(0.2f);
//        window.add(screenManager);
//
//        state1(screenManager);
//
//        window.setVisible(true);
//    }
//
//      DO NOT USE THIS CODE
//
//    private static void state1(TabbedGUIContainer sc) {
//        sc.removeAllTabs();
//
//        JPanel blackWindow = new JPanel();
//        blackWindow.setBackground(Color.MAGENTA);
//        JPanel blueWindow = new JPanel();
//        blueWindow.setBackground(Color.BLUE);
//        JButton loginButton = new JButton("Go to new area");
//        blueWindow.add(loginButton);
//          DO NOT USE THIS CODE
//        loginButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                SwingUtilities.invokeLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        state2(sc);
//                    }
//                });
//            }
//        });
//          DO NOT USE THIS CODE
//        sc.insertTab("MAGENTA", blackWindow, new TabbedGUIContainer.ScreenRequirement() {
//            @Override
//            public boolean canOpen() {
//                return true;
//            }
//        });
//        sc.insertTab("BLUE", blueWindow, new TabbedGUIContainer.ScreenRequirement() {
//            @Override
//            public boolean canOpen() {
//                return true;
//            }
//        });
//
//        sc.switchTab("MAGENTA");
//    }
//      DO NOT USE THIS CODE
//    public static void state2(TabbedGUIContainer sc) {
//        sc.removeAllTabs();
//
//        JPanel blackWindow = new JPanel();
//        blackWindow.setBackground(Color.BLACK);
//        JPanel blueWindow = new JPanel();
//        blueWindow.setBackground(Color.CYAN);
//
//        sc.insertTab("blue", blackWindow, new TabbedGUIContainer.ScreenRequirement() {
//            @Override
//            public boolean canOpen() {
//                return true;
//            }
//        });
//        sc.insertTab("blue", blueWindow, new TabbedGUIContainer.ScreenRequirement() {
//            @Override
//            public boolean canOpen() {
//                return true;
//            }
//        });
//          DO NOT USE THIS CODE
////        sc.switchTab("black");
//    }
}
