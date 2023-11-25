package gui.staff.stock;

import entity.product.Component;
import entity.product.Controller;
import entity.product.Locomotive;
import entity.product.Track;
import utils.GUI;

import static utils.GUI.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

class CreateProductPanel extends JPanel {
    JTextField productCodeInput;
    JTextField nameInput;
    JFormattedTextField stockInput;
    JFormattedTextField priceInput;

    JPanel componentSpecializationPanel;
    ButtonGroup productTypeRadioGroup;
    private JTextField brandInput;
    private JTextField eraInput;
    private JComboBox<Component.Gauge> gaugeInput;

    ButtonGroup componentTypeRadioGroup;
    JPanel trackContainer;
    private JComboBox<Track.Curvature> curvatureInput;
    JPanel locomotiveContainer;
    private JComboBox<Locomotive.PriceBracket> locomotiveInput;
    JPanel controllerContainer;
    private JComboBox<Controller.ControlType> controllerInput;


    public CreateProductPanel() {
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        this.setLayout(gbl);

        JPanel productData = createProductDataPanel(); // common product data
        JPanel productType = createProductTypePanel(); // either component data OR boxedSetContentList

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.3;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(productData, gbc);
        gbc.weightx = 0.7;
        gbc.gridx = 1;
        add(productType, gbc);
    }

    /**
     * The panel for entering any kind of information about a product
     * @return
     */
    private JPanel createProductDataPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
//        panel.setBackground(Color.CYAN);

        panel.setBorder(new EmptyBorder(6, 6, 6, 6));

        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel title = new JLabel("<html><h2>Product Information</h2></html>");
        title.setBorder(new EmptyBorder(0,6,0,0));
        panel.add(title, gbc);
        {
            JLabel productCodeLabel = new JLabel("Product Code");
            productCodeInput = new JTextField();
            JPanel container = createLabelInputRow(productCodeLabel, productCodeInput);
            gbc.gridy++;
            panel.add(container, gbc);
        }
        {
            JLabel nameLabel = new JLabel("Name");
            nameInput = new JTextField();
            JPanel container = createLabelInputRow(nameLabel, nameInput);
            gbc.gridy++;
            panel.add(container, gbc);
        }
        {
            JLabel stockLabel = new JLabel("Initial Stock Level");
            stockInput = new JFormattedTextField(getIntegerFormatter());
            JPanel container = createLabelInputRow(stockLabel, stockInput);
            gbc.gridy++;
            panel.add(container, gbc);
        }
        {
            JLabel priceLabel = new JLabel("Price per Unit");
            priceInput = new JFormattedTextField(getCurrencyFormatter());
            JPanel container = createLabelInputRow(priceLabel, priceInput);
            gbc.gridy++;
            panel.add(container, gbc);
        }
        {
            gbc.gridy++;
            panel.add(createProductSpecialization(), gbc);
        }
        {
            gbc.weighty = 1;
            gbc.gridy++;
            JPanel filler = new JPanel();
            filler.setOpaque(false);
            panel.add(filler, gbc);
        }

        return panel;
    }

    /**
     * Entering the component data and selecting the type or specifying it's a boxedset
     * @return
     */
    private JPanel createProductSpecialization() {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
//        panel.setBackground(Color.MAGENTA);

        panel.setBorder(new EmptyBorder(6, 6, 6, 6));

        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        {
            gbc.gridwidth = 2;
            JLabel title = new JLabel("<html><h3>Product type</h3></html>");
            panel.add(title, gbc);
            gbc.gridwidth = 1;
        }
        {
            gbc.gridy++;
            productTypeRadioGroup = new ButtonGroup();
            JRadioButton component = new JRadioButton("Single Component");
            JRadioButton boxedSet = new JRadioButton("Boxed Set");
            productTypeRadioGroup.add(component);
            productTypeRadioGroup.add(boxedSet);

            component.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            setEnabledRecursively(componentSpecializationPanel, true);
                            // enable correct component type panel
                            switch (Objects.requireNonNull(getSelectedButtonFromGroup(componentTypeRadioGroup))) {
                                case "Track":
                                    GUI.setEnabledRecursively(trackContainer, true);
                                    GUI.setEnabledRecursively(locomotiveContainer, false);
                                    GUI.setEnabledRecursively(controllerContainer, false);
                                    break;
                                case "Locomotive":
                                    GUI.setEnabledRecursively(trackContainer, false);
                                    GUI.setEnabledRecursively(locomotiveContainer, true);
                                    GUI.setEnabledRecursively(controllerContainer, false);
                                    break;
                                case "Controller":
                                    GUI.setEnabledRecursively(trackContainer, false);
                                    GUI.setEnabledRecursively(locomotiveContainer, false);
                                    GUI.setEnabledRecursively(controllerContainer, true);
                                    break;
                            }
                        }
                    });
                }
            });
            boxedSet.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            setEnabledRecursively(componentSpecializationPanel, false);
                        }
                    });
                }
            });

            panel.add(component, gbc);
            gbc.gridx++;
            panel.add(boxedSet, gbc);

            component.setSelected(true);
        }
        gbc.gridwidth = 2;
        {
            gbc.gridy++;
            JPanel blankPanel = new JPanel();
            blankPanel.setBorder(new EmptyBorder(0, 6, 0, 6));
            panel.add(blankPanel, gbc);
        }
        {
            gbc.gridy++;
            gbc.gridx = 0;
            JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
            sep.setBorder(new EmptyBorder(0, 6, 0, 6));
            panel.add(sep, gbc);
        }
        {
            gbc.gridy++;
            gbc.gridx = 0;

            componentSpecializationPanel = createComponentPanel();
            panel.add(componentSpecializationPanel, gbc);
        }

        return panel;
    }

    private JPanel createComponentPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
//        panel.setBackground(Color.PINK);

        panel.setBorder(new EmptyBorder(16, 6, 6, 6));

        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // component field pairs
        // radio list for component type
        gbc.gridwidth = 2;
        {
            JLabel brandLabel = new JLabel("Brand");
            brandInput = new JTextField();
            JPanel container = createLabelInputRow(brandLabel, brandInput);
            gbc.gridy++;
            panel.add(container, gbc);
        }
        {
            JLabel eraLabel = new JLabel("Era");
            eraInput = new JTextField();
            JPanel container = createLabelInputRow(eraLabel, eraInput);
            gbc.gridy++;
            panel.add(container, gbc);
        }
        {
            JLabel gaugeLabel = new JLabel("Gauge");
            gaugeInput = new JComboBox<>(Component.Gauge.values());
            JPanel container = createLabelInputRow(gaugeLabel, gaugeInput);
            gbc.gridy++;
            panel.add(container, gbc);
        }
        gbc.gridwidth = 1;
        {
            gbc.gridy++;
            JLabel typeLabel = new JLabel("Select a component type");
            typeLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
            panel.add(typeLabel, gbc);
        }
        { // special component type and data
            componentTypeRadioGroup = new ButtonGroup();
            JRadioButton track = new JRadioButton("Track");
            JRadioButton locomotive = new JRadioButton("Locomotive");
            JRadioButton controller = new JRadioButton("Controller");
            componentTypeRadioGroup.add(track);
            componentTypeRadioGroup.add(locomotive);
            componentTypeRadioGroup.add(controller);

            gbc.gridy++;
            panel.add(track, gbc);
            {
                gbc.gridx++;
                JLabel curveLabel = new JLabel("Curvature");
                curvatureInput = new JComboBox<>(Track.Curvature.values());
                trackContainer = createLabelInputRow(curveLabel, curvatureInput);
                panel.add(trackContainer, gbc);
            }

            gbc.gridx = 0;
            gbc.gridy++;
            panel.add(locomotive, gbc);
            {
                gbc.gridx++;
                JLabel priceLabel = new JLabel("Price Bracket");
                locomotiveInput = new JComboBox<>(Locomotive.PriceBracket.values());
                locomotiveContainer = createLabelInputRow(priceLabel, locomotiveInput);
                panel.add(locomotiveContainer, gbc);
            }

            gbc.gridx = 0;
            gbc.gridy++;
            panel.add(controller, gbc);
            {
                gbc.gridx++;
                JLabel controllerLabel = new JLabel("Price Bracket");
                controllerInput = new JComboBox<>(Controller.ControlType.values());
                controllerContainer = createLabelInputRow(controllerLabel, controllerInput);
                panel.add(controllerContainer, gbc);
            }

            track.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            GUI.setEnabledRecursively(trackContainer, true);
                            GUI.setEnabledRecursively(locomotiveContainer, false);
                            GUI.setEnabledRecursively(controllerContainer, false);
                        }
                    });
                }
            });
            locomotive.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            GUI.setEnabledRecursively(trackContainer, false);
                            GUI.setEnabledRecursively(locomotiveContainer, true);
                            GUI.setEnabledRecursively(controllerContainer, false);
                        }
                    });
                }
            });
            controller.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            GUI.setEnabledRecursively(trackContainer, false);
                            GUI.setEnabledRecursively(locomotiveContainer, false);
                            GUI.setEnabledRecursively(controllerContainer, true);
                        }
                    });
                }
            });

            // initial selection
            track.setSelected(true);
            GUI.setEnabledRecursively(trackContainer, true);
            GUI.setEnabledRecursively(locomotiveContainer, false);
            GUI.setEnabledRecursively(controllerContainer, false);
        }

        return panel;
    }

    /**
     * Either a panel entering the specific specialization data or looking up stuff to add to the boxedset
     * @return
     */
    private JPanel createProductTypePanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.PINK);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel title = new JLabel("<html><h3>Boxed Set Contents</h3></html>");
        title.setBorder(new EmptyBorder(0,6,0,0));
        panel.add(title, gbc);

        {
            gbc.weighty = 1;
            gbc.gridy = 5;
            JPanel filler = new JPanel();
            filler.setOpaque(false);
            panel.add(filler, gbc);
        }

        return panel;
    }
}
