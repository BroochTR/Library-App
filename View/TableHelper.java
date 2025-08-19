package com.library.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

/**
 * Lớp cho các thao tác table phổ biến
 */
public class TableHelper {
    
    /**
     * Tạo table chỉ đọc với style chuẩn
     */
    public static JTable createReadOnlyTable(String[] columnNames) {
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);
        
        return table;
    }
    
    /**
     * Thiết lập độ rộng cột chuẩn 
     */
    public static void setStandardColumnWidths(JTable table, int[] widths) {
        for (int i = 0; i < Math.min(widths.length, table.getColumnCount()); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }
    
    /**
     * Xóa và điền lại table với dữ liệu mới
     */
    public static void updateTableData(DefaultTableModel model, Object[][] data) {
        model.setRowCount(0); 
        for (Object[] row : data) {
            model.addRow(row);
        }
    }
    
    /**
     * Lấy dữ liệu hàng được chọn từ table
     */
    public static Object[] getSelectedRowData(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            Object[] rowData = new Object[model.getColumnCount()];
            for (int i = 0; i < model.getColumnCount(); i++) {
                rowData[i] = model.getValueAt(selectedRow, i);
            }
            return rowData;
        }
        return null;
    }
    
    /**
     * Lấy ID được chọn từ table
     */
    public static String getSelectedId(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            return table.getValueAt(selectedRow, 0).toString();
        }
        return null;
    }
    
    /**
     * Tạo panel table cuộn
     */
    public static JPanel createTablePanel(JTable table, Dimension preferredSize) {
        JPanel panel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(preferredSize);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Lọc các hàng table dựa trên text 
     */
    public static void filterTable(JTable table, String searchText, int... searchColumns) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        RowFilter<Object, Object> filter = null;
        
        if (searchText != null && !searchText.trim().isEmpty()) {
            try {
                StringBuilder regex = new StringBuilder();
                regex.append("(?i)"); 
                regex.append(".*").append(searchText).append(".*");
                
                if (searchColumns.length > 0) {
                    filter = RowFilter.regexFilter(regex.toString(), searchColumns);
                } else {
                    filter = RowFilter.regexFilter(regex.toString());
                }
            } catch (java.util.regex.PatternSyntaxException e) {
                filter = null;
            }
        }
        
        if (table.getRowSorter() instanceof TableRowSorter) {
            @SuppressWarnings("unchecked")
            TableRowSorter<DefaultTableModel> sorter = 
                (TableRowSorter<DefaultTableModel>) table.getRowSorter();
            sorter.setRowFilter(filter);
        } else {
            // Tạo sorter mới nếu chưa có
            TableRowSorter<DefaultTableModel> sorter = 
                new TableRowSorter<>(model);
            sorter.setRowFilter(filter);
            table.setRowSorter(sorter);
        }
    }

}
