package MasterTable;

import MasterTable.gui.LoginWindow;
import MasterTable.entity.user.UsersHibernate;
import MasterTable.gui.MainWindow;
import MasterTable.util.HibernateUtil;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->{
            HibernateUtil.getSessionFactory(); //SessionFactory gets prepared so LogIn is faster
            LoginWindow login = new LoginWindow();
            UsersHibernate user = login.getLoggedInUser();

            if(user != null){
                new MainWindow(user).setVisible(true);
            }else {
                System.exit(0); // Window closes without Login
            }
        });

    }
}
