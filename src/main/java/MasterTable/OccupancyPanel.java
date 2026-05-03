package MasterTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.Scanner;

public class OccupancyPanel extends JPanel {

    JTable table;
    DefaultTableModel model;
    String path = "src/main/resources/occupancy.txt";

    JComboBox<String> fromMonth = new JComboBox<>(new String[]{"01","02","03","04","05","06","07","08","09","10","11","12"});
    JComboBox<String> toMonth   = new JComboBox<>(new String[]{"01","02","03","04","05","06","07","08","09","10","11","12"});
    JComboBox<Category> categoryFilter = new JComboBox<>(Category.values());
    JTextField fromYear = new JTextField("2024", 5);
    JTextField toYear   = new JTextField("2026", 5);
    JComboBox<String> hotelFilter = new JComboBox<>();

    OccupancyPanel() {
        definePanel();
        initComponents();
        addComponents();
        loadHotel();
        fillTable();
    }

    public void fillTable() {
        model.setRowCount(0);

        String selectedHotel = (String) hotelFilter.getSelectedItem();
        String filterHotelId = null;
        if (selectedHotel != null && !selectedHotel.equals("ALL")) {
            filterHotelId = selectedHotel.split(" - ")[0].trim();
        }

        int fYear  = Integer.parseInt(fromYear.getText().trim());
        int fMonth = Integer.parseInt((String) fromMonth.getSelectedItem());
        int tYear  = Integer.parseInt(toYear.getText().trim());
        int tMonth = Integer.parseInt((String) toMonth.getSelectedItem());

        try {
            Scanner sc = new Scanner(new File(path));
            if (sc.hasNextLine()) sc.nextLine();
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] d = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (d.length < 7) continue;

                String hotelId   = d[0].trim();
                String hotelName = d[1].trim();
                String hotelCategory = d[2].trim();
                int year         = Integer.parseInt(d[3].trim());
                int month        = Integer.parseInt(d[4].trim());
                String roomOcc   = d[5].trim();
                String bedOcc    = d[6].trim();

                //Hotel Filter
                if (filterHotelId != null && !hotelId.equals(filterHotelId)) continue;

                //Category Filter
                Category selectedCategory = (Category) categoryFilter.getSelectedItem();
                if (selectedCategory != Category.ALL && !hotelCategory.equals(selectedCategory.toString())) continue;

                //Date Filter
                int rowDate    = year * 100 + month;
                int filterFrom = fYear * 100 + fMonth;
                int filterTo   = tYear * 100 + tMonth;
                if (rowDate < filterFrom || rowDate > filterTo) continue;

                model.addRow(new Object[]{hotelId, hotelName, year, month, roomOcc, bedOcc});
            }
            sc.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not load occupancy data: " + e.getMessage());
        }
    }



    private void loadHotel() {
        hotelFilter.addItem("ALL");
        try {
            Scanner sc = new Scanner(new File("src/main/resources/hotels.txt"));
            if (sc.hasNextLine()) sc.nextLine();
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (data.length >= 3) {
                    hotelFilter.addItem(data[0] + " - " + data[2]);
                }
            }
            sc.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not load hotels!");
        }
    }

    private void addComponents() {
        JPanel filterPanel = new JPanel();
        filterPanel.add(new JLabel("Hotel:"));
        filterPanel.add(hotelFilter);
        filterPanel.add(new JLabel("From Month:"));
        filterPanel.add(fromMonth);
        filterPanel.add(new JLabel("Year:"));
        filterPanel.add(fromYear);
        filterPanel.add(new JLabel("To Month:"));
        filterPanel.add(toMonth);
        filterPanel.add(new JLabel("Year:"));
        filterPanel.add(toYear);
        filterPanel.add(new JLabel("Category:"));
        filterPanel.add(categoryFilter);

        JButton filterButton = new JButton("FILTER");
        filterPanel.add(filterButton);
        filterButton.addActionListener(e -> fillTable());
        add(filterPanel, BorderLayout.NORTH);

        // Table
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Button bar
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("ADD OCCUPANCY");
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> new AddOccupancyWindow(this));
    }



    private void initComponents() {
        model = new DefaultTableModel();
        table = new JTable(model);
        model.addColumn("HOTEL ID");
        model.addColumn("HOTEL NAME");
        model.addColumn("YEAR");
        model.addColumn("MONTH");
        model.addColumn("ROOM OCCUPANCY");
        model.addColumn("BED OCCUPANCY");
    }

    private void definePanel() {
        setLayout(new BorderLayout());
    }


}

