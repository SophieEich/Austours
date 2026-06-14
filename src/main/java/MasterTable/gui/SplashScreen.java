package MasterTable.gui;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {

    private JLabel loadingLabel;
    private Timer dotTimer;

    public SplashScreen() {
        setSize(500, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Hintergrund Gradient — dunkelblau nach hellblau
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(31, 78, 150),
                        getWidth(), getHeight(), new Color(173, 216, 230)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Punkte Muster
                g2d.setColor(new Color(255, 255, 255, 30));
                for (int x = 0; x < getWidth(); x += 20) {
                    for (int y = 0; y < getHeight(); y += 20) {
                        g2d.fillOval(x, y, 4, 4);
                    }
                }
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createLineBorder(new Color(31, 78, 150), 2));

        // Logo
        JLabel logoLabel;
        java.net.URL logoUrl = getClass().getResource("/images/2026-LATP_Logo.jpg");
        if (logoUrl != null) {
            ImageIcon raw = new ImageIcon(logoUrl);
            Image scaled = raw.getImage().getScaledInstance(300, 150, Image.SCALE_SMOOTH);
            logoLabel = new JLabel(new ImageIcon(scaled), SwingConstants.CENTER);
        } else {
            logoLabel = new JLabel("NOE-Hotels", SwingConstants.CENTER);
            logoLabel.setFont(new Font("Arial", Font.BOLD, 32));
            logoLabel.setForeground(Color.WHITE);
        }

        // Animierter Loading Text
        loadingLabel = new JLabel("Loading", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Arial", Font.BOLD, 16));
        loadingLabel.setForeground(Color.WHITE);
        loadingLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // Punkte Animation — wechselt alle 500ms
        int[] dotCount = {0};
        dotTimer = new Timer(500, e -> {
            dotCount[0] = (dotCount[0] + 1) % 4;
            String dots = ".".repeat(dotCount[0]);
            loadingLabel.setText("Loading" + dots);
        });
        dotTimer.start();

        panel.add(logoLabel, BorderLayout.CENTER);
        panel.add(loadingLabel, BorderLayout.SOUTH);

        add(panel);
    }

    public void dispose() {
        if (dotTimer != null) dotTimer.stop();
        super.dispose();
    }
}