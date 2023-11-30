package gui.staff;

import controllers.AppContext;
import db.DatabaseBridge;
import entity.user.Person;
import gui.components.TabbedGUIContainer;
import org.jdesktop.swingx.prompt.PromptSupport;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ManagerScreen extends JPanel implements TabbedGUIContainer.TabPanel {
    private class UserRow extends JPanel {
        static String[] roleNames = {"USER", "STAFF"};
        Person person;

        public UserRow(Person user, TabbedGUIContainer parent) {
            person = user;
            JComboBox<String> roleBox = new JComboBox<>(roleNames);
            roleBox.setSelectedItem(user.getRole().toString());

            JButton fireButton = new JButton("Dismiss");

            // Can't promote/demote yourself
            if (person.getRole().equals(Person.Role.MANAGER)) {
                fireButton.setEnabled(false);
            }

            JLabel nameLabel = new JLabel(user.getFullName() + " ("+user.getEmail()+")");

            GridLayout gridLayout = new GridLayout(0,2);
            JPanel content = new JPanel();

            content.setLayout(gridLayout);
            add(content);

            content.add(nameLabel);
            content.add(fireButton);

            content.add(new JSeparator(SwingConstants.HORIZONTAL));
            content.add(new JSeparator(SwingConstants.HORIZONTAL));

            fireButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(AppContext.getWindow(), "Are you sure you want to dismiss " + user.getFullName(), "Confirm Action", JOptionPane.YES_NO_OPTION);

                if (confirm == 0) {
                    DatabaseBridge db = DatabaseBridge.instance();
                    try {
                        db.openConnection();
                        Person.updateUserRole(person, Person.Role.USER);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    } finally {
                        db.closeConnection();
                    }

                    refresh();
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


        JPanel header = new JPanel();
        gbc.fill = GridBagConstraints.BOTH;
        header.setLayout(new GridBagLayout());

        JLabel title = new JLabel("<html><h1>User Management </h1></html>");
        title.setHorizontalAlignment(SwingConstants.LEFT);
        JTextField emailBox = new JTextField();
        PromptSupport.setPrompt("Add staff member by email", emailBox);
        emailBox.setPreferredSize( new Dimension( 200, 24 ) );
        JButton search = searchAndPromoteEmail(emailBox);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        header.add(title, gbc);

        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.gridy++;
        header.add(emailBox, gbc);

        gbc.gridx++;
        gbc.fill = GridBagConstraints.NONE;
        header.add(search, gbc);

        this.add(header, BorderLayout.NORTH);
        header.setBorder(new EmptyBorder(7,7,7,7));

        JPanel filler = new JPanel();
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(filler, gbc);

        scrollPane = new JScrollPane(contentPanel);
        this.add(scrollPane, BorderLayout.CENTER);

        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = -1;

        refresh();
    }

    private JButton searchAndPromoteEmail(JTextField emailBox) {
        JButton search = new JButton("Promote");

        search.addActionListener((e) -> {
            DatabaseBridge db = DatabaseBridge.instance();

            try {
                db.openConnection();
                Person newStaffMember = Person.getPersonByEmail(emailBox.getText());

                if (newStaffMember == null) {
                    JOptionPane.showMessageDialog(AppContext.getWindow(), "Could not find user with the email "+ emailBox.getText(), "User not Found", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Are you sure you want to promote ");
                sb.append(newStaffMember.getFullName());
                sb.append(" to the role of staff?");

                int confirm = JOptionPane.showConfirmDialog(AppContext.getWindow(), sb.toString(), "Confirm Action", JOptionPane.YES_NO_OPTION);
                if (confirm == 0) {
                    Person.updateUserRole(newStaffMember, Person.Role.STAFF);
                }

                emailBox.setText("");

            } catch (SQLException ex) {
                DatabaseBridge.databaseError("Error finding user by email ["+ emailBox.getText()+"] to promote them to staff", ex);
            } finally {
                db.closeConnection();
            }
            refresh();
        });
        return search;
    }

    private void refresh() {
        contentPanel.removeAll();

        DatabaseBridge db = DatabaseBridge.instance();

        List<Person> personList = new ArrayList<>();
        try {
            db.openConnection();
            PreparedStatement query = db.prepareStatement("SELECT Person.PersonId FROM Person JOIN team005.Role R on Person.PersonId = R.personId WHERE R.role != 'USER'");

            ResultSet rs = query.executeQuery();

            while (rs.next()) {
                Person them = Person.getPersonByID(rs.getInt("PersonId"));
                assert them != null;
                personList.add(them);
            }
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to fetch all persons", e);
            throw new RuntimeException();
        } finally {
            db.closeConnection();
        }

        personList.forEach(this::addUser);

        revalidate();
        repaint();
    }

    private void addUser(Person user) {
//        gbc.gridy += 1;
        gbc.weighty = 0;
        gbc.weightx = 0;
        UserRow row = new UserRow(user, parent);
        contentPanel.add(row, gbc, 0);
    }

    @Override
    public void setNotebookContainer(TabbedGUIContainer cont) {
        parent = cont;
    }

    @Override
    public void onSelected() {
        refresh();
    }
}
