package MasterTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.nio.file.Files;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class HotelTable extends JPanel {
    //not JFrame because we want 2 Windows
    JTable table = new JTable();
    DefaultTableModel model = new DefaultTableModel();
    String path = "src/main/resources/hotels.txt";
    private Hotel hotel;




    HotelTable() {

        //USER STORY 3
        defineFrame();
        innitComponents();
        addComponents();
        fillTable();

    }

    private void fillTable() {
        model.addColumn("ID");
        model.addColumn("CATEGORY");
        model.addColumn("NAME");
        model.addColumn("OWNER");
        model.addColumn("CONTACT");
        model.addColumn("ADDRESS");
        model.addColumn("CITY");
        model.addColumn("CITYCODE");
        model.addColumn("PHONE");
        model.addColumn("NUMBER ROOMS");
        model.addColumn("NUMBER BEDS");
        model.addColumn("LAST REPORTED DATA"); // Kommt erst mit dem EDIT US-5



        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            List<Hotel> hotels = s.createQuery("from Hotel", Hotel.class).list();
            for (Hotel h : hotels) {
                model.addRow(new Object[]{
                        h.getId(),
                        h.getCategory(),
                        h.getName(),
                        h.getOwner(),
                        h.getContact(),
                        h.getAddress(),
                        h.getCity(),
                        h.getCityCode(),
                        h.getPhone(),
                        h.getNoRooms(),
                        h.getNoBeds(),
                        h.getLastReported()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not load hotels: " + e.getMessage());
        }
    }

    private void addComponents() {
       JScrollPane scrollPane = new JScrollPane(table);
       add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JButton addButton = new JButton("ADD HOTEL");
        JButton editButton = new JButton("EDIT HOTEL");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> new AddEditHotelWindow(model, this, -1)); //1 because no ROw is selected

        //US-5
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a hotel to edit!");
                return;
            }
            new AddEditHotelWindow(model, this, selectedRow);
        });

    }

    private void innitComponents() {
        JLabel headerLabel = new JLabel("Masterdata Hotel:");
        model = new DefaultTableModel();
        table = new JTable();
        table.setModel(model);




    }

    private void defineFrame() {
        //setSize(700, 700);
        //setTitle("Master Data"); // name
        //setLocationRelativeTo(null);
        //setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }


    // US- 4
    //Needs to be updated for the SQL Server, then with selects and creates, updates, inserts
    public void saveAlltoFile() {// the entire file gets always saved anew
        try {
            ArrayList<String> lines = new ArrayList<>();
            lines.add("id,category,name,owner,contact,address,city,cityCode,phone,noRooms,noBeds,date");

            for (int i = 0; i < model.getRowCount(); i++) { // searching through the table
                String[] row = new String[model.getColumnCount()];
                boolean isEmpty = true; // isEmpty is our check, if the line is emptyit should not get saved

                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object val = model.getValueAt(i, j);
                    if (val == null) {
                        row[j] = ""; // wiithout [i] because we are alwyas in the right row
                        // if we dont find something it stays empty
                    } else {
                        row[j] = val.toString();
                    }
                    if (!row[j].isEmpty()) {
                        isEmpty = false;
                    }
                }

                if (!isEmpty) {
                    lines.add(String.join(",", row));
                }
            }

            Files.write(
                    java.nio.file.Paths.get(path),
                    lines,
                    java.nio.charset.StandardCharsets.UTF_8
            );
            System.out.println("Saved in: " + path);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Could not Save: " + e.getMessage());
        }

    }
}
