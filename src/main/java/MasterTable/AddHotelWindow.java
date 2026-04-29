package MasterTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class AddHotelWindow extends JFrame {

    private DefaultTableModel model;
    private BasicTable parent;


    public AddHotelWindow(DefaultTableModel model, BasicTable parent) {
        this.model = model;
        this.parent = parent;
        setTitle("Edit Hotel");
        setSize(500, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);


        setLayout(new GridLayout(11, 2, 15, 17));

        JTextField name = new JTextField();
        JTextField category = new JTextField();
        JTextField owner = new JTextField();
        JTextField contact = new JTextField();
        JTextField address = new JTextField();
        JTextField city = new JTextField();
        JTextField citycode = new JTextField();
        JTextField phone = new JTextField();
        JTextField rooms = new JTextField();
        JTextField beds = new JTextField();

        add(new JLabel("Name:")); add(name);
        add(new JLabel("Category:")); add(category);
        add(new JLabel("Owner:")); add(owner);
        add(new JLabel("Contact:")); add(contact);
        add(new JLabel("Address:")); add(address);
        add(new JLabel("City:")); add(city);
        add(new JLabel("Citycode:")); add(citycode);
        add(new JLabel("Phone:")); add(phone);
        add(new JLabel("Number of Rooms:")); add(rooms);
        add(new JLabel("Number of Beds:")); add(beds);

        JButton saveButton = new JButton("Save");
        add(saveButton);


        saveButton.addActionListener(e -> {
            String nextId = getNextId(model);
            String today = LocalDate.now().toString();

            Object[] newRow = {
                    nextId, //ID automatic
                    category.getText(),
                    name.getText(),
                    owner.getText(),
                    contact.getText(),
                    address.getText(),
                    city.getText(),
                    citycode.getText(),
                    phone.getText(),
                    rooms.getText(),
                    beds.getText(),
                    today,
            };

            model.addRow(newRow);
            parent.saveAlltoFile();
            dispose();


        });


        setVisible(true);


    }

    private String getNextId(DefaultTableModel model) {
        int maxId = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                // Den Wert aus der ersten Spalte (ID) holen
                Object value = model.getValueAt(i, 0);
                if (value != null) {
                    int currentId = Integer.parseInt(value.toString().trim());
                    if (currentId > maxId) {
                        maxId = currentId;
                    }
                }
            } catch (Exception e) {
                // Ignorieren, falls mal ein Header oder Text drinsteht
            }
        }
        return String.valueOf(maxId + 1);
    }

}
