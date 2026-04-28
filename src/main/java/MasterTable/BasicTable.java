package MasterTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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

                // splitte die Zeile bei ;
                String[] data = line.split(",");

                // füge direkt in Tabelle ein
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
        button.addActionListener(e -> new EditWindow());
    }

    private void innitComponents() {
        JLabel headerLabel = new JLabel("Masterdata Hotel:");
        model = new DefaultTableModel();
        table = new JTable();
        table.setModel(model);




    }

    private void defineFrame() {
        setSize(700, 700);
        setTitle("Basic Table Demo"); // name
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }


}
