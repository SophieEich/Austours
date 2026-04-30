package MasterTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SummaryPanel extends JPanel {

    JTable table;
    DefaultTableModel model;
    String path = "src/main/resources/hotels.txt";

    Map<String, Integer> countMap = new HashMap<>();
    Map<String, Integer> roomsMap = new HashMap<>();
    Map<String, Integer> bedsMap  = new HashMap<>();

    SummaryPanel() {
        definePanel();
        initComponents();
        addComponents();
        fillTable();
    }

    private void definePanel() {
        setLayout(new BorderLayout());
    }

    private void initComponents() {
        model = new DefaultTableModel();
        table = new JTable(model);
        table.setEnabled(false); // read only

        model.addColumn("CATEGORY");
        model.addColumn("NUMBER OF HOTELS");
        model.addColumn("AVG ROOMS");
        model.addColumn("AVG BEDS");
    }

    private void addComponents() {
        JLabel title = new JLabel("Hotel Master Data Summary", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void fillTable() {
        // Initialize maps using enum
        for (Category cat : Category.values()) {
            if (cat != Category.ALL) {
                countMap.put(cat.toString(), 0);
                roomsMap.put(cat.toString(), 0);
                bedsMap.put(cat.toString(),  0);
            }
        }

        // Read hotels.txt
        try {
            Scanner sc = new Scanner(new File(path));
            if (sc.hasNextLine()) sc.nextLine(); // skip header
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] d = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (d.length < 11) continue;

                String categoryStr = d[1].replace("\"", "").trim();
                int rooms = 0, beds = 0;

                try { rooms = Integer.parseInt(d[9].trim());  } catch (Exception ignored) {}
                try { beds  = Integer.parseInt(d[10].trim()); } catch (Exception ignored) {}

                // Match category string to enum
                for (Category cat : Category.values()) {
                    if (cat != Category.ALL && cat.toString().equals(categoryStr)) {
                        countMap.put(cat.toString(), countMap.get(cat.toString()) + 1);
                        roomsMap.put(cat.toString(), roomsMap.get(cat.toString()) + rooms);
                        bedsMap.put(cat.toString(),  bedsMap.get(cat.toString())  + beds);
                    }
                }
            }
            sc.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not load data: " + e.getMessage());
        }

        // Add one row per category
        int totalCount = 0, totalRooms = 0, totalBeds = 0;

        for (Category cat : Category.values()) {
            if (cat == Category.ALL) continue;

            int count = countMap.get(cat.toString());
            int rooms = roomsMap.get(cat.toString());
            int beds  = bedsMap.get(cat.toString());

            double avgRooms = count > 0 ? (double) rooms / count : 0;
            double avgBeds  = count > 0 ? (double) beds  / count : 0;

            model.addRow(new Object[]{
                    cat.toString(),
                    count,
                    String.format("%.1f", avgRooms),
                    String.format("%.1f", avgBeds)
            });

            totalCount += count;
            totalRooms += rooms;
            totalBeds  += beds;
        }

        // Total row
        double totalAvgRooms = totalCount > 0 ? (double) totalRooms / totalCount : 0;
        double totalAvgBeds  = totalCount > 0 ? (double) totalBeds  / totalCount : 0;

        model.addRow(new Object[]{
                "TOTAL",
                totalCount,
                String.format("%.1f", totalAvgRooms),
                String.format("%.1f", totalAvgBeds)
        });
    }
}