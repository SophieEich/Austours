package MasterTable;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class AddOccupancyWindow extends JFrame {

    private OccupancyPanel parent;

    // US - 6 Enter transactional data (room/bed occupancy per month)

    JComboBox<String> hotelSelect = new JComboBox<>(); // Empty dropdown for hotel selection
    //Occupancy data
    JTextField year      = new JTextField();
    JComboBox<String> month = new JComboBox<>(new String[]{"01","02","03","04","05","06","07","08","09","10","11","12"});
    JTextField roomOcc   = new JTextField();
    JTextField bedOcc    = new JTextField();
    String path = "src/main/resources/occupancy.txt"; //where the occupancy data gets saved

    //constructor
    public AddOccupancyWindow(OccupancyPanel parent) {
        this.parent = parent;
        defineFrame();
        initComponents();
        loadHotels();

        addComponents();

        setVisible(true);


    }
    private void addComponents() { //Columns
        add(new JLabel("Hoel:"));
        add(hotelSelect);
        add(new JLabel("Year:"));
        add(year);
        add(new JLabel("Month:"));
        add(month);
        add(new JLabel("Room Occupancy %:"));
        add(roomOcc);
        add(new JLabel("Bed Occupancy %:"));
        add(bedOcc);

        // save button -> saveOccupancy
        JButton saveButton = new JButton("Save");
        add(saveButton);

        saveButton.addActionListener(e -> saveOccupancy());
    }

    private void initComponents() { //each label gets one column

        setLayout(new GridLayout(7, 2, 10, 10));
    }

    private void loadHotels() { //checks if a line exists before reading -> should avoid empty lines
        try {
            Scanner sc = new Scanner(new File("src/main/resources/hotels.txt"));
            if (sc.hasNextLine()) {
                sc.nextLine(); // skip header
            }
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (data.length >= 3) { //3 columns
                    String id   = data[0].trim();
                    String name = data[2].replace("\"", "").trim();
                    hotelSelect.addItem(id + " - " + name);
                }
            }
            sc.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not load hotels: " + e.getMessage());
        }
    }

    //Framing
    private void defineFrame() {
        setTitle("Add Occupancy");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void saveOccupancy() {
        // if required fields = empty
        if (year.getText().isEmpty() ||
                roomOcc.getText().isEmpty() ||
                bedOcc.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields!");
            return;
        }

        // Only positive numbers allowed
        double roomOccVal, bedOccVal;
        int yearVal;
        try {
            roomOccVal = Double.parseDouble(roomOcc.getText().trim());
            bedOccVal  = Double.parseDouble(bedOcc.getText().trim());
            yearVal    = Integer.parseInt(year.getText().trim());
            if (roomOccVal <= 0 || bedOccVal <= 0 || yearVal <= 0) {
                JOptionPane.showMessageDialog(this, "Values must be positive numbers!");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Year and Occupancy must be valid numbers!");
            return;
        }
        //
        String selected  = (String) hotelSelect.getSelectedItem();
        String hotelId   = selected.split(" - ")[0].trim();
        String hotelName = selected.split(" - ")[1].trim();



        // Looks up category from hotels.txt based on hotel ID
        String category = getCategoryForHotel(hotelId); // ← auto fetched


        String line = String.join(",",
                hotelId,
                hotelName,
                category,
                year.getText(),
                (String) month.getSelectedItem(),
                roomOcc.getText(),
                bedOcc.getText()
        );
        //header needed?
        try {
            File file = new File(path);

            boolean needsHeader;
            if (!file.exists() || file.length() == 0) {
                needsHeader = true;
            } else {
                needsHeader = false;
            }

            FileWriter fw = new FileWriter(file, true);
            if (needsHeader) {
                fw.write("hotelId,hotelName,category,year,month,roomOccupancy,bedOccupancy\n");
                //Without \n the first data line gets glued to the header line!
            }
            fw.write(line + "\n");
            fw.close();

            parent.fillTable();
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not save: " + e.getMessage());
        }
    }
    //searches category
    private String getCategoryForHotel(String hotelId) {
        try {
            Scanner sc = new Scanner(new File("src/main/resources/hotels.txt"));
            if (sc.hasNextLine()) {
                sc.nextLine(); // skip header
            }
            //read each hotel line
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (data.length >= 2) {
                    String id = data[0].trim();
                    if (id.equals(hotelId)) {
                        return data[1].replace("\"", "").trim();
                    }
                }
            }
            sc.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not load category: " + e.getMessage());
        }
        return "";
    }
}
