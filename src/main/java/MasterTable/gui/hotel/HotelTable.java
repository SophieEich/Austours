package MasterTable.gui.hotel;

import MasterTable.entity.user.UserRole;
import MasterTable.entity.user.UsersHibernate;
import MasterTable.entity.Hotel;
import MasterTable.util.HibernateUtil;
import MasterTable.util.TableUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import MasterTable.dao.HotelDAO;

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
    private final HotelDAO hotelDAO = new HotelDAO();

    private final UsersHibernate currentUser;
    private final Timer clearSelectionTimer = new Timer(9000, e -> {
        table.clearSelection();
    });

    public HotelTable(UsersHibernate user) {
        this.currentUser = user;

        //USER STORY 3
        defineFrame();
        initComponents();
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

        //Only Admin can add/edit
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
        addButton.setEnabled(isAdmin);
        editButton.setEnabled(isAdmin);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);

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
            //selection will be immediately cleared after window is disposed
            //table.clearSelection();
        });

        // Delete Button US11
        JButton deleteButton = new JButton("DELETE HOTEL");
        deleteButton.setEnabled(isAdmin);
        buttonPanel.add(deleteButton);
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a hotel to delete!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this hotel and all linked occupancy data?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                int modelRow = table.convertRowIndexToModel(selectedRow);
                Long id = Long.parseLong(model.getValueAt(modelRow, 0).toString());
                deleteHotel(id);
                fillTable();
            }
        });
    }

    private void initComponents() {
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

        //After 10 sec selection will be cleared automatically
        clearSelectionTimer.setRepeats(false);

        table.getSelectionModel().addListSelectionListener(e ->{
            if (!e.getValueIsAdjusting()) {
                clearSelectionTimer.restart();
            }
        });


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
        table.setRowHeight(25);


    }

    private void defineFrame() {
        setLayout(new BorderLayout());
    }

    public Hotel addHotel(Hotel h) {
        return hotelDAO.addHotel(h);
    }

    public void updateHotel(Hotel h) {
        hotelDAO.updateHotel(h);
    }

    public void deleteHotel(Long id) {
        hotelDAO.deleteHotel(id);
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
                                .noRooms(Integer.parseInt(d[9].trim()))
                                .noBeds(Integer.parseInt(d[10].trim()))
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
