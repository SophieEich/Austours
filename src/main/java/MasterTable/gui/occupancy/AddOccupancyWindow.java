package MasterTable.gui.occupancy;

import MasterTable.entity.Hotel;
import MasterTable.entity.Occupancy;
import MasterTable.dao.OccupancyDAO;
import MasterTable.entity.user.UserRole;
import MasterTable.entity.user.UsersHibernate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class AddOccupancyWindow extends JDialog {

    private OccupancyPanel parent;
    private final OccupancyDAO occupancyDAO = new OccupancyDAO();
    private final UsersHibernate currentUser;

    // US - 6 Enter transactional data (room/bed occupancy per month)

    JComboBox<Hotel> hotelSelect = new JComboBox<>(); // Empty dropdown for hotel selection
    //Occupancy data
    JComboBox<String> year  = new JComboBox<>(new String[]{"2024","2025","2026","2027", "2028", "2029", "2030", "2031"});
    JComboBox<String> month = new JComboBox<>(new String[]{"01","02","03","04","05","06","07","08","09","10","11","12"});
    JTextField roomOcc   = new JTextField();
    JTextField bedOcc    = new JTextField();


    //constructor
    public AddOccupancyWindow(OccupancyPanel parent, UsersHibernate user) {
        super((JFrame) SwingUtilities.getWindowAncestor(parent), "Add Occupancy", true);//US-6
        this.parent = parent;
        this.currentUser = user;

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

        //Search Logic - Change Request US-29
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

        setLayout(new GridLayout(8, 2, 10, 10));
    }
    //
    private void loadHotels() {
        List<Hotel> hotels = occupancyDAO.getAllHotels();
        for (Hotel h : hotels) {
            hotelSelect.addItem(h);
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
        //__________________________________________________________________ US-26
        Hotel hotel = (Hotel) hotelSelect.getSelectedItem();

        if (currentUser.getRole() == UserRole.HOTEL_REPRESENTATIVE) {

            if (hotel.getRepresentative() == null ||
                    !hotel.getRepresentative().getId().equals(currentUser.getId())) {

                JOptionPane.showMessageDialog(this,
                        "You can only add occupancy for your own hotels!");
                return;
            }
        }

        if (roomOccVal > hotel.getNoRooms()) {
            JOptionPane.showMessageDialog(this, "Room occupancy cannot exceed total rooms!");
            return;
        }

        if (bedOccVal > hotel.getNoBeds()) {
            JOptionPane.showMessageDialog(this, "Bed occupancy cannot exceed total beds!");
            return;
        }

        Occupancy occ = Occupancy.builder()
                .hotel(hotel)
                .year(Integer.parseInt((String) year.getSelectedItem()))
                .month(Integer.parseInt((String) month.getSelectedItem()))
                .roomOccupancy(roomOccVal)
                .bedOccupancy(bedOccVal)
                .build();

        occupancyDAO.saveOccupancy(occ);
        parent.fillTable();
        dispose();
    }
}
