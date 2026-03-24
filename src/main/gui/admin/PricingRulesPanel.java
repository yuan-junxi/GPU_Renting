package main.gui.admin;

import main.model.GpuCard;
import main.model.PricingRule;
import main.service.DataService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PricingRulesPanel extends JPanel {
    private DataService dataService;

    private JTable ruleTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private List<PricingRule> allRules;

    public PricingRulesPanel() {
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
        JLabel titleLabel = new JLabel("计费规则管理", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(241, 196, 15));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // 工具栏
        JPanel toolbarPanel = createToolbar();
        add(toolbarPanel, BorderLayout.NORTH);

        // 表格
        String[] columns = {"规则ID", "显卡型号", "小时单价(¥)", "生效日期", "失效日期", "创建时间"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ruleTable = new JTable(tableModel);
        ruleTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        ruleTable.setRowHeight(35);
        ruleTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        ruleTable.getTableHeader().setBackground(new Color(241, 196, 15));
        ruleTable.getTableHeader().setForeground(Color.BLACK);
        ruleTable.setSelectionBackground(new Color(255, 250, 225));

        // 设置列宽
        ruleTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        ruleTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        ruleTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        ruleTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        ruleTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        ruleTable.getColumnModel().getColumn(5).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(ruleTable);
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
        searchBtn.setBackground(new Color(241, 196, 15));
        searchBtn.setForeground(Color.BLACK);
        searchBtn.setBorderPainted(false);
        searchBtn.setFocusPainted(false);
        searchBtn.setPreferredSize(new Dimension(80, 30));
        searchBtn.addActionListener(e -> searchRules());
        panel.add(searchBtn);

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

        JButton addBtn = new JButton("添加规则");
        addBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        addBtn.setBackground(new Color(46, 204, 113));
        addBtn.setForeground(Color.WHITE);
        addBtn.setBorderPainted(false);
        addBtn.setFocusPainted(false);
        addBtn.setPreferredSize(new Dimension(130, 40));
        addBtn.addActionListener(e -> addRule());

        JButton editBtn = new JButton("修改规则");
        editBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        editBtn.setBackground(new Color(52, 152, 219));
        editBtn.setForeground(Color.WHITE);
        editBtn.setBorderPainted(false);
        editBtn.setFocusPainted(false);
        editBtn.setPreferredSize(new Dimension(130, 40));
        editBtn.addActionListener(e -> editRule());

        JButton deleteBtn = new JButton("删除规则");
        deleteBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setPreferredSize(new Dimension(130, 40));
        deleteBtn.addActionListener(e -> deleteRule());

        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);

        return panel;
    }

    private void setupListeners() {
        ruleTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showRuleDetail();
                }
            }
        });
    }

    private void searchRules() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            updateTableData(allRules);
            return;
        }

        List<PricingRule> filtered = allRules.stream()
                .filter(r -> r.getGpuModel().toLowerCase().contains(keyword))
                .collect(java.util.stream.Collectors.toList());

        updateTableData(filtered);
    }

    private void updateTableData(List<PricingRule> rules) {
        tableModel.setRowCount(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (PricingRule rule : rules) {
            String expireDateStr = rule.getExpireDate() != null ?
                    rule.getExpireDate().format(formatter) : "永久有效";

            Object[] row = {
                    rule.getRuleId(),
                    rule.getGpuModel(),
                    String.format("%.2f", rule.getPriceHourly()),
                    rule.getEffectiveDate().format(formatter),
                    expireDateStr,
                    rule.getCreateTime().format(timeFormatter)
            };
            tableModel.addRow(row);
        }
    }

    private void addRule() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "添加计费规则", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 显卡型号
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("显卡型号:"), gbc);

        gbc.gridx = 1;
        JTextField modelField = new JTextField(15);
        formPanel.add(modelField, gbc);

        // 小时单价
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("小时单价(¥):"), gbc);

        gbc.gridx = 1;
        JTextField priceField = new JTextField(15);
        formPanel.add(priceField, gbc);

        // 生效日期
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("生效日期:"), gbc);

        gbc.gridx = 1;
        JTextField effectiveField = new JTextField(LocalDate.now().toString(), 15);
        formPanel.add(effectiveField, gbc);

        // 失效日期
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("失效日期:"), gbc);

        gbc.gridx = 1;
        JTextField expireField = new JTextField("", 15);
        formPanel.add(expireField, gbc);

        JLabel expireHint = new JLabel("(留空表示永久有效)");
        expireHint.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        expireHint.setForeground(Color.GRAY);
        gbc.gridx = 1;
        gbc.gridy = 4;
        formPanel.add(expireHint, gbc);

        // 按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveBtn = new JButton("保存");
        saveBtn.setBackground(new Color(46, 204, 113));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> {
            try {
                String model = modelField.getText().trim();
                BigDecimal price = new BigDecimal(priceField.getText().trim());
                LocalDate effectiveDate = LocalDate.parse(effectiveField.getText().trim());
                LocalDate expireDate = expireField.getText().trim().isEmpty() ?
                        null : LocalDate.parse(expireField.getText().trim());

                if (model.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请输入显卡型号！");
                    return;
                }

                PricingRule newRule = new PricingRule();
                newRule.setGpuModel(model);
                newRule.setPriceHourly(price);
                newRule.setEffectiveDate(effectiveDate);
                newRule.setExpireDate(expireDate);

                boolean success = dataService.addPricingRule(newRule);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "添加成功！");
                    dialog.dispose();
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "添加失败！");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "输入格式错误！\n" + ex.getMessage());
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

    private void editRule() {
        int selectedRow = ruleTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要修改的规则！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int ruleId = (int) tableModel.getValueAt(selectedRow, 0);
        PricingRule rule = allRules.stream()
                .filter(r -> r.getRuleId().equals(ruleId))
                .findFirst()
                .orElse(null);

        if (rule == null) return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "修改计费规则", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 显卡型号
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("显卡型号:"), gbc);

        gbc.gridx = 1;
        JTextField modelField = new JTextField(rule.getGpuModel(), 15);
        formPanel.add(modelField, gbc);

        // 小时单价
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("小时单价(¥):"), gbc);

        gbc.gridx = 1;
        JTextField priceField = new JTextField(rule.getPriceHourly().toString(), 15);
        formPanel.add(priceField, gbc);

        // 生效日期
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("生效日期:"), gbc);

        gbc.gridx = 1;
        JTextField effectiveField = new JTextField(rule.getEffectiveDate().toString(), 15);
        formPanel.add(effectiveField, gbc);

        // 失效日期
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("失效日期:"), gbc);

        gbc.gridx = 1;
        JTextField expireField = new JTextField(
                rule.getExpireDate() != null ? rule.getExpireDate().toString() : "", 15);
        formPanel.add(expireField, gbc);

        // 按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveBtn = new JButton("保存");
        saveBtn.setBackground(new Color(52, 152, 219));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> {
            try {
                String model = modelField.getText().trim();
                BigDecimal price = new BigDecimal(priceField.getText().trim());
                LocalDate effectiveDate = LocalDate.parse(effectiveField.getText().trim());
                LocalDate expireDate = expireField.getText().trim().isEmpty() ?
                        null : LocalDate.parse(expireField.getText().trim());

                if (model.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请输入显卡型号！");
                    return;
                }

                rule.setGpuModel(model);
                rule.setPriceHourly(price);
                rule.setEffectiveDate(effectiveDate);
                rule.setExpireDate(expireDate);

                boolean success = dataService.updatePricingRule(rule);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "修改成功！");
                    dialog.dispose();
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "修改失败！");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "输入格式错误！\n" + ex.getMessage());
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

    private void deleteRule() {
        int selectedRow = ruleTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的规则！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int ruleId = (int) tableModel.getValueAt(selectedRow, 0);
        String model = (String) tableModel.getValueAt(selectedRow, 1);

        // 检查是否存在该型号的显卡
        List<GpuCard> allCards = dataService.getAllGpuCards();
        boolean hasCards = allCards.stream()
                .anyMatch(c -> c.getModel().equals(model));

        if (hasCards) {
            JOptionPane.showMessageDialog(this,
                    "无法删除此规则！\n存在型号为 [" + model + "] 的显卡。\n请先删除所有该型号的显卡后再删除规则。",
                    "无法删除", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除 [" + model + "] 的计费规则吗？\n此操作不可恢复！",
                "确认删除", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = dataService.deletePricingRule(ruleId);
            if (success) {
                JOptionPane.showMessageDialog(this, "删除成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showRuleDetail() {
        int selectedRow = ruleTable.getSelectedRow();
        if (selectedRow < 0) return;

        int ruleId = (int) tableModel.getValueAt(selectedRow, 0);
        PricingRule rule = allRules.stream()
                .filter(r -> r.getRuleId().equals(ruleId))
                .findFirst()
                .orElse(null);

        if (rule != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            StringBuilder detail = new StringBuilder();
            detail.append("计费规则详情\n");
            detail.append("═══════════════════\n");
            detail.append("规则ID: ").append(rule.getRuleId()).append("\n");
            detail.append("显卡型号: ").append(rule.getGpuModel()).append("\n");
            detail.append("小时单价: ¥").append(String.format("%.2f", rule.getPriceHourly())).append("\n");
            detail.append("生效日期: ").append(rule.getEffectiveDate().format(formatter)).append("\n");
            detail.append("失效日期: ").append(rule.getExpireDate() != null ?
                    rule.getExpireDate().format(formatter) : "永久有效").append("\n");
            detail.append("创建时间: ").append(rule.getCreateTime().format(timeFormatter)).append("\n");
            detail.append("═══════════════════");

            JTextArea textArea = new JTextArea(detail.toString());
            textArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            textArea.setEditable(false);
            textArea.setBackground(new Color(245, 245, 245));
            textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JOptionPane.showMessageDialog(this, new JScrollPane(textArea),
                    "规则详情", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void refreshData() {
        allRules = dataService.getAllPricingRules();
        searchField.setText("");
        updateTableData(allRules);
    }
}