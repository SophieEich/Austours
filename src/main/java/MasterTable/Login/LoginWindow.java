package MasterTable.Login;

import MasterTable.HibernateUtil;
import org.hibernate.Session;

import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JDialog {

    private JTextField usernamefield = new JTextField(15);
    private JPasswordField passwordfield = new JPasswordField(15);
    private UsersHibernate loggedInUser = null;

    public LoginWindow() {
        setTitle("Login");
        setModal(true);
        setSize(350, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        // Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10,10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        // Input Fields
        JPanel inputPanel =  new JPanel(new GridLayout(2,2, 10, 10));
        inputPanel.add(new JLabel("Username:"));
        inputPanel.add(usernamefield);
        inputPanel.add(new JLabel("Password:"));
        inputPanel.add(passwordfield);

        //Login Button
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(0, 35));
        getRootPane().setDefaultButton(loginButton);
        loginButton.addActionListener(e -> doLogin());

        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(loginButton, BorderLayout.SOUTH);

        add(mainPanel);

        setVisible(true);
    }

    private void doLogin() {
        String username = usernamefield.getText().trim();
        String password = new String(passwordfield.getPassword());

        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            UsersHibernate user = session.createQuery(
                    "FROM UsersHibernate uh WHERE uh.username = :uh AND uh.password = :p", UsersHibernate.class)
                    .setParameter("uh", username)
                    .setParameter("p", password)
                    .uniqueResult();

            if (user != null) {
                loggedInUser = user;
                dispose();
            }else {
                JOptionPane.showMessageDialog(this, "Login Failed - Invalid username or password!");
            }

        }

    }

    public UsersHibernate getLoggedInUser() {
        return loggedInUser;
    }


}
