package MasterTable.gui;

import MasterTable.gui.occupancy.OccupancyPanel;
import MasterTable.entity.user.UsersHibernate;
import MasterTable.gui.hotel.HotelTable;
import MasterTable.gui.summary.SummaryPanel;
import MasterTable.gui.helptab.HelpTab;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class MainWindow extends JFrame {

    // US17: Höhe des Logo-Banners im Header (in Pixeln)
    private static final int HEADER_LOGO_HEIGHT = 70;
    private final UsersHibernate currentUser;

    public MainWindow(UsersHibernate user) {
        this.currentUser = user;
        defineFrame();
        addComponents();
    }

    private void addComponents() {
        // US17: Header mit LATP / NOE-TO Logo
        add(createHeader(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Summary", new SummaryPanel());
        tabs.addTab("Hotels", new HotelTable(currentUser));
        tabs.addTab("Occupancy", new OccupancyPanel(currentUser));
        tabs.addTab("Help", new HelpTab());

        add(tabs, BorderLayout.CENTER);
    }

    // US17: baut das Header-Panel mit dem Logo
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);

        URL logoUrl = getClass().getResource("/images/2026-LATP_Logo.jpg");
        if (logoUrl != null) {
            ImageIcon raw = new ImageIcon(logoUrl);
            // Höhe fix, Breite proportional -> sauberes Skalieren bei verschiedenen Fenstergrößen
            Image scaled = raw.getImage()
                    .getScaledInstance(-1, HEADER_LOGO_HEIGHT, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaled));
            header.add(logoLabel, BorderLayout.WEST);
        } else {
            // Fallback,if file is not in classpath
            JLabel fallback = new JLabel("Lower Austria Tourist Portal");
            fallback.setFont(new Font("Arial", Font.BOLD, 18));
            header.add(fallback);
        }
        //User top right
        JLabel userLabel = new JLabel(
                currentUser.getUsername() + " (" + currentUser.getRole()+ ") ",
                SwingConstants.RIGHT
        );
        userLabel.setFont(new Font("Arial", Font.BOLD, 12));
        header.add(userLabel, BorderLayout.EAST);

        return header;
    }

    private void defineFrame() {
        setTitle("NOE-Hotels");
        setSize(930, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // BorderLayout ist Default für JFrame.contentPane -> kein setLayout nötig
    }




}
