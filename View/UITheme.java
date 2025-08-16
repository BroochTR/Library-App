package com.library.view;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Hằng số UI Theme cho giao diện hiện đại và thân thiện với người dùng
 */
public class UITheme {
    
    // Bảng màu - Theme xanh hiện đại
    public static final Color PRIMARY_COLOR = new Color(59, 130, 246);        
    public static final Color PRIMARY_DARK = new Color(37, 99, 235);          
    public static final Color PRIMARY_LIGHT = new Color(147, 197, 253);       
    
    public static final Color ACCENT_COLOR = new Color(16, 185, 129);         
    public static final Color WARNING_COLOR = new Color(245, 158, 11);        
    public static final Color DANGER_COLOR = new Color(239, 68, 68);          
    
    // Màu nền
    public static final Color BACKGROUND_PRIMARY = new Color(249, 250, 251);  
    public static final Color BACKGROUND_SECONDARY = new Color(243, 244, 246); 
    public static final Color BACKGROUND_CARD = Color.WHITE;
    
    // Màu chữ
    public static final Color TEXT_PRIMARY = new Color(17, 24, 39);           
    public static final Color TEXT_SECONDARY = new Color(75, 85, 99);         
    public static final Color TEXT_MUTED = new Color(156, 163, 175);          
    
    // Màu viền
    public static final Color BORDER_COLOR = new Color(229, 231, 235);        
    public static final Color BORDER_FOCUS = PRIMARY_COLOR;
    
    // Font chữ
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    
    // Kích thước
    public static final int BORDER_RADIUS = 8;
    public static final int PADDING_SMALL = 8;
    public static final int PADDING_MEDIUM = 16;
    public static final int PADDING_LARGE = 24;
    public static final int BUTTON_HEIGHT = 40;
    public static final int INPUT_HEIGHT = 36;
    
    /**
     * Tạo button hiện đại với style chính
     */
    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, PRIMARY_COLOR, Color.WHITE, PRIMARY_DARK);
        return button;
    }
    
    /**
     * Tạo button hiện đại với style phụ
     */
    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, BACKGROUND_CARD, TEXT_PRIMARY, BACKGROUND_SECONDARY);
        button.setBorder(createBorder(BORDER_COLOR));
        return button;
    }
    
    /**
     * Tạo button thành công
     */
    public static JButton createSuccessButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, ACCENT_COLOR, Color.WHITE, ACCENT_COLOR.darker());
        return button;
    }
    
    /**
     * Tạo button cảnh báo
     */
    public static JButton createWarningButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, WARNING_COLOR, Color.WHITE, WARNING_COLOR.darker());
        return button;
    }
    
    /**
     * Tạo button nguy hiểm
     */
    public static JButton createDangerButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, DANGER_COLOR, Color.WHITE, DANGER_COLOR.darker());
        return button;
    }
    
    /**
     * Tạo style cho button với giao diện hiện đại
     */
    private static void styleButton(JButton button, Color bgColor, Color textColor, Color hoverColor) {
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFont(FONT_BUTTON);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(120, BUTTON_HEIGHT));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Thêm hiệu ứng hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(hoverColor);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }
    
    /**
     * Tạo text field hiện đại
     */
    public static JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(FONT_BODY);
        field.setBorder(createInputBorder());
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, INPUT_HEIGHT));
        
        // Thêm hiệu ứng placeholder
        if (placeholder != null && !placeholder.isEmpty()) {
            field.setText(placeholder);
            field.setForeground(TEXT_MUTED);
            
            field.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent e) {
                    if (field.getText().equals(placeholder)) {
                        field.setText("");
                        field.setForeground(TEXT_PRIMARY);
                    }
                    field.setBorder(createInputBorder(BORDER_FOCUS));
                }
                
                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    if (field.getText().isEmpty()) {
                        field.setText(placeholder);
                        field.setForeground(TEXT_MUTED);
                    }
                    field.setBorder(createInputBorder());
                }
            });
        }
        
        return field;
    }
    
    /**
     * Tạo panel hiện đại với giao diện card
     */
    public static JPanel createCard() {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND_CARD);
        panel.setBorder(createCardBorder());
        return panel;
    }
    
    /**
     * Tạo panel hiện đại với padding
     */
    public static JPanel createPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(BACKGROUND_PRIMARY);
        return panel;
    }
    
    /**
     * Tạo viền card với hiệu ứng đổ bóng
     */
    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(PADDING_MEDIUM, PADDING_MEDIUM, PADDING_MEDIUM, PADDING_MEDIUM)
        );
    }
    
    /**
     * Tạo viền đơn giản
     */
    public static Border createBorder(Color color) {
        return BorderFactory.createLineBorder(color, 1);
    }
    
    /**
     * Tạo viền input
     */
    public static Border createInputBorder() {
        return createInputBorder(BORDER_COLOR);
    }
    
    public static Border createInputBorder(Color color) {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 1),
            BorderFactory.createEmptyBorder(PADDING_SMALL, PADDING_SMALL, PADDING_SMALL, PADDING_SMALL)
        );
    }
    
    /**
     * Tạo viền có tiêu đề với style hiện đại
     */
    public static Border createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
            createBorder(BORDER_COLOR),
            title,
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            FONT_TITLE,
            TEXT_PRIMARY
        );
    }
} 