package MasterTable.gui;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {

    public SplashScreen() {
        // Size of Splash Screens
        setSize(500, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // load Logo
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

        // Loading Text
        JLabel loadingLabel = new JLabel("Loading...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        loadingLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Background white
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(31, 78, 150), 2));
        panel.add(logoLabel, BorderLayout.CENTER);
        panel.add(loadingLabel, BorderLayout.SOUTH);

        add(panel);
    }

    public void showSplash(int milliseconds) {
        setVisible(true);
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setVisible(false);
        dispose();
    }
}