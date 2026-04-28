package MasterTable;

import javax.swing.*;
import java.awt.*;

public class EditWindow extends JFrame {



    public EditWindow() {
        setTitle("Edit Hotel");
        setSize(300, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);


        setLayout(new GridLayout(5, 2, 10, 10));

        add(new JLabel("Name:"));
        add(new JTextField());

        add(new JLabel("City:"));
        add(new JTextField());

        add(new JLabel("Rooms:"));
        add(new JTextField());

        add(new JLabel("Beds:"));
        add(new JTextField());

        JButton saveButton = new JButton("Save");
        add(saveButton);



        setVisible(true);


    }

}
