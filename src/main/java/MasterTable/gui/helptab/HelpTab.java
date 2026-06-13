package MasterTable.gui.helptab;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

//US 18
public class HelpTab extends JPanel {
    public HelpTab() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextArea textArea = new JTextArea(
                "=== LOWER AUSTRIA TOURIST PORTAL - HELP ===\n" +
                        "\n" +
                        "--- FOR NOE-TO SENIOR USERS ---\n" +
                        "\n" +
                        "HOTEL MASTER DATA\n" +
                        "- Add Hotel:    Click 'Add Hotel' to create a new entry.\n" +
                        "- Edit Hotel:   Select a hotel from the list and click 'Edit'.\n" +
                        "- Delete Hotel: Select a hotel, click 'Delete' and confirm.\n" +
                        "\n" +
                        "TRANSACTIONAL DATA\n" +
                        "- Add Entry:  Select a hotel and enter room/bed occupancy for the month.\n" +
                        "- Edit Entry: Select an existing entry and modify the values.\n" +
                        "- Deadline:   Data must be submitted by the 5th business day of the following month.\n" +
                        "\n" +
                        "STATISTICS & REPORTS\n" +
                        "- Filter by hotel, category, year and/or month.\n" +
                        "- Export reports as PDF via the 'Export' button.\n" +
                        "- View occupancy histograms by selecting a FROM/TO month and year.\n" +
                        "\n" +
                        "USER MANAGEMENT\n" +
                        "- Senior Users:  Full access to master and transactional data.\n" +
                        "- Delete Rights: Only specially authorized users may delete master data.\n" +
                        "\n" +
                        "--- FOR HOTEL REPRESENTATIVES ---\n" +
                        "\n" +
                        "HOTEL MASTER DATA\n" +
                        "- View your hotel(s) in the list.\n" +
                        "- Edit your hotel's master data (name, address, rooms, beds).\n" +
                        "\n" +
                        "TRANSACTIONAL DATA\n" +
                        "- Add monthly occupancy data for your hotel(s).\n" +
                        "- View and check all previously entered transactional data.\n"
        );

        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textArea.setCaretPosition(textArea.getDocument().getLength());

        add(new JScrollPane(textArea), BorderLayout.CENTER);
    }
}
