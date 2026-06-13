package MasterTable.util;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.SortOrder;

public class TableUtils {


    // Enables sorting by clicking on the column headers
    public static void enableSorting(JTable table) {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
        // After 3 Clicks (ASC → DESC-> normal) will be normal again
        table.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                java.util.List<? extends RowSorter.SortKey> keys = sorter.getSortKeys();
                if (!keys.isEmpty() && keys.get(0).getColumn() == col
                        && keys.get(0).getSortOrder() == SortOrder.DESCENDING) {
                    sorter.setSortKeys(null); // automatisch zurücksetzen
                }
            }
        });

    }

    /**
     * filters the table based on a search term in a specific column
     * @param table The JTable
     * @param searchText the Text from the Searchfeld
     * @param columnIndex the Index of the column (f.E. 1 for Hotelname)
     */

    public static void filterTable(JTable table, String searchText, int columnIndex) {
        TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) table.getRowSorter();
        if (sorter == null) {
            sorter = new TableRowSorter<>(table.getModel());
            table.setRowSorter(sorter);
        }
        if (searchText.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, columnIndex));
        }
    }

    public static Object getSelectedId(JTable table, int iDcolumnIndex) {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            return null;
        }
        //Converts the visual index (View) to the actual data index (Model)
        int modelRow = table.convertRowIndexToModel(viewRow);
        return table.getModel().getValueAt(modelRow, iDcolumnIndex);


    }

    public static void resetSort(JTable table) {
        TableRowSorter<?> sorter = (TableRowSorter<?>) table.getRowSorter();
        if (sorter != null) {
            sorter.setSortKeys(null);
        }
    }



}
