package gui.staff;

import controllers.AppContext;
import db.DatabaseBridge;
import entity.StoreAttributes;
import entity.user.Person;
import gui.person.TabbedGUIContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManagerScreen extends JPanel implements TabbedGUIContainer.TabPanel {
    private static class UserRow extends JPanel {
        static String[] roleNames = StoreAttributes.Role.getStringValues();
        Person person;

        public UserRow(Person user) {
            person = user;
            JComboBox<String> roleBox = new JComboBox<>(roleNames);
            roleBox.setSelectedItem(user.getRole().toString());

            JLabel nameLabel = new JLabel(user.getFullName());

            GridLayout gridLayout = new GridLayout(0,2);
            JPanel content = new JPanel();

            content.setLayout(gridLayout);
            add(content);

            content.add(nameLabel);
            content.add(roleBox);

            content.add(new JSeparator(SwingConstants.HORIZONTAL));
            content.add(new JSeparator(SwingConstants.HORIZONTAL));

            roleBox.addActionListener(e -> {
                StoreAttributes.Role newRole = StoreAttributes.Role.valueOf((String) roleBox.getSelectedItem());

                DatabaseBridge db = DatabaseBridge.instance();
                try {
                    db.openConnection();
                    Person.updateUserRole(person, newRole);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                } finally {
                    db.closeConnection();
                }
            });
        }
    }
    private TabbedGUIContainer parent;
    JScrollPane scrollPane;
    JPanel contentPanel;
    GridBagConstraints gbc;

    public ManagerScreen() {
        setLayout(new BorderLayout());

        GridBagLayout gbl = new GridBagLayout();
        gbc = new GridBagConstraints();
        contentPanel = new JPanel();
        contentPanel.setLayout(gbl);

        JLabel title = new JLabel("<html><h1>Update User Roles</h1></html>");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(title, BorderLayout.NORTH);

        scrollPane = new JScrollPane(contentPanel);
        this.add(scrollPane, BorderLayout.CENTER);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;

        DatabaseBridge db = DatabaseBridge.instance();

        try {
            db.openConnection();
            PreparedStatement query = db.prepareStatement("SELECT * FROM Person");

            ResultSet rs = query.executeQuery();

            while (rs.next()) {
                Person them = Person.getPersonByEmail(rs.getString("email"));
                assert them != null;
                addUser(them);
            }
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to fetch all persons", e);
            throw new RuntimeException();
        } finally {
            db.closeConnection();
        }
    }

    public void addUser(Person user) {
        gbc.gridy += 1;
        UserRow row = new UserRow(user);
        contentPanel.add(row, gbc);
    }

    @Override
    public void setNotebookContainer(TabbedGUIContainer cont) {
        parent = cont;
    }

    public static void main(String[] args) {
        JFrame win = AppContext.getWindow();
        win.add(new ManagerScreen());
        win.setVisible(true);
    }
}
