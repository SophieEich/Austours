package MasterTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EditWindow extends JFrame {

    private DefaultTableModel model;

    public EditWindow(DefaultTableModel model) {
        this.model = model;
        setTitle("Edit Hotel");
        setSize(500, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);


        setLayout(new GridLayout(7, 3, 12, 15));

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
            Object[] newRow = {
                    "AUTO",//ID automatic
                    category.getText(),
                    owner.getText(),
                    contact.getText(),
                    address.getText(),
                    city.getText(),
                    citycode.getText(),
                    phone.getText(),
                    rooms.getText(),
                    beds.getText()
            };

            model.addRow(newRow);
        });


        setVisible(true);


    }

}
