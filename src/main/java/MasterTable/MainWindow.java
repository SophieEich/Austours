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
        setTitle("NOE-Hotels");
        setSize(930, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    //Help Tab
    private void addComponent() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Summary", new SummaryPanel());
        tabs.addTab("Hotels", new HotelTable());
        tabs.addTab("Occupancy", new OccupancyPanel());
        tabs.addTab("Help", new HilfeTab());

        add(tabs);
    }

}
