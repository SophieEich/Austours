package MasterTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.Scanner;
import javax.swing.BoxLayout;
public class OccupancyPanel extends JPanel {

    JTable table;
    DefaultTableModel model;
    String path = "src/main/resources/occupancy.txt";


    // ── US-02: Filter controls ────────────────────────────────────────────────
    // Senior User can filter the occupancy summary by hotel, date range, and category

    JComboBox<String>   hotelFilter    = new JComboBox<>();
    JComboBox<String>   fromMonth      = new JComboBox<>(new String[]{"01","02","03","04","05","06","07","08","09","10","11","12"});
    JTextField          fromYear       = new JTextField("2024", 5);
    JComboBox<String>   toMonth        = new JComboBox<>(new String[]{"01","02","03","04","05","06","07","08","09","10","11","12"});
    JTextField          toYear         = new JTextField("2026", 5);
    JComboBox<Category> categoryFilter = new JComboBox<>(Category.values());

    // ─────────────────────────────────────────────────────────────────────────

    OccupancyPanel() {
        definePanel();
        initComponents();
        addComponents();
        loadHotel();
        fillTable();
    }

    // US-02: Reads occupancy data from file and applies all active filter criteria
    // Displays room and bed occupancy per month in the summary table
    public void fillTable() {
        model.setRowCount(0);

        // US-02: Resolve selected hotel filter to a hotel ID
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


    // US-02: Populates the hotel dropdown from hotels.txt for the hotel filter
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

    // US-02: Builds the filter bar (hotel, date range, category + FILTER button) and table area
    private void addComponents() {

        // US-02: Row 1 — hotel and date range filters
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Hotel:"));      row1.add(hotelFilter);
        row1.add(new JLabel("From Month:")); row1.add(fromMonth);
        row1.add(new JLabel("Year:"));       row1.add(fromYear);
        row1.add(new JLabel("To Month:"));   row1.add(toMonth);
        row1.add(new JLabel("Year:"));       row1.add(toYear);

        // US-02: Row 2 — category filter + FILTER button
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(new JLabel("Category:")); row2.add(categoryFilter);

        JButton filterButton = new JButton("FILTER");
        filterButton.addActionListener(e -> fillTable());
        row2.add(filterButton);

        // Stack both rows vertically
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.add(row1);
        filterPanel.add(row2);

        add(filterPanel, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("ADD OCCUPANCY");
        addButton.addActionListener(e -> new AddOccupancyWindow(this));
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);
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

