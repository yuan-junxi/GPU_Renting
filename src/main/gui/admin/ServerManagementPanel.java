package main.gui.admin;

import main.gui.CustomHeaderRenderer;
import main.model.GpuCard;
import main.model.GpuServer;
import main.service.DataService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ServerManagementPanel extends JPanel {
    private DataService dataService;

    private JTable serverTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilterCombo;
    private List<GpuServer> allServers;

    public ServerManagementPanel() {
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
        JLabel titleLabel = new JLabel("服务器管理", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // 工具栏
        JPanel toolbarPanel = createToolbar();
        add(toolbarPanel, BorderLayout.NORTH);

        // 表格
        String[] columns = {"服务器ID", "编号", "名称", "位置", "IP地址", "状态", "添加时间"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        serverTable = new JTable(tableModel);
        serverTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        serverTable.setRowHeight(35);

        // 使用自定义表头渲染器
        serverTable.getTableHeader().setDefaultRenderer(
                new CustomHeaderRenderer(new Color(52, 73, 94), Color.WHITE));

        serverTable.setSelectionBackground(new Color(220, 230, 240));

        // 设置列宽
        serverTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        serverTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        serverTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        serverTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        serverTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        serverTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        serverTable.getColumnModel().getColumn(6).setPreferredWidth(150);

        // 设置单元格渲染器
        serverTable.setDefaultRenderer(Object.class, new ServerTableCellRenderer());

        JScrollPane scrollPane = new JScrollPane(serverTable);
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
        searchBtn.setBackground(new Color(52, 73, 94));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setBorderPainted(false);
        searchBtn.setFocusPainted(false);
        searchBtn.setPreferredSize(new Dimension(80, 30));
        searchBtn.addActionListener(e -> searchServers());
        panel.add(searchBtn);

        panel.add(new JLabel("状态过滤:"));

        String[] statuses = {"全部", "在线", "维护"};
        statusFilterCombo = new JComboBox<>(statuses);
        statusFilterCombo.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        statusFilterCombo.setPreferredSize(new Dimension(100, 30));
        statusFilterCombo.addActionListener(e -> filterServers());
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
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton addBtn = new JButton("添加服务器");
        addBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        addBtn.setBackground(new Color(46, 204, 113));
        addBtn.setForeground(Color.WHITE);
        addBtn.setBorderPainted(false);
        addBtn.setFocusPainted(false);
        addBtn.setPreferredSize(new Dimension(130, 40));
        addBtn.addActionListener(e -> addServer());

        JButton editBtn = new JButton("修改信息");
        editBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        editBtn.setBackground(new Color(52, 152, 219));
        editBtn.setForeground(Color.WHITE);
        editBtn.setBorderPainted(false);
        editBtn.setFocusPainted(false);
        editBtn.setPreferredSize(new Dimension(130, 40));
        editBtn.addActionListener(e -> editServer());

        JButton deleteBtn = new JButton("删除服务器");
        deleteBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setPreferredSize(new Dimension(130, 40));
        deleteBtn.addActionListener(e -> deleteServer());

        JButton statusBtn = new JButton("修改状态");
        statusBtn.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        statusBtn.setBackground(new Color(241, 196, 15));
        statusBtn.setForeground(Color.WHITE);
        statusBtn.setBorderPainted(false);
        statusBtn.setFocusPainted(false);
        statusBtn.setPreferredSize(new Dimension(130, 40));
        statusBtn.addActionListener(e -> changeStatus());

        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);
        panel.add(statusBtn);

        return panel;
    }

    private void setupListeners() {
        serverTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showServerDetail();
                }
            }
        });
    }

    private void searchServers() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            filterServers();
            return;
        }

        List<GpuServer> filtered = allServers.stream()
                .filter(s -> s.getServerName().toLowerCase().contains(keyword) ||
                        s.getServerNo().toLowerCase().contains(keyword) ||
                        s.getLocation().toLowerCase().contains(keyword) ||
                        s.getIpAddress().contains(keyword))
                .collect(java.util.stream.Collectors.toList());

        updateTableData(filtered);
    }

    private void filterServers() {
        String statusFilter = (String) statusFilterCombo.getSelectedItem();

        List<GpuServer> filtered = new java.util.ArrayList<>(allServers);

        if (!"全部".equals(statusFilter)) {
            filtered.removeIf(s -> !statusFilter.equals(s.getStatus()));
        }

        updateTableData(filtered);
    }

    private void updateTableData(List<GpuServer> servers) {
        tableModel.setRowCount(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (GpuServer server : servers) {
            Object[] row = {
                    server.getServerId(),
                    server.getServerNo(),
                    server.getServerName(),
                    server.getLocation(),
                    server.getIpAddress(),
                    server.getStatus(),
                    server.getCreateTime().format(formatter)
            };
            tableModel.addRow(row);
        }
    }

    private void addServer() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "添加服务器", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 服务器编号
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("服务器编号:"), gbc);

        gbc.gridx = 1;
        JTextField noField = new JTextField(15);
        formPanel.add(noField, gbc);

        // 服务器名称
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("服务器名称:"), gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(15);
        formPanel.add(nameField, gbc);

        // 位置
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("位置:"), gbc);

        gbc.gridx = 1;
        JTextField locationField = new JTextField(15);
        formPanel.add(locationField, gbc);

        // IP地址
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("IP地址:"), gbc);

        gbc.gridx = 1;
        JTextField ipField = new JTextField(15);
        formPanel.add(ipField, gbc);

        // 状态 - 只有"在线"和"维护"两种
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("状态:"), gbc);

        gbc.gridx = 1;
        String[] statuses = {"在线", "维护"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        formPanel.add(statusCombo, gbc);

        // 按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveBtn = new JButton("保存");
        saveBtn.setBackground(new Color(46, 204, 113));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> {
            String serverNo = noField.getText().trim();
            String serverName = nameField.getText().trim();
            String location = locationField.getText().trim();
            String ipAddress = ipField.getText().trim();
            String status = (String) statusCombo.getSelectedItem();

            if (serverNo.isEmpty() || serverName.isEmpty() || location.isEmpty() || ipAddress.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "请填写所有字段！");
                return;
            }

            GpuServer newServer = new GpuServer();
            newServer.setServerNo(serverNo);
            newServer.setServerName(serverName);
            newServer.setLocation(location);
            newServer.setIpAddress(ipAddress);
            newServer.setStatus(status);

            boolean success = dataService.addServer(newServer);
            if (success) {
                JOptionPane.showMessageDialog(dialog, "添加成功！");
                dialog.dispose();
                refreshData();
            } else {
                JOptionPane.showMessageDialog(dialog, "添加失败！");
            }
        });

        JButton cancelBtn = new JButton("取消");
        cancelBtn.setBackground(new Color(149, 165, 166));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void editServer() {
        int selectedRow = serverTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要修改的服务器！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int serverId = (int) tableModel.getValueAt(selectedRow, 0);
        GpuServer server = allServers.stream()
                .filter(s -> s.getServerId().equals(serverId))
                .findFirst()
                .orElse(null);

        if (server == null) return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "修改服务器信息", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 服务器编号
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("服务器编号:"), gbc);

        gbc.gridx = 1;
        JTextField noField = new JTextField(server.getServerNo(), 15);
        formPanel.add(noField, gbc);

        // 服务器名称
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("服务器名称:"), gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(server.getServerName(), 15);
        formPanel.add(nameField, gbc);

        // 位置
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("位置:"), gbc);

        gbc.gridx = 1;
        JTextField locationField = new JTextField(server.getLocation(), 15);
        formPanel.add(locationField, gbc);

        // IP地址
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("IP地址:"), gbc);

        gbc.gridx = 1;
        JTextField ipField = new JTextField(server.getIpAddress(), 15);
        formPanel.add(ipField, gbc);

        // 按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveBtn = new JButton("保存");
        saveBtn.setBackground(new Color(52, 152, 219));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> {
            String serverNo = noField.getText().trim();
            String serverName = nameField.getText().trim();
            String location = locationField.getText().trim();
            String ipAddress = ipField.getText().trim();

            if (serverNo.isEmpty() || serverName.isEmpty() || location.isEmpty() || ipAddress.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "请填写所有字段！");
                return;
            }

            server.setServerNo(serverNo);
            server.setServerName(serverName);
            server.setLocation(location);
            server.setIpAddress(ipAddress);

            boolean success = dataService.updateServer(server);
            if (success) {
                JOptionPane.showMessageDialog(dialog, "修改成功！");
                dialog.dispose();
                refreshData();
            } else {
                JOptionPane.showMessageDialog(dialog, "修改失败！");
            }
        });

        JButton cancelBtn = new JButton("取消");
        cancelBtn.setBackground(new Color(149, 165, 166));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteServer() {
        int selectedRow = serverTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的服务器！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int serverId = (int) tableModel.getValueAt(selectedRow, 0);
        String serverName = (String) tableModel.getValueAt(selectedRow, 2);

        // 检查服务器下是否有显卡
        List<GpuCard> cards = dataService.getAllGpuCards();
        boolean hasCards = cards.stream().anyMatch(c -> c.getServerId().equals(serverId));

        if (hasCards) {
            JOptionPane.showMessageDialog(this,
                    "该服务器下还有显卡，无法删除！\n请先删除或转移服务器下的显卡。",
                    "删除失败", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除服务器 [" + serverName + "] 吗？\n此操作不可恢复！",
                "确认删除", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = dataService.deleteServer(serverId);
            if (success) {
                JOptionPane.showMessageDialog(this, "删除成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void changeStatus() {
        int selectedRow = serverTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要修改状态的服务器！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int serverId = (int) tableModel.getValueAt(selectedRow, 0);
        String serverName = (String) tableModel.getValueAt(selectedRow, 2);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 5);

        String[] options = {"在线", "维护"};
        String newStatus = (String) JOptionPane.showInputDialog(this,
                "选择新状态：", "修改状态",
                JOptionPane.QUESTION_MESSAGE, null,
                options, currentStatus);

        if (newStatus != null && !newStatus.equals(currentStatus)) {
            GpuServer server = allServers.stream()
                    .filter(s -> s.getServerId().equals(serverId))
                    .findFirst()
                    .orElse(null);

            if (server != null) {
                // 如果要改为维护状态，检查该服务器下是否有已租的显卡
                if ("维护".equals(newStatus)) {
                    List<GpuCard> cards = dataService.getAllGpuCards();
                    boolean hasRented = cards.stream()
                            .filter(c -> c.getServerId().equals(serverId))
                            .anyMatch(c -> "已租".equals(c.getStatus()));

                    if (hasRented) {
                        JOptionPane.showMessageDialog(this,
                                "该服务器下有显卡正在租赁中，无法设置为维护状态！\n请先结束所有租赁。",
                                "警告", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                // 先更新服务器状态
                server.setStatus(newStatus);
                boolean success = dataService.updateServer(server);

                if (success) {
                    // 如果服务器状态变为"维护"，需要更新该服务器下所有显卡的状态为"维护"
                    if ("维护".equals(newStatus)) {
                        updateCardsStatusByServer(serverId, "维护");
                        JOptionPane.showMessageDialog(this,
                                "服务器状态修改成功！\n该服务器下的所有显卡已自动设置为'维护'状态。",
                                "成功", JOptionPane.INFORMATION_MESSAGE);
                    }
                    // 如果服务器状态从"维护"变为"在线"，显卡状态恢复为"空闲"
                    else if ("在线".equals(newStatus) && "维护".equals(currentStatus)) {
                        updateCardsStatusByServer(serverId, "空闲");
                        JOptionPane.showMessageDialog(this,
                                "服务器状态修改成功！\n该服务器下的所有显卡已自动设置为'空闲'状态。",
                                "成功", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "状态修改成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                    }

                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this, "修改失败！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }



    /**
     * 更新指定服务器下的所有显卡状态
     * @param serverId 服务器ID
     * @param newStatus 新状态（中文）
     */
    private void updateCardsStatusByServer(int serverId, String newStatus) {
        List<GpuCard> allCards = dataService.getAllGpuCards();
        List<GpuCard> serverCards = allCards.stream()
                .filter(c -> c.getServerId().equals(serverId))
                .collect(java.util.stream.Collectors.toList());

        for (GpuCard card : serverCards) {
            // 如果显卡是"已租"状态且要改为其他状态，需要特殊处理
            if ("已租".equals(card.getStatus()) && !"已租".equals(newStatus)) {
                System.out.println("警告：显卡 " + card.getCardId() + " 正在租赁中，无法修改状态");
                continue;
            }
            dataService.updateGpuCardStatus(card.getCardId(), newStatus);
        }
    }

    private void showServerDetail() {
        int selectedRow = serverTable.getSelectedRow();
        if (selectedRow < 0) return;

        int serverId = (int) tableModel.getValueAt(selectedRow, 0);
        GpuServer server = allServers.stream()
                .filter(s -> s.getServerId().equals(serverId))
                .findFirst()
                .orElse(null);

        if (server != null) {
            // 获取该服务器下的显卡数量
            List<GpuCard> cards = dataService.getAllGpuCards();
            long cardCount = cards.stream().filter(c -> c.getServerId().equals(serverId)).count();
            long activeCount = cards.stream()
                    .filter(c -> c.getServerId().equals(serverId) && "已租".equals(c.getStatus()))
                    .count();

            StringBuilder detail = new StringBuilder();
            detail.append("服务器详情\n");
            detail.append("═══════════════════\n");
            detail.append("服务器ID: ").append(server.getServerId()).append("\n");
            detail.append("服务器编号: ").append(server.getServerNo()).append("\n");
            detail.append("服务器名称: ").append(server.getServerName()).append("\n");
            detail.append("位置: ").append(server.getLocation()).append("\n");
            detail.append("IP地址: ").append(server.getIpAddress()).append("\n");
            detail.append("状态: ").append(server.getStatus()).append("\n");
            detail.append("显卡数量: ").append(cardCount).append(" 张\n");
            detail.append("正在使用: ").append(activeCount).append(" 张\n");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            detail.append("添加时间: ").append(server.getCreateTime().format(formatter)).append("\n");
            detail.append("═══════════════════");

            JTextArea textArea = new JTextArea(detail.toString());
            textArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            textArea.setEditable(false);
            textArea.setBackground(new Color(245, 245, 245));
            textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JOptionPane.showMessageDialog(this, new JScrollPane(textArea),
                    "服务器详情", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void refreshData() {
        allServers = dataService.getAllServers();
        searchField.setText("");
        statusFilterCombo.setSelectedIndex(0);
        updateTableData(allServers);
    }

    // 自定义单元格渲染器
    private class ServerTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);

            if (!isSelected) {
                String status = (String) table.getValueAt(row, 5);
                if ("在线".equals(status)) {
                    setBackground(new Color(235, 255, 235));
                } else if ("维护".equals(status)) {
                    setBackground(new Color(255, 250, 225));
                } else if ("离线".equals(status)) {
                    setBackground(new Color(255, 235, 235));
                } else {
                    setBackground(Color.WHITE);
                }
            }

            return c;
        }
    }
}