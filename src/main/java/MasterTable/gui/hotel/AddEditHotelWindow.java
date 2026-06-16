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

    // US14: Attribute checkboxes
    JCheckBox familyFriendly = new JCheckBox();
    JCheckBox petFriendly = new JCheckBox();
    JCheckBox spa = new JCheckBox();
    JCheckBox fitness = new JCheckBox();

    JButton saveButton = new JButton("Save");


    public AddEditHotelWindow(JFrame father, Object[] rowData, HotelTable parent) {
        super(father, rowData != null ? "Edit Hotel" : "Add Hotel", true);
        this.rowData = rowData;
        this.parent = parent;


        defineDialog();
        prefillFields();
        addComponents();
        setVisible(true);
    }


    private void defineDialog() {
        setSize(500, 500);
        // setDefaultCloseOperation(DISPOSE_ON_CLOSE); NOT Needed for JDialouge
        setLocationRelativeTo(getOwner());
        // US14: 15 rows now (10 text fields + 4 checkboxes + 1 save button row)
        setLayout(new GridLayout(15, 2, 15, 12));
    }



        // US14: Pre-fill attribute checkboxes from rowData
        familyFriendly.setSelected(isChecked(rowData[12]));
        petFriendly.setSelected(isChecked(rowData[13]));
        spa.setSelected(isChecked(rowData[14]));
        fitness.setSelected(isChecked(rowData[15]));


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

        // US14: Attribute checkboxes
        add(new JLabel("Family Friendly:"));
        add(familyFriendly);
        add(new JLabel("Pet Friendly:"));
        add(petFriendly);
        add(new JLabel("Spa:"));
        add(spa);
        add(new JLabel("Fitness:"));
        add(fitness);

        add(new JLabel(""));
        add(saveButton);

        //this line makes the Save Button react to enter -> if you press enter it will save
        this.getRootPane().setDefaultButton(saveButton);

        saveButton.addActionListener(e -> onSave());
    }





        //Only positve numebers
        int roomCount;
        int bedCount;
        int cityCodeCheck;
        int phoneCheck;

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

        try {
            phoneCheck = Integer.parseInt(phone.getText().trim());
            if (phoneCheck <= 0) {
                JOptionPane.showMessageDialog(this, "Phone must be positive a number!");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Phone must be a number!");
            return;
        }


        String today = LocalDate.now().toString();
        String selectedCategory = category.getSelectedItem().toString();


// US14: Pass attribute checkbox values to the builder
        Hotel h = Hotel.builder()
                .category(category.getSelectedItem().toString())
                .name(name.getText().trim())
                .owner(owner.getText().trim())
                .contact(contact.getText().trim())
                .address(address.getText().trim())
                .city(city.getText().trim())
                .cityCode(citycode.getText().trim())
                .phone(phone.getText().trim())
                .noRooms(roomCount)
                .noBeds(bedCount)
                .lastReported(today)
                .familyFriendly(familyFriendly.isSelected())
                .petFriendly(petFriendly.isSelected())
                .spa(spa.isSelected())
                .fitness(fitness.isSelected())
                .build();


        } else {
            parent.addHotel(h);
            parent.fillTable(); // database will be loaded again, done
            JOptionPane.showMessageDialog(null,
                    "Hotel '" + name.getText() + "' was successfully added!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

        }
        dispose();
    }


    private boolean hasChanges() {
        if (!isEditing) return true; // always save with Add

        String savedCategory = rowData[1].toString();

        boolean textChanged =
                !name.getText().equals(rowData[2].toString()) ||
                        !category.getSelectedItem().toString().equals(savedCategory) ||
                        !owner.getText().equals(rowData[3].toString()) ||
                        !contact.getText().equals(rowData[4].toString()) ||
                        !address.getText().equals(rowData[5].toString()) ||
                        !city.getText().equals(rowData[6].toString()) ||
                        !citycode.getText().equals(rowData[7].toString()) ||
                        !phone.getText().equals(rowData[8].toString()) ||
                        !rooms.getText().equals(rowData[9].toString()) ||
                        !beds.getText().equals(rowData[10].toString());

        // US14: Also compare checkbox state for the 4 attributes
        boolean oldFamily = isChecked(rowData[12]);
        boolean oldPet = isChecked(rowData[13]);
        boolean oldSpa = isChecked(rowData[14]);
        boolean oldFitness = isChecked(rowData[15]);

        boolean attrChanged =
                familyFriendly.isSelected() != oldFamily ||
                        petFriendly.isSelected() != oldPet ||
                        spa.isSelected() != oldSpa ||
                        fitness.isSelected() != oldFitness;

        return textChanged || attrChanged;
    }

    private boolean isChecked(Object value) {
        return Boolean.TRUE.equals(value)
                || "true".equalsIgnoreCase(String.valueOf(value))
                || "✓".equals(String.valueOf(value));
    }
}