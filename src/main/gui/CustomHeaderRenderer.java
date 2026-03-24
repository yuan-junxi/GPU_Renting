package main.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * 自定义表头渲染器，强制设置表头样式
 */
public class CustomHeaderRenderer extends DefaultTableCellRenderer {
    private Color backgroundColor;
    private Color foregroundColor;

    public CustomHeaderRenderer(Color backgroundColor, Color foregroundColor) {
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
        setHorizontalAlignment(CENTER);
        setFont(new Font("微软雅黑", Font.BOLD, 13));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);

        c.setBackground(backgroundColor);
        c.setForeground(foregroundColor);
        setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        return c;
    }
}