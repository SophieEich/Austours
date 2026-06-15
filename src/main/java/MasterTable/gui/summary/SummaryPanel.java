package MasterTable.gui.summary;

import MasterTable.entity.Category;
import MasterTable.entity.Hotel;
import MasterTable.entity.Occupancy;
import MasterTable.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
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

    //US1
    model.addTableModelListener(e-> {//US-23
        if (e.getType() != TableModelEvent.UPDATE) return;

        int col = e.getColumn();
        // only rooms + Beds Occupancy
        if (col != 4 && col != 5) return;

        int row = e.getFirstRow();

        try {
            Occupancy occ = loadedOccupancies.get(row); //straight from list
            int roomOcc = Integer.parseInt(model.getValueAt(row, 4).toString());
            int bedOcc= Integer.parseInt(model.getValueAt(row, 5).toString());

            //Validation
            if (roomOcc <= 0 || bedOcc <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Values must be positive numbers!");
                fillTable();
                return;
            }
            if (roomOcc > occ.getHotel().getNoRooms()) {
                JOptionPane.showMessageDialog(this, "Room occupancy cannot exceed total rooms!");
                fillTable(); // so that the old value is there
                return;
            }

            if (bedOcc > occ.getHotel().getNoBeds()) {
                JOptionPane.showMessageDialog(this, "Bed occupancy cannot exceed total beds!");
                fillTable(); // so that the old value is there
                return;
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