package MasterTable;

import javax.swing.*;

public class MainWindow extends JFrame {

    MainWindow() {
        defineFrame();
        addComponents();
    }

    private void addComponents() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Summary", new SummaryPanel());
        tabs.addTab("Hotels", new HotelTable());
        tabs.addTab("Occupancy", new OccupancyPanel());

        add(tabs);
    }

    private void defineFrame() {
        setTitle("Information");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

}
