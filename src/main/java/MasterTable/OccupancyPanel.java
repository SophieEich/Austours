package MasterTable;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import javax.swing.BoxLayout;
import MasterTable.Login.UserRole;
import MasterTable.Login.UsersHibernate;
import org.hibernate.Session;
import java.awt.event.KeyEvent;
import java.util.List;



public class OccupancyPanel extends JPanel {

    JTable table;
    DefaultTableModel model;
    private final UsersHibernate currentUser;


    // ── US-02 / US-10: Filter controls ────────────────────────────────────────────────
    // Senior User can filter the occupancy summary by hotel, date range, and category

    JComboBox<String>   hotelFilter    = new JComboBox<>();
    JComboBox<String>   fromMonth      = new JComboBox<>(new String[]{"01","02","03","04","05","06","07","08","09","10","11","12"});
    JComboBox<String>   fromYear       = new JComboBox<>(new String[] {"2024","2025","2026","2027", "2028", "2029", "2030", "2031"});
    JComboBox<String>   toMonth        = new JComboBox<>(new String[]{"01","02","03","04","05","06","07","08","09","10","11","12"});
    JComboBox<String>   toYear       = new JComboBox<>(new String[] {"2024","2025","2026","2027", "2028", "2029", "2030", "2031"});
    JComboBox<Category> categoryFilter = new JComboBox<>(Category.values());



    // ─────────────────────────────────────────────────────────────────────────

    OccupancyPanel(UsersHibernate user) {
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
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            // JOIN FETCH lädt die Hotel-Daten sofort mit (verhindert LazyInitializationException)
            List<Occupancy> list = s.createQuery("SELECT o FROM Occupancy o JOIN FETCH o.hotel", Occupancy.class).list();

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
        } catch (Exception e) {
            e.printStackTrace(); // Zeigt SQL-Fehler in der Konsole
        }
    }


    // US-10
    // US-02: Populates the hotel dropdown from hotels.txt for the hotel filter
    private void loadHotel() {
        hotelFilter.removeAllItems();
        hotelFilter.addItem("ALL");
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            List<Hotel> hotels = s.createQuery("from Hotel", Hotel.class).list();
            for (Hotel h : hotels) {
                hotelFilter.addItem(h.getId() + " - " + h.getName());
            }
        } catch (Exception e) {
            System.out.println("Could not load filter Hotels.");
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

