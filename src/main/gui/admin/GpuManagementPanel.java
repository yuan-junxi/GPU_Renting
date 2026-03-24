package main.gui.admin;

import main.gui.CustomHeaderRenderer;
import main.model.GpuCard;
import main.model.GpuServer;
import main.service.DataService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GpuManagementPanel extends JPanel {
    private DataService dataService;

    private JTable gpuTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilterCombo;
    private JComboBox<String> modelFilterCombo;
    private List<GpuCard> allGpus;
    private List<GpuServer> servers;

    public GpuManagementPanel() {
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
        JLabel titleLabel = new JLabel("显卡管理", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(155, 89, 182));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // 工具栏
        JPanel toolbarPanel = createToolbar();
        add(toolbarPanel, BorderLayout.NORTH);

        // 表格
        String[] columns = {"显卡ID", "型号", "显存(GB)", "小时单价(¥)", "状态", "服务器", "添加时间"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        gpuTable = new JTable(tableModel);
        gpuTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        gpuTable.setRowHeight(35);
        gpuTable.setSelectionBackground(new Color(245, 230, 255));

        // 使用自定义表头渲染器
        JTableHeader header = gpuTable.getTableHeader();
        header.setDefaultRenderer(new CustomHeaderRenderer(new Color(155, 89, 182), Color.WHITE));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // 设置列宽
        gpuTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        gpuTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        gpuTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        gpuTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        gpuTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        gpuTable.getColumnModel().getColumn(5).setPreferredWidth(150);
        gpuTable.getColumnModel().getColumn(6).setPreferredWidth(150);

        // 设置单元格渲染器
        gpuTable.setDefaultRenderer(Object.class, new GpuTableCellRenderer());

        JScrollPane scrollPane = new JScrollPane(gpuTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // 底部操作面板
        JPanel actionPanel = createActionPanel();
        add(actionPanel, BorderLayout.SOUTH);
    }

    private JPanel createToolbar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));

        panel.add(new JLabel("搜索型号:"));

        searchField = new JTextField(15);
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(200, 30));
        panel.add(searchField);

        JButton searchBtn = new JButton("搜索");
        searchBtn.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        searchBtn.setBackground(new Color(155, 89, 182));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setBorderPainted(false);
        searchBtn.setFocusPainted(false);
        searchBtn.setPreferredSize(new Dimension(80, 30));
        searchBtn.addActionListener(e -> searchGpus());
        panel.add(searchBtn);

        panel.add(new JLabel("型号过滤:"));

        String[] models = {"全部", "A100", "H100", "RTX 4090", "RTX 4080"};
        modelFilterCombo = new JComboBox<>(models);
        modelFilterCombo.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        modelFilterCombo.setPreferredSize(new Dimension(100, 30));
        modelFilterCombo.addActionListener(e -> filterGpus());
        panel.add(modelFilterCombo);

        panel.add(new JLabel("状态过滤:"));

        String[] statuses = {"全部", "空闲", "已租", "维护"};
        statusFilterCombo = new JComboBox<>(statuses);
        statusFilterCombo.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        statusFilterCombo.setPreferredSize(new Dimension(100, 30));
        statusFilterCombo.addActionListener(e -> filterGpus());
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

        JButton addBtn = new JButton("添加显卡");
        addBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        addBtn.setBackground(new Color(46, 204, 113));
        addBtn.setForeground(Color.WHITE);
        addBtn.setBorderPainted(false);
        addBtn.setFocusPainted(false);
        addBtn.setPreferredSize(new Dimension(120, 40));
        addBtn.addActionListener(e -> addGpu());

        JButton editBtn = new JButton("修改信息");
        editBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        editBtn.setBackground(new Color(52, 152, 219));
        editBtn.setForeground(Color.WHITE);
        editBtn.setBorderPainted(false);
        editBtn.setFocusPainted(false);
        editBtn.setPreferredSize(new Dimension(120, 40));
        editBtn.addActionListener(e -> editGpu());

        JButton deleteBtn = new JButton("删除显卡");
        deleteBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setPreferredSize(new Dimension(120, 40));
        deleteBtn.addActionListener(e -> deleteGpu());

        JButton statusBtn = new JButton("修改状态");
        statusBtn.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        statusBtn.setBackground(new Color(241, 196, 15));
        statusBtn.setForeground(Color.WHITE);
        statusBtn.setBorderPainted(false);
        statusBtn.setFocusPainted(false);
        statusBtn.setPreferredSize(new Dimension(120, 40));
        statusBtn.addActionListener(e -> changeStatus());

        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);
        panel.add(statusBtn);

        return panel;
    }

    private void setupListeners() {
        // 双击查看详情
        gpuTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showGpuDetail();
                }
            }
        });
    }

    private void searchGpus() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            filterGpus();
            return;
        }

        List<GpuCard> filtered = allGpus.stream()
                .filter(g -> g.getModel().toLowerCase().contains(keyword))
                .collect(java.util.stream.Collectors.toList());

        updateTableData(filtered);
    }

    private void filterGpus() {
        String modelFilter = (String) modelFilterCombo.getSelectedItem();
        String statusFilter = (String) statusFilterCombo.getSelectedItem();

        List<GpuCard> filtered = new java.util.ArrayList<>(allGpus);

        if (!"全部".equals(modelFilter)) {
            filtered.removeIf(g -> !g.getModel().contains(modelFilter));
        }

        if (!"全部".equals(statusFilter)) {
            filtered.removeIf(g -> !statusFilter.equals(g.getStatus()));
        }

        updateTableData(filtered);
    }

    private void updateTableData(List<GpuCard> gpus) {
        tableModel.setRowCount(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (GpuCard gpu : gpus) {
            Object[] row = {
                    gpu.getCardId(),
                    gpu.getModel(),
                    gpu.getMemoryGb(),
                    String.format("%.2f", gpu.getPriceHourly()),
                    gpu.getStatus(),
                    gpu.getServerName(),
                    gpu.getCreateTime().format(formatter)
            };
            tableModel.addRow(row);
        }
    }

    private void addGpu() {
        servers = dataService.getAllServers();
        if (servers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先添加服务器！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "添加显卡", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 型号
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("型号:"), gbc);

        gbc.gridx = 1;
        JTextField modelField = new JTextField(15);
        formPanel.add(modelField, gbc);

        // 显存
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("显存(GB):"), gbc);

        gbc.gridx = 1;
        JTextField memoryField = new JTextField(15);
        formPanel.add(memoryField, gbc);

        // 小时单价 - 改为自动获取，不可编辑
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("小时单价(¥):"), gbc);

        gbc.gridx = 1;
        JTextField priceField = new JTextField(15);
        priceField.setEditable(false);
        priceField.setBackground(new Color(240, 240, 240));
        formPanel.add(priceField, gbc);

        // 添加提示标签
        JLabel priceHint = new JLabel("(根据计费规则自动获取)");
        priceHint.setFont(new Font("微软雅黑", Font.PLAIN, 10));
        priceHint.setForeground(Color.GRAY);
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(priceHint, gbc);

        // 服务器
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("所属服务器:"), gbc);

        gbc.gridx = 1;
        String[] serverNames = servers.stream()
                .map(s -> s.getServerId() + " - " + s.getServerName())
                .toArray(String[]::new);
        JComboBox<String> serverCombo = new JComboBox<>(serverNames);
        formPanel.add(serverCombo, gbc);

        // 状态
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("初始状态:"), gbc);

        gbc.gridx = 1;
        String[] statuses = {"空闲", "维护"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        formPanel.add(statusCombo, gbc);

        // 型号输入监听，自动获取价格
        modelField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                String model = modelField.getText().trim();
                if (!model.isEmpty()) {
                    BigDecimal price = dataService.getPriceByModel(model);
                    if (price.compareTo(BigDecimal.ZERO) > 0) {
                        priceField.setText(String.format("%.2f", price));
                    } else {
                        priceField.setText("未找到规则，请先添加计费规则");
                        priceField.setForeground(Color.RED);
                    }
                }
            }
        });

        // 按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveBtn = new JButton("保存");
        saveBtn.setBackground(new Color(46, 204, 113));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> {
            try {
                String model = modelField.getText().trim();
                int memory = Integer.parseInt(memoryField.getText().trim());
                BigDecimal price = new BigDecimal(priceField.getText().trim());
                int serverIndex = serverCombo.getSelectedIndex();
                GpuServer selectedServer = servers.get(serverIndex);
                String status = (String) statusCombo.getSelectedItem();

                if (model.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请输入显卡型号！");
                    return;
                }

                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(dialog, "请先添加该型号的计费规则！");
                    return;
                }

                GpuCard newCard = new GpuCard();
                newCard.setModel(model);
                newCard.setMemoryGb(memory);
                newCard.setPriceHourly(price);
                newCard.setServerId(selectedServer.getServerId());
                newCard.setServerName(selectedServer.getServerName());
                newCard.setStatus(status);

                boolean success = dataService.addGpuCard(newCard);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "添加成功！");
                    dialog.dispose();
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "添加失败！");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "请输入有效的数字！");
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

    private void editGpu() {
        int selectedRow = gpuTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要修改的显卡！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cardId = (int) tableModel.getValueAt(selectedRow, 0);
        GpuCard card = allGpus.stream()
                .filter(g -> g.getCardId().equals(cardId))
                .findFirst()
                .orElse(null);

        if (card == null) return;

        servers = dataService.getAllServers();

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "修改显卡信息", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 型号
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("型号:"), gbc);

        gbc.gridx = 1;
        JTextField modelField = new JTextField(card.getModel(), 15);
        formPanel.add(modelField, gbc);

        // 显存
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("显存(GB):"), gbc);

        gbc.gridx = 1;
        JTextField memoryField = new JTextField(String.valueOf(card.getMemoryGb()), 15);
        formPanel.add(memoryField, gbc);

        // 小时单价
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("小时单价(¥):"), gbc);

        gbc.gridx = 1;
        JTextField priceField = new JTextField(card.getPriceHourly().toString(), 15);
        formPanel.add(priceField, gbc);

        // 服务器
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("所属服务器:"), gbc);

        gbc.gridx = 1;
        String[] serverNames = servers.stream()
                .map(s -> s.getServerId() + " - " + s.getServerName())
                .toArray(String[]::new);
        JComboBox<String> serverCombo = new JComboBox<>(serverNames);

        // 设置当前选中的服务器
        for (int i = 0; i < servers.size(); i++) {
            if (servers.get(i).getServerId().equals(card.getServerId())) {
                serverCombo.setSelectedIndex(i);
                break;
            }
        }
        formPanel.add(serverCombo, gbc);

        // 按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveBtn = new JButton("保存");
        saveBtn.setBackground(new Color(52, 152, 219));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> {
            try {
                String model = modelField.getText().trim();
                int memory = Integer.parseInt(memoryField.getText().trim());
                BigDecimal price = new BigDecimal(priceField.getText().trim());
                int serverIndex = serverCombo.getSelectedIndex();
                GpuServer selectedServer = servers.get(serverIndex);

                if (model.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请输入显卡型号！");
                    return;
                }

                card.setModel(model);
                card.setMemoryGb(memory);
                card.setPriceHourly(price);
                card.setServerId(selectedServer.getServerId());
                card.setServerName(selectedServer.getServerName());

                boolean success = dataService.updateGpuCard(card);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "修改成功！");
                    dialog.dispose();
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "修改失败！");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "请输入有效的数字！");
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

    private void deleteGpu() {
        int selectedRow = gpuTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的显卡！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cardId = (int) tableModel.getValueAt(selectedRow, 0);
        String model = (String) tableModel.getValueAt(selectedRow, 1);
        String status = (String) tableModel.getValueAt(selectedRow, 4);

        if ("已租".equals(status)) {
            JOptionPane.showMessageDialog(this, "正在租赁中的显卡不能删除！", "警告", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除显卡 [" + model + "] 吗？\n此操作不可恢复！",
                "确认删除", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = dataService.deleteGpuCard(cardId);
            if (success) {
                JOptionPane.showMessageDialog(this, "删除成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void changeStatus() {
        int selectedRow = gpuTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要修改状态的显卡！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cardId = (int) tableModel.getValueAt(selectedRow, 0);
        String model = (String) tableModel.getValueAt(selectedRow, 1);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 4);

        // 已租的显卡不能修改状态
        if ("已租".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this,
                    "正在租赁中的显卡不能修改状态！\n请先结束租赁。",
                    "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 获取显卡所属服务器
        GpuCard card = allGpus.stream()
                .filter(c -> c.getCardId().equals(cardId))
                .findFirst()
                .orElse(null);

        if (card != null) {
            // 获取显卡所属服务器
            GpuServer server = dataService.getAllServers().stream()
                    .filter(s -> s.getServerId().equals(card.getServerId()))
                    .findFirst()
                    .orElse(null);

            // 如果服务器状态为"维护"，显卡状态只能为"维护"，不能修改
            if (server != null && "维护".equals(server.getStatus())) {
                JOptionPane.showMessageDialog(this,
                        "该显卡所属服务器处于维护状态，无法修改显卡状态！\n请先修改服务器状态。",
                        "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        String[] options = {"空闲", "维护"};
        String newStatus = (String) JOptionPane.showInputDialog(this,
                "选择新状态：", "修改状态",
                JOptionPane.QUESTION_MESSAGE, null,
                options, currentStatus);

        if (newStatus != null && !newStatus.equals(currentStatus)) {
            boolean success = dataService.updateGpuCardStatus(cardId, newStatus);
            if (success) {
                JOptionPane.showMessageDialog(this, "状态修改成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "修改失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showGpuDetail() {
        int selectedRow = gpuTable.getSelectedRow();
        if (selectedRow < 0) return;

        int cardId = (int) tableModel.getValueAt(selectedRow, 0);
        GpuCard card = allGpus.stream()
                .filter(g -> g.getCardId().equals(cardId))
                .findFirst()
                .orElse(null);

        if (card != null) {
            StringBuilder detail = new StringBuilder();
            detail.append("显卡详情\n");
            detail.append("═══════════════════\n");
            detail.append("显卡ID: ").append(card.getCardId()).append("\n");
            detail.append("型号: ").append(card.getModel()).append("\n");
            detail.append("显存: ").append(card.getMemoryGb()).append(" GB\n");
            detail.append("小时单价: ¥").append(String.format("%.2f", card.getPriceHourly())).append("\n");
            detail.append("当前状态: ").append(card.getStatus()).append("\n");
            detail.append("所属服务器: ").append(card.getServerName()).append("\n");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            detail.append("添加时间: ").append(card.getCreateTime().format(formatter)).append("\n");
            detail.append("═══════════════════");

            JTextArea textArea = new JTextArea(detail.toString());
            textArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            textArea.setEditable(false);
            textArea.setBackground(new Color(245, 245, 245));
            textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JOptionPane.showMessageDialog(this, new JScrollPane(textArea),
                    "显卡详情", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void refreshData() {
        allGpus = dataService.getAllGpuCards();
        servers = dataService.getAllServers();
        searchField.setText("");
        modelFilterCombo.setSelectedIndex(0);
        statusFilterCombo.setSelectedIndex(0);
        updateTableData(allGpus);
    }

    // 自定义单元格渲染器
    private class GpuTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);

            if (!isSelected) {
                String status = (String) table.getValueAt(row, 4);
                if ("空闲".equals(status)) {
                    setBackground(new Color(235, 255, 235));
                } else if ("已租".equals(status)) {
                    setBackground(new Color(255, 235, 235));
                } else if ("维护".equals(status)) {
                    setBackground(new Color(255, 250, 225));
                } else {
                    setBackground(Color.WHITE);
                }
            }

            return c;
        }
    }
}