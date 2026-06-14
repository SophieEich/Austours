package MasterTable.gui;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {

    public SplashScreen() {
        setSize(500, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel logoLabel;
        java.net.URL logoUrl = getClass().getResource("/images/2026-LATP_Logo.jpg");
        if (logoUrl != null) {
            ImageIcon raw = new ImageIcon(logoUrl);
            Image scaled = raw.getImage().getScaledInstance(300, 150, Image.SCALE_SMOOTH);
            logoLabel = new JLabel(new ImageIcon(scaled), SwingConstants.CENTER);
        } else {
            logoLabel = new JLabel("NOE-Hotels", SwingConstants.CENTER);
            logoLabel.setFont(new Font("Arial", Font.BOLD, 32));
        }


        panel.add(logoLabel, BorderLayout.CENTER);
        panel.add(loadingLabel, BorderLayout.SOUTH);

        add(panel);
    }

    }
}