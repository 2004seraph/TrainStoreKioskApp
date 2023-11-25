package gui.staff.stock;

import controllers.AppContext;
import db.DatabaseBridge;
import entity.product.Component;
import entity.product.Controller;
import entity.product.Locomotive;
import entity.product.Track;
import utils.GUI;

import static utils.GUI.*;
import static utils.GUI.getSelectedButtonFromGroup;
import static utils.Java.removeMatrixColumn;
import static utils.Java.setMatrixColumn;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class CreateProductPanel extends JPanel {
    JButton newProductButton;
    JButton finishProductButton;
    JPanel productData;
    JTextField productCodeInput;
    JTextField nameInput;
    JFormattedTextField stockInput;
    JFormattedTextField priceInput;

    JPanel componentSpecializationPanel;
    ButtonGroup productTypeRadioGroup;

    static final String componentText = "Single Component";
    static final String boxedSetText = "Boxed Set";

    // component fields
    private JTextField brandInput;
    private JTextField eraInput;
    private JComboBox<Component.Gauge> gaugeInput;
    ButtonGroup componentTypeRadioGroup;

    static final String trackText = "Track";
    static final String locomotiveText = "Locomotive";
    static final String controllerText = "Controller";

    JPanel trackContainer;
    private JComboBox<Track.Curvature> curvatureInput;
    JPanel locomotiveContainer;
    private JComboBox<Locomotive.PriceBracket> locomotiveInput;
    JPanel controllerContainer;
    private JComboBox<Controller.ControlType> controllerInput;

    JPanel boxedSetBuilder;
    JTable boxedSetContentSelection;
    String[] buildBoxedSetColumns = new String[]{"ID", "Product", "Amount"};
    Object[][] productDataSet;

    Runnable alterEvent;

    public CreateProductPanel(Object[][] productDataSet, Runnable alterEvent) {
        this.productDataSet = setMatrixColumn(removeMatrixColumn(productDataSet, 2), 2, 0);
        this.alterEvent = alterEvent;

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        this.setLayout(gbl);

        productData = createProductDataPanel(); // common product data
        boxedSetBuilder = createComposeBoxedSetPanel(); // either component data OR boxedSetContentList

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        newProductButton = new JButton("Create New Product");
        newProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setProductCreationEnabled(true);
            }
        });
        add(newProductButton, gbc);

        gbc.gridx++;
        finishProductButton = new JButton("Finish");
        finishProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setProductCreationEnabled(false);
                createNewProduct();
            }
        });
        add(finishProductButton, gbc);

        gbc.gridx = 0;
        gbc.weightx = 0.3;
        gbc.weighty = 1;
        gbc.gridy++;
        add(productData, gbc);
        gbc.weightx = 0.7;
        gbc.gridx = 1;
        add(boxedSetBuilder, gbc);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setProductCreationEnabled(false);
            }
        });
    }

    private void createNewProduct() {
        DatabaseBridge db = DatabaseBridge.instance();
        try {
            db.openConnection();
            db.setAutoCommit(false);

            StringBuilder validationErrorMessage = new StringBuilder();

            if (!validateProductForm(validationErrorMessage)) {
                throw new IllegalStateException(validationErrorMessage.toString());
            }

            // Product table
            {
                PreparedStatement newProduct = db.prepareStatement("INSERT INTO Product VALUES (?,?,?,?)");
                newProduct.setString(1, productCodeInput.getText());
                newProduct.setString(2, nameInput.getText());
                newProduct.setInt(3, Math.toIntExact(((Long) stockInput.getValue())));
                newProduct.setDouble(4, (Double) priceInput.getValue());
                newProduct.executeUpdate();
            }

            switch (Objects.requireNonNull(getSelectedButtonFromGroup(productTypeRadioGroup))) {
                case componentText:
                    if (!validateComponentForm(validationErrorMessage)) {
                        throw new IllegalStateException(validationErrorMessage.toString());
                    }

                    //System.out.println("Component");
                    {
                        PreparedStatement newComponent = db.prepareStatement("INSERT INTO Component VALUES (?,?,?,?)");
                        newComponent.setString(1, productCodeInput.getText());
                        newComponent.setString(2, brandInput.getText());
                        newComponent.setString(3, eraInput.getText());
                        newComponent.setString(4, ((Component.Gauge) Objects.requireNonNull(gaugeInput.getSelectedItem())).toString());
                        newComponent.executeUpdate();
                    }

                    switch (Objects.requireNonNull(getSelectedButtonFromGroup(componentTypeRadioGroup))) {
                        case trackText:
                            //System.out.println("track");
                            {
                                PreparedStatement newTrack = db.prepareStatement("INSERT INTO Track VALUES (?,?)");
                                newTrack.setString(1, productCodeInput.getText());
                                newTrack.setString(2, ((Track.Curvature) Objects.requireNonNull(curvatureInput.getSelectedItem())).toString());
                                newTrack.executeUpdate();
                            }
                            break;
                        case locomotiveText:
                            //System.out.println("locomotive");
                            {
                                PreparedStatement newLocomotive = db.prepareStatement("INSERT INTO Locomotive VALUES (?,?)");
                                newLocomotive.setString(1, productCodeInput.getText());
                                newLocomotive.setString(2, ((Locomotive.PriceBracket) Objects.requireNonNull(locomotiveInput.getSelectedItem())).toString());
                                newLocomotive.executeUpdate();
                            }
                            break;
                        case controllerText:
                            //System.out.println("controller");
                            {
                                PreparedStatement newController = db.prepareStatement("INSERT INTO Controller VALUES (?,?)");
                                newController.setString(1, productCodeInput.getText());
                                newController.setString(2, ((Controller.ControlType) Objects.requireNonNull(controllerInput.getSelectedItem())).toString());
                                newController.executeUpdate();
                            }
                            break;
                    }
                    break;
                case boxedSetText:
                    if (!validateBoxedSetForm(validationErrorMessage)) {
                        throw new IllegalStateException(validationErrorMessage.toString());
                    }
                    //System.out.println("Boxed set");
                    {
                        PreparedStatement newBoxSetItem = db.prepareStatement("INSERT INTO BoxedSetContent VALUES (?,?,?)");
                        newBoxSetItem.setString(1, productCodeInput.getText());

                        for (int i = 0; i < boxedSetContentSelection.getRowCount(); i++) {
                            int quantity = (Integer) boxedSetContentSelection.getValueAt(i, 2);
                            if (quantity == 0) continue;

                            newBoxSetItem.setString(2, (String) boxedSetContentSelection.getValueAt(i, 0));
                            newBoxSetItem.setInt(3, quantity);
                            newBoxSetItem.executeUpdate();
                        }
                    }
                    break;
            }

            db.commit();
            db.setAutoCommit(true);
        } catch (IllegalStateException e) {
            try {
                db.rollback();
            } catch (Throwable i) {
                DatabaseBridge.databaseError("Product creation rollbackerror", i);
            }
            JOptionPane.showMessageDialog(
                    AppContext.getWindow(),
                    "Could not insert new product: " + e.getMessage(),
                    "Invalid Form Input",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Throwable e) {
            DatabaseBridge.databaseError("Product creation error", e);
            try {
                db.rollback();
            } catch (Throwable i) {
                DatabaseBridge.databaseError("Product creation rollbackerror", i);
            }
            JOptionPane.showMessageDialog(
                    AppContext.getWindow(),
                    "Could not insert new product entry.",
                    "Stock Insertion Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            db.closeConnection();
        }

        SwingUtilities.invokeLater(alterEvent);
    }

    private boolean validateProductForm(StringBuilder message) {
        String pc = productCodeInput.getText();
        String n = nameInput.getText();
        String s = stockInput.getText();
        String p = priceInput.getText();

        if (pc.isEmpty() || n.isEmpty() || s.isEmpty() || p.isEmpty()) {
            message.append("Empty product data fields");
            return false;
        }
        if (!pc.matches("^(R|C|L|S|M|P)[a-zA-Z0-9]*$") || pc.length() > 7 || pc.length() < 4) {
            message.append("Malformed product code");
            return false;
        }
        return true;
    }
    private boolean validateComponentForm(StringBuilder message) {
        String b = brandInput.getText();
        String e = eraInput.getText();

        if (b.isEmpty() || e.isEmpty()) {
            message.append("Empty component data fields");
            return false;
        }
        if (!e.matches("^([0-9]{1,2}|[0-9]{1,2}-[0-9]{1,2})$") || e.length() > 5) {
            message.append("Malformed era range");
            return false;
        }

        return true;
    }
    private boolean validateBoxedSetForm(StringBuilder message) {
        int selectedItems = 0;
        for (int i = 0; i < boxedSetContentSelection.getRowCount(); i++) {
            int quantity = (Integer) boxedSetContentSelection.getValueAt(i, 2);
            if (quantity == 0) continue;
            selectedItems++;
        }
        if (selectedItems == 0) {
            message.append("No content selected for the boxed set");
            return false;
        }
        return true;
    }

    private void setProductCreationEnabled(boolean status) {
        if (status) {
            setEnabledRecursively(productData, true);
            setEnabledRecursively(boxedSetBuilder, true);
            selectBoxedSetRadioButton();
            newProductButton.setEnabled(false);
            finishProductButton.setEnabled(true);
        } else {
            setEnabledRecursively(productData, false);
            setEnabledRecursively(boxedSetBuilder, false);
            newProductButton.setEnabled(true);
            finishProductButton.setEnabled(false);
        }
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

    private void selectBoxedSetRadioButton() {
        setEnabledRecursively(componentSpecializationPanel, false);
        setEnabledRecursively(boxedSetBuilder, true);
        boxedSet.setSelected(true);
    }
    private void selectComponentRadioButton() {
        setEnabledRecursively(componentSpecializationPanel, true);
        setEnabledRecursively(boxedSetBuilder, false);
        // enable correct component type panel
        switch (Objects.requireNonNull(getSelectedButtonFromGroup(componentTypeRadioGroup))) {
            case trackText:
                GUI.setEnabledRecursively(trackContainer, true);
                GUI.setEnabledRecursively(locomotiveContainer, false);
                GUI.setEnabledRecursively(controllerContainer, false);
                break;
            case locomotiveText:
                GUI.setEnabledRecursively(trackContainer, false);
                GUI.setEnabledRecursively(locomotiveContainer, true);
                GUI.setEnabledRecursively(controllerContainer, false);
                break;
            case controllerText:
                GUI.setEnabledRecursively(trackContainer, false);
                GUI.setEnabledRecursively(locomotiveContainer, false);
                GUI.setEnabledRecursively(controllerContainer, true);
                break;
        }
    }
    private void selectComponentTypeRadioButton(Class<?> productType) {
        if (productType.equals(Track.class)) {
            GUI.setEnabledRecursively(trackContainer, true);
            GUI.setEnabledRecursively(locomotiveContainer, false);
            GUI.setEnabledRecursively(controllerContainer, false);
        } else if (productType.equals(Locomotive.class)) {
            GUI.setEnabledRecursively(trackContainer, false);
            GUI.setEnabledRecursively(locomotiveContainer, true);
            GUI.setEnabledRecursively(controllerContainer, false);
        } else if (productType.equals(Controller.class)) {
            GUI.setEnabledRecursively(trackContainer, false);
            GUI.setEnabledRecursively(locomotiveContainer, false);
            GUI.setEnabledRecursively(controllerContainer, true);
        } else {
            throw new IllegalArgumentException("Must be a component type");
        }
    }

    private JRadioButton boxedSet; // dumb hack
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
            JRadioButton component = new JRadioButton(componentText);
            boxedSet = new JRadioButton(boxedSetText);
            productTypeRadioGroup.add(component);
            productTypeRadioGroup.add(boxedSet);

            component.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            selectComponentRadioButton();
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
                            selectBoxedSetRadioButton();
                        }
                    });
                }
            });

            panel.add(component, gbc);
            gbc.gridx++;
            panel.add(boxedSet, gbc);

            boxedSet.setSelected(true);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    selectBoxedSetRadioButton();
                }
            });
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
            JRadioButton track = new JRadioButton(trackText);
            JRadioButton locomotive = new JRadioButton(locomotiveText);
            JRadioButton controller = new JRadioButton(controllerText);
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
                JLabel controllerLabel = new JLabel("Wiring");
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
                            selectComponentTypeRadioButton(Track.class);
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
                            selectComponentTypeRadioButton(Locomotive.class);
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
                            selectComponentTypeRadioButton(Controller.class);
                        }
                    });
                }
            });

            // initial selection
            track.setSelected(true);
            selectComponentTypeRadioButton(Track.class);
        }

        return panel;
    }

    /**
     * Either a panel entering the specific specialization data or looking up stuff to add to the boxedset
     * @return
     */
    private JPanel createComposeBoxedSetPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
//        panel.setBackground(Color.PINK);

        panel.setBorder(new EmptyBorder(14, 6, 6, 6));

        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel title = new JLabel("<html><h3>Boxed Set Contents</h3></html>");
        title.setBorder(new EmptyBorder(0,6,0,0));
        panel.add(title, gbc);

        {
            gbc.gridy++;
            gbc.weighty = 1;
            boxedSetContentSelection = new JTable(new BoxedSetConstructionTableModel(productDataSet, buildBoxedSetColumns));
            boxedSetContentSelection.setRowHeight(24);
            JScrollPane scrollPane = new JScrollPane(boxedSetContentSelection);
            panel.add(scrollPane, gbc);

            TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(boxedSetContentSelection.getModel());
            boxedSetContentSelection.setRowSorter(sorter);

            List<RowSorter.SortKey> sortKeys = new ArrayList<>(1);
            sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
            sorter.setSortKeys(sortKeys);
        }

        return panel;
    }
}
