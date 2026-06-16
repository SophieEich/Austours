package MasterTable;

import MasterTable.gui.LoginWindow;
import MasterTable.entity.user.UsersHibernate;
import MasterTable.gui.MainWindow;
import MasterTable.util.HibernateUtil;
import MasterTable.gui.SplashScreen;


import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->{

            //Splash Screen
            SplashScreen splash = new SplashScreen();
            splash.setVisible(true);
            new Thread(() -> {
                HibernateUtil.getSessionFactory(); //SessionFactory gets prepared so LogIn is faster

                SwingUtilities.invokeLater(() -> {
                    splash.dispose();
                    LoginWindow login = new LoginWindow();
                    UsersHibernate user = login.getLoggedInUser();
                    if(user != null){
                        new MainWindow(user).setVisible(true);
                    }else {
                        System.exit(0); // Window closes without Login
                    }


                });
            }).start();
        });
    }
}
