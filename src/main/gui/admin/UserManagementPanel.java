package main.gui.admin;

import main.gui.CustomHeaderRenderer;
import main.model.User;
import main.service.DataService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private DataService dataService;

    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilterCombo;
    private List<User> allUsers;

    public UserManagementPanel() {
        this.dataService = DataService.getInstance();

        initComponents();
        refreshData();
        setupListeners();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 标题
        JLabel titleLabel = new JLabel("用户管理", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // 工具栏
        JPanel toolbarPanel = createToolbar();
        add(toolbarPanel, BorderLayout.NORTH);

        // 表格
        String[] columns = {"用户ID", "用户名", "手机号", "余额(¥)", "角色", "状态", "注册时间"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(tableModel);
        userTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        userTable.setRowHeight(35);

        // 使用自定义表头渲染器
        userTable.getTableHeader().setDefaultRenderer(
                new CustomHeaderRenderer(new Color(41, 128, 185), Color.WHITE));

        userTable.setSelectionBackground(new Color(230, 247, 255));

        // 设置列宽
        userTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        userTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        userTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        userTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        userTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        userTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        userTable.getColumnModel().getColumn(6).setPreferredWidth(150);

        // 设置单元格渲染器
        userTable.setDefaultRenderer(Object.class, new UserTableCellRenderer());

        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);

        // 底部操作面板
        JPanel actionPanel = createActionPanel();
        add(actionPanel, BorderLayout.SOUTH);
    }

    private JPanel createToolbar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));

        panel.add(new JLabel("搜索:"));

        searchField = new JTextField(15);
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(200, 30));
        panel.add(searchField);

        JButton searchBtn = new JButton("搜索");
        searchBtn.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        searchBtn.setBackground(new Color(52, 152, 219));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setBorderPainted(false);
        searchBtn.setFocusPainted(false);
        searchBtn.setPreferredSize(new Dimension(80, 30));
        searchBtn.addActionListener(e -> searchUsers());
        panel.add(searchBtn);

        panel.add(new JLabel("状态过滤:"));

        String[] statuses = {"全部", "正常", "已禁用"};
        statusFilterCombo = new JComboBox<>(statuses);
        statusFilterCombo.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        statusFilterCombo.setPreferredSize(new Dimension(100, 30));
        statusFilterCombo.addActionListener(e -> filterUsers());
        panel.add(statusFilterCombo);

        JButton refreshBtn = new JButton("刷新");
        refreshBtn.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        refreshBtn.setBackground(new Color(46, 204, 113));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setPreferredSize(new Dimension(80, 30));
        refreshBtn.addActionListener(e -> refreshData());
        panel.add(refreshBtn);

        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton enableBtn = new JButton("启用用户");
        enableBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        enableBtn.setBackground(new Color(46, 204, 113));
        enableBtn.setForeground(Color.WHITE);
        enableBtn.setBorderPainted(false);
        enableBtn.setFocusPainted(false);
        enableBtn.setPreferredSize(new Dimension(120, 40));
        enableBtn.addActionListener(e -> enableUser());

        JButton disableBtn = new JButton("禁用用户");
        disableBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        disableBtn.setBackground(new Color(231, 76, 60));
        disableBtn.setForeground(Color.WHITE);
        disableBtn.setBorderPainted(false);
        disableBtn.setFocusPainted(false);
        disableBtn.setPreferredSize(new Dimension(120, 40));
        disableBtn.addActionListener(e -> disableUser());

        JButton viewDetailBtn = new JButton("查看详情");
        viewDetailBtn.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        viewDetailBtn.setBackground(new Color(52, 152, 219));
        viewDetailBtn.setForeground(Color.WHITE);
        viewDetailBtn.setBorderPainted(false);
        viewDetailBtn.setFocusPainted(false);
        viewDetailBtn.setPreferredSize(new Dimension(120, 40));
        viewDetailBtn.addActionListener(e -> showUserDetail());

        panel.add(enableBtn);
        panel.add(disableBtn);
        panel.add(viewDetailBtn);

        return panel;
    }

    private void setupListeners() {
        // 双击查看详情
        userTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showUserDetail();
                }
            }
        });
    }

    private void searchUsers() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            filterUsers();
            return;
        }

        java.util.List<User> filtered = allUsers.stream()
                .filter(u -> u.getUsername().toLowerCase().contains(keyword) ||
                        u.getPhone().contains(keyword))
                .collect(java.util.stream.Collectors.toList());

        updateTableData(filtered);
    }

    private void filterUsers() {
        String statusFilter = (String) statusFilterCombo.getSelectedItem();

        java.util.List<User> filtered = new java.util.ArrayList<>(allUsers);

        if (!"全部".equals(statusFilter)) {
            String targetStatus = "正常".equals(statusFilter) ? "active" : "disabled";
            filtered.removeIf(u -> !targetStatus.equals(u.getStatus()));
        }

        updateTableData(filtered);
    }

    private void updateTableData(java.util.List<User> users) {
        tableModel.setRowCount(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (User user : users) {
            String roleStr = user.getRole() == 0 ? "管理员" : "普通用户";
            String statusStr = "active".equals(user.getStatus()) ? "正常" : "已禁用";

            Object[] row = {
                    user.getUserId(),
                    user.getUsername(),
                    user.getPhone(),
                    String.format("%.2f", user.getBalance()),
                    roleStr,
                    statusStr,
                    user.getCreateTime().format(formatter)
            };
            tableModel.addRow(row);
        }
    }

    private void enableUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要启用的用户！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 5);

        if ("正常".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, "该用户已是正常状态！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要启用用户 [" + username + "] 吗？",
                "确认启用", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = dataService.updateUserStatus(userId, "active");
            if (success) {
                JOptionPane.showMessageDialog(this, "用户已启用！", "成功", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "操作失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void disableUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要禁用的用户！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 5);

        if ("已禁用".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, "该用户已是禁用状态！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 检查是否是管理员
        String role = (String) tableModel.getValueAt(selectedRow, 4);
        if ("管理员".equals(role)) {
            JOptionPane.showMessageDialog(this, "不能禁用管理员账户！", "警告", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要禁用用户 [" + username + "] 吗？\n禁用后该用户将无法登录系统。",
                "确认禁用", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = dataService.updateUserStatus(userId, "disabled");
            if (success) {
                JOptionPane.showMessageDialog(this, "用户已禁用！", "成功", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "操作失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showUserDetail() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要查看的用户！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        User user = dataService.getUserById(userId);

        if (user != null) {
            StringBuilder detail = new StringBuilder();
            detail.append("用户详情\n");
            detail.append("═══════════════════\n");
            detail.append("用户ID: ").append(user.getUserId()).append("\n");
            detail.append("用户名: ").append(user.getUsername()).append("\n");
            detail.append("手机号: ").append(user.getPhone()).append("\n");
            detail.append("账户余额: ¥").append(String.format("%.2f", user.getBalance())).append("\n");
            detail.append("用户角色: ").append(user.getRole() == 0 ? "管理员" : "普通用户").append("\n");
            detail.append("账户状态: ").append("active".equals(user.getStatus()) ? "正常" : "已禁用").append("\n");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            detail.append("注册时间: ").append(user.getCreateTime().format(formatter)).append("\n");
            detail.append("═══════════════════");

            JTextArea textArea = new JTextArea(detail.toString());
            textArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            textArea.setEditable(false);
            textArea.setBackground(new Color(245, 245, 245));
            textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JOptionPane.showMessageDialog(this, new JScrollPane(textArea),
                    "用户详情", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void refreshData() {
        allUsers = dataService.getAllUsers();
        searchField.setText("");
        statusFilterCombo.setSelectedIndex(0);
        updateTableData(allUsers);
    }

    // 自定义单元格渲染器
    private class UserTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);

            if (!isSelected) {
                String status = (String) table.getValueAt(row, 5);
                if ("正常".equals(status)) {
                    setBackground(new Color(235, 255, 235));
                } else if ("已禁用".equals(status)) {
                    setBackground(new Color(255, 235, 235));
                } else {
                    setBackground(Color.WHITE);
                }
            }

            return c;
        }
    }
}