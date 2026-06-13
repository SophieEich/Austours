package MasterTable.gui.occupancy;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import javax.swing.BoxLayout;

import MasterTable.PdfExporter;
import MasterTable.entity.user.UserRole;
import MasterTable.entity.user.UsersHibernate;
import MasterTable.entity.Category;
import MasterTable.entity.Hotel;
import MasterTable.entity.Occupancy;
import MasterTable.util.TableUtils;
import java.awt.event.KeyEvent;
import java.util.List;
import MasterTable.dao.OccupancyDAO;



public class OccupancyPanel extends JPanel {

    public JTable table;
    public DefaultTableModel model;
    private final UsersHibernate currentUser;
    private final OccupancyDAO occupancyDAO = new OccupancyDAO();


    // ── US-02 / US-10: Filter controls ────────────────────────────────────────────────
    // Senior User can filter the occupancy summary by hotel, date range, and category

    public JComboBox<String>   hotelFilter    = new JComboBox<>();
    public JComboBox<String>   fromMonth      = new JComboBox<>(new String[]{"01","02","03","04","05","06","07","08","09","10","11","12"});
    public JComboBox<String>   fromYear       = new JComboBox<>(new String[] {"2024","2025","2026","2027", "2028", "2029", "2030", "2031"});
    public JComboBox<String>   toMonth        = new JComboBox<>(new String[]{"01","02","03","04","05","06","07","08","09","10","11","12"});
    public JComboBox<String>   toYear       = new JComboBox<>(new String[] {"2024","2025","2026","2027", "2028", "2029", "2030", "2031"});
    public JComboBox<Category> categoryFilter = new JComboBox<>(Category.values());



    // ─────────────────────────────────────────────────────────────────────────

    public OccupancyPanel(UsersHibernate user) {
        this.currentUser = user;
        definePanel();
        initComponents();
        addComponents();
        loadHotel();
        fillTable();
    }
    // US-10
    // US-02: Reads occupancy data from file and applies all active filter criteria
    // Displays room and bed occupancy per month in the summary table
    public void fillTable() {
        if (model == null) return;
        model.setRowCount(0);

        // 1. Filter-Werte aus der UI holen
        String selectedHotel = (String) hotelFilter.getSelectedItem();
        Long filterHotelId = null;
        if (selectedHotel != null && !selectedHotel.equals("ALL")) {
            filterHotelId = Long.parseLong(selectedHotel.split(" - ")[0].trim());
        }


        Category selectedCategory = (Category) categoryFilter.getSelectedItem();

        // 2. Datenbank-Abfrage


        List<Occupancy> list = occupancyDAO.getAllOccupancies();

        int fromYearInt = Integer.parseInt((String) fromYear.getSelectedItem());
        int toYearInt = Integer.parseInt((String) toYear.getSelectedItem());

        int fromMonthInt = Integer.parseInt((String) fromMonth.getSelectedItem());
        int toMonthInt = Integer.parseInt((String) toMonth.getSelectedItem());

        int filterFrom = fromYearInt * 100 + fromMonthInt;
        int filterTo   = toYearInt * 100 + toMonthInt;

        for (Occupancy occ : list) {
            Hotel h = occ.getHotel();

            // --- FILTER PRÜFUNG ---
            if (filterHotelId != null && !h.getId().equals(filterHotelId)) continue;
            if (selectedCategory != null && selectedCategory != Category.ALL) {
                if (!h.getCategory().equals(selectedCategory.toString())) continue;
            }

            // Datum-Filter
            int rowDate = occ.getYear() * 100 + occ.getMonth();
            if (rowDate < filterFrom || rowDate > filterTo) continue;

            // 3. In Tabelle schreiben
            model.addRow(new Object[]{
                    h.getId(),
                    h.getName(),
                    occ.getYear(),
                    occ.getMonth(),
                    occ.getRoomOccupancy(),
                    occ.getBedOccupancy()
            });
        }

    }


    // US-10
    // US-02: Populates the hotel dropdown from hotels.txt for the hotel filter
    private void loadHotel() {
        hotelFilter.removeAllItems();
        hotelFilter.addItem("ALL");
        List<Hotel> hotels = occupancyDAO.getAllHotels();
        for (Hotel h : hotels) {
            hotelFilter.addItem(h.getId() + " - " + h.getName());
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

        // US-07: Export current filtered table as A4 PDF
        JButton exportButton = new JButton("EXPORT PDF");
        exportButton.addActionListener(e -> PdfExporter.export(this));
        buttonPanel.add(exportButton);

        // Only admins should add/edit/delete
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
        addButton.setEnabled(isAdmin);

        JButton resetSortButton = new JButton("RESET SORT");
        resetSortButton.addActionListener(e -> TableUtils.resetSort(table));
        buttonPanel.add(resetSortButton);
        add(buttonPanel, BorderLayout.SOUTH);

        JTextField searchField = new JTextField(20);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                TableUtils.filterTable(table, searchField.getText(), 1);
            }
        });
        JLabel searchLabel = new JLabel("Search Hotelname:");
        row2.add(searchLabel);
        row2.add(searchField);
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

        table.setDefaultEditor(Object.class, null); // makes the Table uneditable without edit button
        TableUtils.enableSorting(table);
    }

    private void definePanel() {
        setLayout(new BorderLayout());
    }


}

