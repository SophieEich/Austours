package MasterTable;

import MasterTable.Login.UserRole;
import MasterTable.Login.UsersHibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import java.util.Scanner;

public class HotelTable extends JPanel {
    //not JFrame because we want 2 Windows
    JTable table = new JTable();
    DefaultTableModel model = new DefaultTableModel();
    String path = "src/main/resources/hotels.txt";

    private final UsersHibernate currentUser;

    HotelTable(UsersHibernate user) {
        this.currentUser = user;

        //USER STORY 3
        defineFrame();
        innitComponents();
        addComponents();
        importIfEmpty();
        fillTable();
    }

    public void fillTable() {
        if (model.getColumnCount() == 0) {
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
        }
        model.setRowCount(0); // Rows will be eptied, but columns stay the same

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
                        h.getNoRoom(),
                        h.getNoBed(),
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

        //Only Admin can add/edit
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
        addButton.setEnabled(isAdmin);
        editButton.setEnabled(isAdmin);

        // For Sorting
        JButton resetSortButton =  new JButton("RESET SORT");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        // For Sorting
        buttonPanel.add(resetSortButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            JFrame father = (JFrame) SwingUtilities.getWindowAncestor(this);
            new AddEditHotelWindow(father, null, this); //null = ADD Modus
        });
        //US-5
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a hotel to edit!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int columnCount = model.getColumnCount();
            Object[] rowData = new Object[columnCount];
            for (int i = 0; i < columnCount; i++) {
                rowData[i] = model.getValueAt(modelRow, i);
            }
            JFrame father = (JFrame) SwingUtilities.getWindowAncestor(HotelTable.this);
            new AddEditHotelWindow(father, rowData, HotelTable.this);
        });

        // For Reset-Sorting
        resetSortButton.addActionListener(e -> {
            TableUtils.resetSort(table);
        });


    }

    private void innitComponents() {
        // 1. Container for the top Space (Header + Search)
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));

        // add Header
        JLabel headerLabel = new JLabel("Hotels:");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        topContainer.add(headerLabel);

        //create Search-Panel
        JTextField searchField = new JTextField(20);
        searchField.setToolTipText("Search for Hotel Name...");

        // Event-Listener: Searches every time a key is pressed
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                // Column 2 is Hotelname in our Table
                TableUtils.filterTable(table, searchField.getText(), 2);
                // we give the Methode the table we are working in, the searchText, and in what column it should search
            }
        });

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Search Hotelname:");

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        topContainer.add(searchPanel);
        add(topContainer, BorderLayout.NORTH);

        // 2. Initialize the table
        model = new DefaultTableModel();
        table = new JTable();
        table.setModel(model);
        table.setDefaultEditor(Object.class, null); // makes the Table uneditable without edit button

        //activates Sorting for the HotelTable +
        TableUtils.enableSorting(table);

    }

    private void defineFrame() {
        setLayout(new BorderLayout());
    }

    // US- 4
    //Needs to be updated for the SQL Server, then with selects and creates, updates, inserts
    public Hotel addHotel(Hotel h) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            s.persist(h); // nach persist() hat h.getId() den Wert von der DB
            tx.commit();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not save: " + e.getMessage());
        }
        return h; // ID ist jetzt gesetzt
    }

    public void updateHotel(Hotel h) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            s.merge(h);
            tx.commit();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not update: " + e.getMessage());
        }
    }
    //IF Databse needs to be created again from scratch
    private void importFromTxt() {
        try {
            Scanner sc = new Scanner(new File(path));
            if (sc.hasNextLine()) {
                sc.nextLine(); // skip header
            }

            try (Session s = HibernateUtil.getSessionFactory().openSession()) {
                Transaction tx = s.beginTransaction();

                while (sc.hasNextLine()) {
                    String line = sc.nextLine().trim();
                    if (line.isEmpty()) {
                        continue;
                    }

                    String[] d = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    if (d.length < 11) {
                        continue;
                    }

                    try {
                        Hotel h = Hotel.builder()
                                .category(d[1].replace("\"", "").trim())
                                .name(d[2].replace("\"", "").trim())
                                .owner(d[3].replace("\"", "").trim())
                                .contact(d[4].replace("\"", "").trim())
                                .address(d[5].replace("\"", "").trim())
                                .city(d[6].replace("\"", "").trim())
                                .cityCode(d[7].replace("\"", "").trim())
                                .phone(d[8].replace("\"", "").trim())
                                .noRoom(Integer.parseInt(d[9].trim()))
                                .noBed(Integer.parseInt(d[10].trim()))
                                .lastReported(d.length > 11 ? d[11].trim() : "")
                                .build();

                        s.persist(h);
                    } catch (Exception e) {
                        System.out.println("Skipped line: " + line);
                    }
                }

                tx.commit();
            }
            sc.close();
            System.out.println("Import finished!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Import failed: " + e.getMessage());
        }
    }

    private void importIfEmpty() {
        boolean isEmpty = false;

        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Long count = s.createQuery("SELECT COUNT(h.id) FROM Hotel h", Long.class)
                    .uniqueResult();
            isEmpty = (count == 0);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Import-Check faileds: " + e.getMessage());
            return;
        }

        if (isEmpty) {
            System.out.println("Table empty → import from TXT...");
            importFromTxt(); // ← jetzt außerhalb der Session
        } else {
            System.out.println("Data is Database → No Import needed");
        }
    }

}
