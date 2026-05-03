package MasterTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class AddEditHotelWindow extends JFrame {

    //US-4
    private DefaultTableModel model;
    private HotelTable parent;

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


    public AddEditHotelWindow(DefaultTableModel model, HotelTable parent, int editRow) {
        this.model = model;
        this.parent = parent;


        boolean isEditing;
        isEditing = defineFrame(editRow);

        //Pre filled fields for isEditing
        if (isEditing) {
            String savedCategory = model.getValueAt(editRow, 1).toString();
            for (Category cat : Category.values()) {
                if (cat.toString().equals(savedCategory)) {
                    category.setSelectedItem(cat);
                    break;
                }
            }

            name    .setText(model.getValueAt(editRow, 2).toString());
            owner   .setText(model.getValueAt(editRow, 3).toString());
            contact .setText(model.getValueAt(editRow, 4).toString());
            address .setText(model.getValueAt(editRow, 5).toString());
            city    .setText(model.getValueAt(editRow, 6).toString());
            citycode.setText(model.getValueAt(editRow, 7).toString());
            phone   .setText(model.getValueAt(editRow, 8).toString());
            rooms   .setText(model.getValueAt(editRow, 9).toString());
            beds    .setText(model.getValueAt(editRow, 10).toString());
        }// only one "}" because the add(....) should always be added
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


        JButton saveButton = new JButton("Save");
        add(saveButton);

        saveButton.addActionListener(e -> {

            //Validation - all fields must be filled in
            if (name.getText().isEmpty() ||
            owner.getText().isEmpty() ||
            contact.getText().isEmpty() ||
            address.getText().isEmpty()||
            city.getText().isEmpty()||
            citycode.getText().isEmpty()||
            phone.getText().isEmpty()||
            rooms.getText().isEmpty() ||
            beds.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all the fields");
                return;
            }

            //Only positve numebers
            int roomCount;
            int bedCount;

            try{
                roomCount = Integer.parseInt(rooms.getText().trim());
                bedCount  = Integer.parseInt(beds.getText().trim());
                if(roomCount <= 0 || bedCount <=0){
                    JOptionPane.showMessageDialog(this, "Rooms and Beds must be positive numbers!");
                    return;
                }
            }catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(this, "Rooms and Beds must be numbers!");
                return;
            }




            String today = LocalDate.now().toString();
            String selectedCategory = category.getSelectedItem().toString();

            if (category.getSelectedItem().equals(Category.ALL)) {
                JOptionPane.showMessageDialog(this, "Please select a Star rating!");
                return;
            }

            if(isEditing) {
                // UPDATE existing row — keep original ID
                model.setValueAt(selectedCategory, editRow, 1);
                model.setValueAt(name.getText(),     editRow, 2);
                model.setValueAt(owner.getText(),    editRow, 3);
                model.setValueAt(contact.getText(),  editRow, 4);
                model.setValueAt(address.getText(),  editRow, 5);
                model.setValueAt(city.getText(),     editRow, 6);
                model.setValueAt(citycode.getText(), editRow, 7);
                model.setValueAt(phone.getText(),    editRow, 8);
                model.setValueAt(rooms.getText(),    editRow, 9);
                model.setValueAt(beds.getText(),     editRow, 10);
                model.setValueAt(today,              editRow, 11); // update date

            } else {
                Object[] newRow = {

                        getNextId(model), //ID automatic
                        selectedCategory,
                        name.getText(),
                        owner.getText(),
                        contact.getText(),
                        address.getText(),
                        city.getText(),
                        citycode.getText(),
                        phone.getText(),
                        rooms.getText(),
                        beds.getText(),
                        today,
                };

                model.addRow(newRow);
            }
            parent.saveAlltoFile();
            dispose();


        });


        setVisible(true);

    }

    private boolean defineFrame(int editRow) {
        boolean isEditing;
        if (editRow >= 0){
            isEditing = true;
        }else {
            isEditing = false;
        }
        //setTitle(isEditing ? "Edit Hotel" : "Add Hotel");
        if(isEditing) {
            setTitle("Edit Hotel");
        }else {
            setTitle("Add Hotel");
        }
        setSize(500, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(11, 2, 15, 17));
        return isEditing;
    }

    private String getNextId(DefaultTableModel model) {
        int maxId = 0; //we are searching for the highest id
        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                // Get Value from first column (ID)
                Object value = model.getValueAt(i, 0);
                if (value != null) {
                    int currentId = Integer.parseInt(value.toString().trim());
                    if (currentId > maxId) {
                        maxId = currentId;
                    }
                }
            } catch (Exception e) {
                // ignore non-integer values
            }
        }
        return String.valueOf(maxId + 1);
    }

}
