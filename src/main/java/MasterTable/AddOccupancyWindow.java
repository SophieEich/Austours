package MasterTable;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;

public class AddOccupancyWindow extends JFrame {

    private OccupancyPanel parent;

    JTextField hotelId   = new JTextField();
    JTextField hotelName = new JTextField();
    JComboBox<Category> category = new JComboBox<>(Category.values());
    JTextField year      = new JTextField();
    JComboBox<String> month = new JComboBox<>(new String[]{"01","02","03","04","05","06","07","08","09","10","11","12"});
    JTextField roomOcc   = new JTextField();
    JTextField bedOcc    = new JTextField();
    String path = "src/main/resources/occupancy.txt";

    public AddOccupancyWindow(OccupancyPanel parent) {
        this.parent = parent;
        defineFrame();
        initComponets();
        addComponents();

        setVisible(true);


    }

    private void addComponents() {
        add(new JLabel("Hotel ID:"));
        add(hotelId);
        add(new JLabel("Hotel Name:"));
        add(hotelName);
        add(new JLabel("Category:"));
        add(category);
        add(new JLabel("Year:"));
        add(year);
        add(new JLabel("Month:"));
        add(month);
        add(new JLabel("Room Occupancy %:"));
        add(roomOcc);
        add(new JLabel("Bed Occupancy %:"));
        add(bedOcc);

        JButton saveButton = new JButton("Save");
        add(saveButton);

        saveButton.addActionListener(e -> saveOccupancy());
    }

    private void initComponets() {
        setLayout(new GridLayout(8, 2, 10, 10));
    }

    private void defineFrame() {
        setTitle("Add Occupancy");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void saveOccupancy() {
        String line = String.join(",",
                hotelId.getText(),
                hotelName.getText(),
                category.getSelectedItem().toString(),
                year.getText(),
                (String) month.getSelectedItem(),
                roomOcc.getText(),
                bedOcc.getText()
        );

        try {
            File file = new File(path);
            boolean needsHeader = !file.exists() || file.length() == 0;

            FileWriter fw = new FileWriter(file, true);
            if (needsHeader) fw.write("hotelId,hotelName,category,year,month,roomOccupancy,bedOccupancy");
            fw.write(line + "\n");
            fw.close();

            parent.fillTable();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not save: " + ex.getMessage());
        }
    }
}
