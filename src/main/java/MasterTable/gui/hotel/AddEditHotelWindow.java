package MasterTable.gui.hotel;


import MasterTable.entity.Category;
import MasterTable.entity.Hotel;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class AddEditHotelWindow extends JDialog {

    //US-4
    private HotelTable parent;
    private Object[] rowData;
    private boolean isEditing;

    JTextField name = new JTextField();
    JComboBox<Category> category = new JComboBox<>(Category.values());
    JTextField owner = new JTextField();
    JTextField contact = new JTextField();
    JTextField address = new JTextField();
    JTextField city = new JTextField();
    JTextField citycode = new JTextField();
    JTextField phone = new JTextField();
    JTextField rooms = new JTextField();
    JTextField beds = new JTextField();

    JButton saveButton = new JButton("Save");


    public AddEditHotelWindow(JFrame father, Object[] rowData, HotelTable parent) {
        super(father, rowData != null ? "Edit Hotel" : "Add Hotel", true);
        this.rowData = rowData;
        this.parent = parent;
        this.isEditing = (rowData != null);

        defineDialog();
        prefillFields();
        addComponents();
        setVisible(true);
    }



    private void defineDialog() {
        setSize(500, 500);
        // setDefaultCloseOperation(DISPOSE_ON_CLOSE); NOT Needed for JDialouge
        setLocationRelativeTo(getOwner());
        setLayout(new GridLayout(11, 2, 15, 17));
    }

    private void prefillFields() {
        //Pre filled fields for isEditing
        if (!isEditing) {
            return; // that would be an add -> nothing to prefill
        }
        String savedCategory = rowData[1].toString();
        for (Category cat : Category.values()) {
            if (cat.toString().equals(savedCategory)) {
                category.setSelectedItem(cat);
                break;
            }
        }

        name    .setText(rowData[2].toString());
        owner   .setText(rowData[3].toString());
        contact .setText(rowData[4].toString());
        address .setText(rowData[5].toString());
        city    .setText(rowData[6].toString());
        citycode.setText(rowData[7].toString());
        phone   .setText(rowData[8].toString());
        rooms   .setText(rowData[9].toString());
        beds    .setText(rowData[10].toString());

    }

    private void addComponents() {
        add(new JLabel("Name:"));
        add(name);
        add(new JLabel("Category:"));
        add(category);
        add(new JLabel("Owner:"));
        add(owner);
        add(new JLabel("Contact:"));
        add(contact);
        add(new JLabel("Address:"));
        add(address);
        add(new JLabel("City:"));
        add(city);
        add(new JLabel("Citycode:"));
        add(citycode);
        add(new JLabel("Phone:"));
        add(phone);
        add(new JLabel("Number of Rooms:"));
        add(rooms);
        add(new JLabel("Number of Beds:"));
        add(beds);

        add(new JLabel(""));
        add(saveButton);

        //this line makes the Save Button react to enter -> if you press enter it will save
        this.getRootPane().setDefaultButton(saveButton);

        saveButton.addActionListener(e -> onSave());
    }





    public void onSave() {
        //Validation - all fields must be filled in
        if (name.getText().isEmpty() ||
                owner.getText().isEmpty() ||
                contact.getText().isEmpty() ||
                address.getText().isEmpty() ||
                city.getText().isEmpty() ||
                citycode.getText().isEmpty() ||
                phone.getText().isEmpty() ||
                rooms.getText().isEmpty() ||
                beds.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all the fields");
            return;
        }

        if (category.getSelectedItem().equals(Category.ALL)) {
            JOptionPane.showMessageDialog(this, "Please select a Star rating!");
            return;
        }

        //Only positve numebers
        int roomCount;
        int bedCount;
        int cityCodeCheck;

        try {
            roomCount = Integer.parseInt(rooms.getText().trim());
            bedCount = Integer.parseInt(beds.getText().trim());
            if (roomCount <= 0 || bedCount <= 0) {
                JOptionPane.showMessageDialog(this, "Rooms and Beds must be positive numbers!");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Rooms and Beds must be numbers!");
            return;
        }

        try {
            cityCodeCheck = Integer.parseInt(citycode.getText().trim());
            if (cityCodeCheck <= 0) {
                JOptionPane.showMessageDialog(this, "Citycode must be positive a number!");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Citycode must be a number!");
            return;
        }




        String today = LocalDate.now().toString();
        String selectedCategory = category.getSelectedItem().toString();



        Hotel h = Hotel.builder()
                .category(selectedCategory)
                .name(name.getText())
                .owner(owner.getText())
                .contact(contact.getText())
                .address(address.getText())
                .city(city.getText())
                .cityCode(citycode.getText())
                .phone(phone.getText())
                .noRooms(roomCount)
                .noBeds(bedCount)
                .lastReported(today)
                .build();

        if (isEditing) {
            Long id = Long.parseLong(rowData[0].toString());
            h.setId(id);
            parent.updateHotel(h);
        } else {
            parent.addHotel(h);
        }
        parent.fillTable(); // ← DB neu laden, fertig
        dispose();
    }
}

