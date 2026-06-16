package MasterTable.gui.summary;

import MasterTable.entity.Category;
import MasterTable.entity.Hotel;
import MasterTable.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;


public class SummaryPanel extends JPanel {

    JTable table;
    DefaultTableModel model;

    Map<String, Integer> countMap = new HashMap<>();
    Map<String, Integer> roomsMap = new HashMap<>();
    Map<String, Integer> bedsMap  = new HashMap<>();

    //US1 (Summary of master data (hotels per category, avg. rooms/beds))
    public SummaryPanel() {
        definePanel();      //wie ist das fenster aufgeteilt
        initComponents();   //welche Elemente gibt es
        addComponents();    //wo im fenster kommen die Elemente hin
        fillTable();        //füllen von tabellen
    }

    private void definePanel() {
        setLayout(new BorderLayout());
    }   // wie werden die Elemente im Fenster angeordnet (5 Zonen)

    private void initComponents() {
        model = new DefaultTableModel();
        table = new JTable(model);
        table.setEnabled(false); // read only

        //kategorien der Spalten
        model.addColumn("CATEGORY");
        model.addColumn("NUMBER OF HOTELS");
        model.addColumn("AVG ROOMS");
        model.addColumn("AVG BEDS");


        // for better readability with color change
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                                                                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(235, 243, 250)); // NOE Blau
                }
                return this;
            }
        });

        // automatically adjust width of column
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        //increases row height to 16pt
        table.setRowHeight(25); //25 pixel hohe Zeilen


    }

    private void addComponents() {
        JLabel title = new JLabel("Hotel Master Data Summary", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, BorderLayout.NORTH); //north-zone of the border layout
        add(new JScrollPane(table), BorderLayout.CENTER);   //if the table is too long you are able to scroll down


        JPanel buttonPanel = new JPanel();

        JButton refreshButton = new JButton("REFRESH");
        //what happens when someone clicks on the refresh button
        refreshButton.addActionListener(e -> fillTable());

        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);   //south zone


    }

    private void fillTable() {
        // Initialize maps using enum

        model.setRowCount(0);

        //Maps get reset
        for (Category cat : Category.values()) {
            if (cat != Category.ALL) {
                countMap.put(cat.toString(), 0);
                roomsMap.put(cat.toString(), 0);
                bedsMap.put(cat.toString(),  0);
            }
        }

        // Connects with Database
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            List<Hotel> hotels = s.createQuery("from Hotel", Hotel.class).list();

            for (Hotel h : hotels) {
                String categoryStr = h.getCategory();
                int rooms = (h.getNoRooms() != null) ? h.getNoRooms() : 0;
                int beds = (h.getNoBeds() != null) ? h.getNoBeds() : 0;

                // Logik zum Zählen/Summieren (wie vorher)
                if (countMap.containsKey(categoryStr)) {
                    countMap.put(categoryStr, countMap.get(categoryStr) + 1);
                    roomsMap.put(categoryStr, roomsMap.get(categoryStr) + rooms);
                    bedsMap.put(categoryStr, bedsMap.get(categoryStr) + beds);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading Summary: " + e.getMessage());
        }

        // Add one row per category
        int totalCount = 0;
        int totalRooms = 0;
        int totalBeds = 0;

        for (Category cat : Category.values()) {
            if (cat == Category.ALL) continue;

            int count = countMap.get(cat.toString());
            int rooms = roomsMap.get(cat.toString());
            int beds  = bedsMap.get(cat.toString());

            double avgRooms;
            if (count > 0 ) {
                avgRooms = (double) rooms / count;
            }else {
                avgRooms = 0;
            }

            double avgBeds;
            if (count > 0 ) {
                avgBeds = (double) beds / count;
            }else {
                avgBeds = 0;
            }


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
        double totalAvgRooms;
        if (totalCount > 0) {
            totalAvgRooms = (double) totalRooms / totalCount;
        }else {
            totalAvgRooms = 0;
        }

        double totalAvgBeds;
        if (totalCount > 0) {
            totalAvgBeds = (double) totalBeds  / totalCount;
        } else {
            totalAvgBeds = 0;
        }

        model.addRow(new Object[]{
                "TOTAL",
                totalCount,
                String.format("%.1f", totalAvgRooms),
                String.format("%.1f", totalAvgBeds)
        });
    }
}