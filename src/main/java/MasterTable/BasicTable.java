package MasterTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class BasicTable extends JFrame {

    JTable table = new JTable();
    DefaultTableModel model = new DefaultTableModel();
    String path = "src/main/resources/hotels.txt";

    BasicTable() {

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



        try {
            Scanner sc = new Scanner(new File(path));
            if (sc.hasNextLine()) {
                sc.nextLine();
            }
            while (sc.hasNextLine()) {
                String line = sc.nextLine();


                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // before only ",", but this caused issued with names that included an ,
                // only "," outside of "..." are counted with this line

                model.addRow(data);
            }

            sc.close();

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "File not found!");
        }
    }

    private void addComponents() {
       JScrollPane scrollPane = new JScrollPane(table);
       add(scrollPane, BorderLayout.CENTER);


        JButton button = new JButton("ADD HOTEL");
        add(button, BorderLayout.SOUTH);
        button.addActionListener(e -> new AddHotelWindow(model, this));
    }

    private void innitComponents() {
        JLabel headerLabel = new JLabel("Masterdata Hotel:");
        model = new DefaultTableModel();
        table = new JTable();
        table.setModel(model);




    }

    private void defineFrame() {
        setSize(700, 700);
        setTitle("Master Data"); // name
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }


    // US- 4
    public void saveAlltoFile() {
        try {
            ArrayList<String> lines = new ArrayList<>();
            lines.add("id,category,name,owner,contact,address,city,cityCode,phone,noRooms,noBeds,date");

            for (int i = 0; i < model.getRowCount(); i++) {
                String[] row = new String[model.getColumnCount()];
                boolean isEmpty = true;

                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object val = model.getValueAt(i, j);
                    row[j] = (val == null ? "" : val.toString());
                    if (!row[j].isEmpty()) isEmpty = false;
                }

                if (!isEmpty) lines.add(String.join(",", row));
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
