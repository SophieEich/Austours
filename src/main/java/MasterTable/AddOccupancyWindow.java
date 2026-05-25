package MasterTable;

import org.hibernate.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import org.hibernate.Transaction;

public class AddOccupancyWindow extends JDialog {

    private OccupancyPanel parent;

    // US - 6 Enter transactional data (room/bed occupancy per month)

    JComboBox<Hotel> hotelSelect = new JComboBox<>(); // Empty dropdown for hotel selection
    //Occupancy data
    JComboBox<String> year  = new JComboBox<>(new String[] {"2024","2025","2026","2027", "2028", "2029", "2030", "2031"});
    JComboBox<String> month = new JComboBox<>(new String[]{"01","02","03","04","05","06","07","08","09","10","11","12"});
    JTextField roomOcc   = new JTextField();
    JTextField bedOcc    = new JTextField();


    //constructor
    public AddOccupancyWindow(OccupancyPanel parent) {
        super((JFrame) SwingUtilities.getWindowAncestor(parent), "Add Occupancy", true);
        this.parent = parent;

        defineFrame();
        initComponents();
        loadHotels();
        addComponents();

        setVisible(true);


    }
    private void addComponents() { //Columns
        JTextField hotelSearch = new JTextField();
        add(new JLabel("Search Hotelname: "));
        add(hotelSearch);

        add(new JLabel("Selected Hotel:"));
        add(hotelSelect);
        add(new JLabel("Year:"));
        add(year);
        add(new JLabel("Month:"));
        add(month);
        add(new JLabel("Room Occupancy:"));
        add(roomOcc);
        add(new JLabel("Bed Occupancy:"));
        add(bedOcc);
        //Placeholder for Grid
        add(new JLabel(""));

        // save button -> saveOccupancy
        JButton saveButton = new JButton("Save");
        //this line makes the Save Button react to enter -> if you press enter it will save
        this.getRootPane().setDefaultButton(saveButton);
        add(saveButton);

        //Search Logic
        hotelSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                String searchText = hotelSearch.getText().toLowerCase().trim();
                if(searchText.isEmpty()){
                    return;
                }

                for(int i = 0; i < hotelSelect.getItemCount(); i++) {
                    Hotel h = hotelSelect.getItemAt(i);
                    //Checks if typed text appears in Hotelname
                    if(h.getName().toLowerCase().contains(searchText)) {
                        hotelSelect.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });



        saveButton.addActionListener(e -> saveOccupancy());
    }

    private void initComponents() { //each label gets one column

        setLayout(new GridLayout(7, 2, 10, 10));
    }

    private void loadHotels() {
        hotelSelect.removeAllItems(); // ensures that line is empty
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            List<Hotel> hotels = s.createQuery("from Hotel", Hotel.class).list();
            for (Hotel h : hotels) {
                // Wir speichern das Hotel-Objekt oder einen String
                hotelSelect.addItem(h);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading hotels: " + e.getMessage());
        }
    }


    private void defineFrame() {
        setTitle("Add Occupancy");
        setSize(400, 450);
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void saveOccupancy() {
        // if required fields = empty
        if (roomOcc.getText().isEmpty() || bedOcc.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields!");
            return;
        }

        // Only positive numbers allowed
        int  roomOccVal;
        int  bedOccVal;

        try {
            roomOccVal = Integer.parseInt(roomOcc.getText().trim());
            bedOccVal  = Integer.parseInt(bedOcc.getText().trim());

            if (roomOccVal <= 0 || bedOccVal <= 0) {
                JOptionPane.showMessageDialog(this, "Values must be positive numbers!");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Year and Occupancy must be valid numbers! No decimal numbers");
            return;
        }
        //
        Hotel hotel = (Hotel) hotelSelect.getSelectedItem();

        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();


            //Hotel-Objekt gets loaded

            //LAST VALIDATION CHECK. CHECKS if no more beds or rooms are added than the hotel has in the HotelTable
            if (roomOccVal > hotel.getNoRoom()) {
                JOptionPane.showMessageDialog(this,
                        "Room occupancy cannot exceed total rooms!");
                return;
            }

            if (bedOccVal > hotel.getNoBed()) {
                JOptionPane.showMessageDialog(this,
                        "Bed occupancy cannot exceed total beds!");
                return;
            }


            Occupancy occ = Occupancy.builder()
                    .hotel(hotel)
                    .year(Integer.parseInt((String) year.getSelectedItem()))
                    .month(Integer.parseInt((String) month.getSelectedItem()))
                    .roomOccupancy(roomOccVal)
                    .bedOccupancy(bedOccVal)
                    .build();

            s.persist(occ);
            tx.commit();

            parent.fillTable(); // refreshes table in panel
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving to DB: " + e.getMessage());
        }
    }



}
