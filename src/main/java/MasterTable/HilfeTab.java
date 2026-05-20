package MasterTable;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HilfeTab extends JPanel {

    JList<String>  topicList;
    JEditorPane    contentPane;

    HilfeTab() {
        definePanel();
        initComponents();
        addComponents();
    }

    private void definePanel() {
        setLayout(new BorderLayout());
    }

    private void initComponents() {
        topicList   = new JList<String>(TOPICS.keySet().toArray(new String[0]));
        contentPane = new JEditorPane("text/html", "");
        contentPane.setEditable(false);
        contentPane.setBorder(new EmptyBorder(12, 16, 12, 16));
    }

    private void addComponents() {
        topicList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        topicList.setFixedCellHeight(28);
        topicList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showTopic(topicList.getSelectedValue());
        });

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(topicList), new JScrollPane(contentPane));
        split.setDividerLocation(180);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);

        topicList.setSelectedIndex(0);
    }

    private void showTopic(String topic) {
        String body = TOPICS.getOrDefault(topic,
                "<h2>" + topic + "</h2><p><i>Content will be added after implementation.</i></p>");
        contentPane.setText("<html><body style='font-family:Arial;font-size:13px'>" + body + "</body></html>");
        contentPane.setCaretPosition(0);
    }
}
