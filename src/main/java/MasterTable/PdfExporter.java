package MasterTable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// US-07: Exports the currently filtered occupancy table as an A4 portrait PDF.
// Called from OccupancyPanel via the EXPORT PDF button.
public class PdfExporter {

    // A4 dimensions and margins (in PDF points)
    private static final float W         = PDRectangle.A4.getWidth();   // 595
    private static final float H         = PDRectangle.A4.getHeight();  // 842
    private static final float ML        = 40f;  // margin left
    private static final float MR        = 40f;  // margin right
    private static final float MT        = 40f;  // margin top
    private static final float MB        = 40f;  // margin bottom
    private static final float CONTENT_W = W - ML - MR;

    // NOE-TO brand colours
    private static final Color NOE_BLUE  = new Color(31,  78,  150);
    private static final Color NOE_LIGHT = new Color(235, 243, 250);
    private static final Color GREY      = new Color(120, 120, 120);

    // Row heights
    private static final float ROW_H        = 18f;
    private static final float TABLE_HDR_H  = 22f;
    private static final float HEADER_BLOCK = 115f; // space used by NOE-TO header + filter box

    // US-07: Main entry point.
    public static void export(OccupancyPanel panel) {

        // ── 1. Read active filter values ─────────────────────────────────────
        String fromM = (String) panel.fromMonth.getSelectedItem();
        String fromY = (String) panel.fromYear.getSelectedItem();
        String toM   = (String) panel.toMonth.getSelectedItem();
        String toY   = (String) panel.toYear.getSelectedItem();
        String hotel = (String) panel.hotelFilter.getSelectedItem();
        String cat   = panel.categoryFilter.getSelectedItem().toString();

        // US-07: Filename includes the selected period
        String suggestedName = String.format(
                "NOE-TO_Statistics_%s-%s_to_%s-%s.pdf", fromY, fromM, toY, toM);

        // ── 2. File chooser ───────────────────────────────────────────────────
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save PDF");
        fc.setSelectedFile(new File(suggestedName));
        if (fc.showSaveDialog(panel) != JFileChooser.APPROVE_OPTION) return;

        File out = fc.getSelectedFile();
        if (!out.getName().toLowerCase().endsWith(".pdf"))
            out = new File(out.getAbsolutePath() + ".pdf");

        // ── 3. Build PDF ──────────────────────────────────────────────────────
        TableModel data     = panel.table.getModel();
        int        rowCount = data.getRowCount();
        int        colCount = data.getColumnCount();
        float[]    colW     = columnWidths(colCount);

        // Calculate how many data rows fit per page
        float usableFirst = H - MT - MB - HEADER_BLOCK - TABLE_HDR_H;
        float usableRest  = H - MT - MB - TABLE_HDR_H;
        int   rowsPage1   = Math.max(1, (int) (usableFirst / ROW_H));
        int   rowsPageN   = Math.max(1, (int) (usableRest  / ROW_H));

        // Build list of start-row per page
        java.util.List<Integer> pageBreaks = new java.util.ArrayList<>();
        pageBreaks.add(0);
        int cur = rowsPage1;
        while (cur < rowCount) { pageBreaks.add(cur); cur += rowsPageN; }
        int totalPages = Math.max(1, pageBreaks.size());

        try (PDDocument doc = new PDDocument()) {

            PDType1Font regular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDType1Font bold    = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

            for (int p = 0; p < totalPages; p++) {

                PDPage page = new PDPage(PDRectangle.A4);
                doc.addPage(page);

                try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {

                    float y = H - MT;

                    // US-07: Draw branding header only on page 1
                    if (p == 0) {
                        y = drawHeader(cs, regular, bold, y,
                                hotel, fromM, fromY, toM, toY, cat, rowCount);
                    }

                    // US-07: Table column headers repeated on every page
                    y = drawTableHeader(cs, bold, y, colW, data);

                    // US-07: Data rows for this page
                    int start = pageBreaks.get(p);
                    int end   = (p + 1 < pageBreaks.size()) ? pageBreaks.get(p + 1) : rowCount;

                    for (int r = start; r < end; r++) {
                        y = drawRow(cs, regular, y, colW, data, r);
                    }

                    // US-07: Page number footer on every page
                    drawFooter(cs, regular, p + 1, totalPages);
                }
            }

            doc.save(out);
            JOptionPane.showMessageDialog(panel,
                    "PDF saved:\n" + out.getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel,
                    "PDF export failed:\n" + ex.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Drawing helpers ───────────────────────────────────────────────────────

    // US-07: Page 1 header — NOE-TO branding, timestamp, report title, filter context
    private static float drawHeader(PDPageContentStream cs,
                                    PDType1Font regular, PDType1Font bold, float y,
                                    String hotel, String fromM, String fromY,
                                    String toM, String toY, String cat, int records) throws IOException {

        // NOE-TO title
        setColor(cs, NOE_BLUE, false);
        text(cs, bold, 20, ML, y - 26, "NOE-TO");
        text(cs, regular, 10, ML, y - 40, "Lower Austria Tourist Portal");

        // Timestamp (right-aligned)
        String ts = "Generated: " + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        float tsW = textWidth(regular, 8, ts);
        setColor(cs, GREY, false);
        text(cs, regular, 8, W - MR - tsW, y - 26, ts);

        // Divider line
        y -= 50;
        line(cs, NOE_BLUE, ML, y, W - MR, y, 1f);
        y -= 6;

        // Report title (centred)
        String title = "Monthly Occupancy Statistics";
        float titleW = textWidth(bold, 14, title);
        setColor(cs, NOE_BLUE, false);
        text(cs, bold, 14, (W - titleW) / 2f, y - 16, title);
        y -= 26;

        // Filter context box
        String filterLine = String.format(
                "Hotel: %s   |   Period: %s/%s – %s/%s   |   Category: %s   |   Records: %d",
                hotel, fromM, fromY, toM, toY, cat, records);

        float boxH = 20f;
        fillRect(cs, NOE_LIGHT, ML, y - boxH, CONTENT_W, boxH);
        setColor(cs, GREY, false);
        text(cs, regular, 8, ML + 5, y - 13, filterLine);
        y -= boxH + 8;

        return y;
    }

    // US-07: Blue column-header row, repeated on each page
    private static float drawTableHeader(PDPageContentStream cs,
                                         PDType1Font bold, float y, float[] colW, TableModel data) throws IOException {

        fillRect(cs, NOE_BLUE, ML, y - TABLE_HDR_H, CONTENT_W, TABLE_HDR_H);

        float x = ML;
        for (int c = 0; c < data.getColumnCount(); c++) {
            setColor(cs, Color.WHITE, false);
            text(cs, bold, 8, x + 3, y - 14, truncate(data.getColumnName(c), bold, 8, colW[c] - 6));
            x += colW[c];
        }
        return y - TABLE_HDR_H;
    }

    // US-07: One data row with alternating background
    private static float drawRow(PDPageContentStream cs,
                                 PDType1Font regular, float y, float[] colW, TableModel data, int row) throws IOException {

        // Alternating row colour
        if (row % 2 != 0) fillRect(cs, NOE_LIGHT, ML, y - ROW_H, CONTENT_W, ROW_H);

        // Light separator line
        line(cs, new Color(210, 210, 210), ML, y - ROW_H, W - MR, y - ROW_H, 0.3f);

        float x = ML;
        setColor(cs, Color.BLACK, false);
        for (int c = 0; c < data.getColumnCount(); c++) {
            Object val = data.getValueAt(row, c);
            String txt = val != null ? val.toString() : "";
            text(cs, regular, 8, x + 3, y - 12, truncate(txt, regular, 8, colW[c] - 6));
            x += colW[c];
        }
        return y - ROW_H;
    }

    // US-07: "Page X of Y" centred at the bottom
    private static void drawFooter(PDPageContentStream cs,
                                   PDType1Font regular, int page, int total) throws IOException {
        String txt = "Page " + page + " of " + total;
        float  tw  = textWidth(regular, 8, txt);
        line(cs, new Color(200, 200, 200), ML, MB, W - MR, MB, 0.5f);
        setColor(cs, GREY, false);
        text(cs, regular, 8, (W - tw) / 2f, MB - 14, txt);
    }

    // ── Low-level helpers ─────────────────────────────────────────────────────

    private static void text(PDPageContentStream cs, PDType1Font font,
                             float size, float x, float y, String txt) throws IOException {
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(txt);
        cs.endText();
    }

    private static void fillRect(PDPageContentStream cs, Color color,
                                 float x, float y, float w, float h) throws IOException {
        setColor(cs, color, false);
        cs.addRect(x, y, w, h);
        cs.fill();
    }

    private static void line(PDPageContentStream cs, Color color,
                             float x1, float y1, float x2, float y2, float width) throws IOException {
        setColor(cs, color, true);
        cs.setLineWidth(width);
        cs.moveTo(x1, y1);
        cs.lineTo(x2, y2);
        cs.stroke();
    }

    private static void setColor(PDPageContentStream cs, Color c, boolean stroke) throws IOException {
        float r = c.getRed() / 255f, g = c.getGreen() / 255f, b = c.getBlue() / 255f;
        if (stroke) cs.setStrokingColor(r, g, b);
        else        cs.setNonStrokingColor(r, g, b);
    }

    private static float textWidth(PDType1Font font, float size, String txt) throws IOException {
        return font.getStringWidth(txt) / 1000f * size;
    }

    private static String truncate(String txt, PDType1Font font, float size, float maxW) throws IOException {
        if (textWidth(font, size, txt) <= maxW) return txt;
        while (!txt.isEmpty()) {
            txt = txt.substring(0, txt.length() - 1);
            if (textWidth(font, size, txt + "…") <= maxW) return txt + "…";
        }
        return "";
    }

    // US-07: Column widths proportional to CONTENT_W for the 6 occupancy columns
    private static float[] columnWidths(int colCount) {
        if (colCount == 6) return new float[]{
                CONTENT_W * 0.08f,  // HOTEL ID
                CONTENT_W * 0.32f,  // HOTEL NAME
                CONTENT_W * 0.12f,  // YEAR
                CONTENT_W * 0.12f,  // MONTH
                CONTENT_W * 0.18f,  // ROOM OCCUPANCY
                CONTENT_W * 0.18f   // BED OCCUPANCY
        };
        float w = CONTENT_W / colCount;
        float[] eq = new float[colCount]; java.util.Arrays.fill(eq, w); return eq;
    }
}