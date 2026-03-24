package main.gui.admin;

import main.gui.CustomHeaderRenderer;
import main.model.*;
import main.service.DataService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ReportPanel extends JPanel {
    private DataService dataService;

    // 统计面板组件
    private JLabel totalIncomeLabel;
    private JLabel todayIncomeLabel;
    private JLabel monthIncomeLabel;
    private JLabel activeRentalsLabel;
    private JLabel totalUsersLabel;
    private JLabel totalCardsLabel;

    // 图表面板（模拟）
    private JPanel incomeChartPanel;
    private JPanel rentalChartPanel;

    // 表格
    private JTable topRentalsTable;
    private DefaultTableModel topRentalsModel;
    private JTable recentBillsTable;
    private DefaultTableModel recentBillsModel;

    // 时间选择
    private JComboBox<String> yearCombo;
    private JComboBox<String> monthCombo;

    public ReportPanel() {
        this.dataService = DataService.getInstance();

        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 标题
        JLabel titleLabel = new JLabel("数据报表与统计", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(39, 174, 96));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // 创建主内容面板（使用GridBagLayout）
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 第一行：统计卡片
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 0.2;
        mainPanel.add(createStatsPanel(), gbc);

        // 第二行：收入图表和租赁图表
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weighty = 0.3;
        mainPanel.add(createIncomeChartPanel(), gbc);

        gbc.gridx = 1;
        mainPanel.add(createRentalChartPanel(), gbc);

        // 第三行：热门租赁和最近账单
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weighty = 0.5;
        mainPanel.add(createTopRentalsPanel(), gbc);

        gbc.gridx = 1;
        mainPanel.add(createRecentBillsPanel(), gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // 总收入卡片
        panel.add(createStatCard("总收入", totalIncomeLabel = new JLabel("¥0"),
                new Color(46, 204, 113), new Color(39, 174, 96)));

        // 今日收入卡片
        panel.add(createStatCard("今日收入", todayIncomeLabel = new JLabel("¥0"),
                new Color(52, 152, 219), new Color(41, 128, 185)));

        // 本月收入卡片
        panel.add(createStatCard("本月收入", monthIncomeLabel = new JLabel("¥0"),
                new Color(155, 89, 182), new Color(142, 68, 173)));

        // 进行中租赁卡片
        panel.add(createStatCard("进行中租赁", activeRentalsLabel = new JLabel("0"),
                new Color(241, 196, 15), new Color(243, 156, 18)));

        // 总用户数卡片
        panel.add(createStatCard("总用户数", totalUsersLabel = new JLabel("0"),
                new Color(230, 126, 34), new Color(211, 84, 0)));

        // 总显卡数卡片
        panel.add(createStatCard("总显卡数", totalCardsLabel = new JLabel("0"),
                new Color(26, 188, 156), new Color(22, 160, 133)));

        return panel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color bgColor, Color borderColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);

        valueLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createIncomeChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // 标题和选择器
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("收入趋势（近7天）");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));

        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        selectorPanel.setBackground(Color.WHITE);

        yearCombo = new JComboBox<>(new String[]{"2026", "2025", "2024"});
        monthCombo = new JComboBox<>(new String[]{"1月", "2月", "3月", "4月", "5月", "6月",
                "7月", "8月", "9月", "10月", "11月", "12月"});
        monthCombo.setSelectedIndex(LocalDate.now().getMonthValue() - 1);

        JButton refreshBtn = new JButton("刷新");
        refreshBtn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> refreshData());

        selectorPanel.add(new JLabel("年份:"));
        selectorPanel.add(yearCombo);
        selectorPanel.add(new JLabel("月份:"));
        selectorPanel.add(monthCombo);
        selectorPanel.add(refreshBtn);

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(selectorPanel, BorderLayout.EAST);

        // 图表面板（模拟柱状图）
        incomeChartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawIncomeChart(g);
            }
        };
        incomeChartPanel.setBackground(new Color(245, 245, 245));
        incomeChartPanel.setPreferredSize(new Dimension(400, 200));

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(incomeChartPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRentalChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel("显卡租赁热度排行");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // 图表面板（模拟饼图/条形图）
        rentalChartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawRentalChart(g);
            }
        };
        rentalChartPanel.setBackground(new Color(245, 245, 245));
        rentalChartPanel.setPreferredSize(new Dimension(400, 200));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(rentalChartPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTopRentalsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel("热门租赁排行榜");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // 表格
        String[] columns = {"排名", "显卡型号", "租赁次数", "总收入(¥)"};
        topRentalsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        topRentalsTable = new JTable(topRentalsModel);
        topRentalsTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        topRentalsTable.setRowHeight(30);

        // 使用自定义表头渲染器
        topRentalsTable.getTableHeader().setDefaultRenderer(
                new CustomHeaderRenderer(new Color(39, 174, 96), Color.WHITE));

        // 设置列宽
        topRentalsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        topRentalsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        topRentalsTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        topRentalsTable.getColumnModel().getColumn(3).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(topRentalsTable);
        scrollPane.setPreferredSize(new Dimension(400, 200));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRecentBillsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel("最近10笔账单");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // 表格
        String[] columns = {"账单ID", "用户名", "金额(¥)", "类型", "状态", "时间"};
        recentBillsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        recentBillsTable = new JTable(recentBillsModel);
        recentBillsTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        recentBillsTable.setRowHeight(30);

        // 使用自定义表头渲染器
        recentBillsTable.getTableHeader().setDefaultRenderer(
                new CustomHeaderRenderer(new Color(39, 174, 96), Color.WHITE));

        // 设置列宽
        recentBillsTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        recentBillsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        recentBillsTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        recentBillsTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        recentBillsTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        recentBillsTable.getColumnModel().getColumn(5).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(recentBillsTable);
        scrollPane.setPreferredSize(new Dimension(400, 200));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void drawIncomeChart(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int width = incomeChartPanel.getWidth();
        int height = incomeChartPanel.getHeight();

        if (width <= 0 || height <= 0) return;

        // 绘制背景网格
        g2d.setColor(new Color(220, 220, 220));
        for (int i = 0; i <= 5; i++) {
            int y = height - 30 - (i * (height - 60) / 5);
            g2d.drawLine(50, y, width - 20, y);
        }

        // 获取近7天收入数据
        List<Bill> bills = dataService.getAllBills();
        LocalDateTime now = LocalDateTime.now();
        BigDecimal[] dailyIncome = new BigDecimal[7];
        for (int i = 0; i < 7; i++) {
            dailyIncome[i] = BigDecimal.ZERO;
        }

        for (Bill bill : bills) {
            if ("租用".equals(bill.getBillType()) && "已支付".equals(bill.getStatus())) {
                LocalDateTime billTime = bill.getCreateTime();
                long daysDiff = java.time.Duration.between(billTime.toLocalDate().atStartOfDay(),
                        now.toLocalDate().atStartOfDay()).toDays();
                if (daysDiff >= 0 && daysDiff < 7) {
                    dailyIncome[6 - (int) daysDiff] = dailyIncome[6 - (int) daysDiff].add(bill.getAmount());
                }
            }
        }

        // 找到最大值用于缩放
        BigDecimal maxIncome = BigDecimal.ZERO;
        for (BigDecimal income : dailyIncome) {
            if (income.compareTo(maxIncome) > 0) {
                maxIncome = income;
            }
        }
        if (maxIncome.compareTo(BigDecimal.ZERO) == 0) {
            maxIncome = BigDecimal.ONE;
        }

        // 绘制柱状图
        int barWidth = (width - 100) / 14;
        for (int i = 0; i < 7; i++) {
            int x = 60 + i * barWidth * 2;
            double ratio = dailyIncome[i].doubleValue() / maxIncome.doubleValue();
            int barHeight = (int) (ratio * (height - 80));

            // 柱状图颜色渐变
            GradientPaint gradient = new GradientPaint(
                    x, height - 40, new Color(46, 204, 113),
                    x + barWidth, height - 40 - barHeight, new Color(39, 174, 96));
            g2d.setPaint(gradient);
            g2d.fillRect(x, height - 40 - barHeight, barWidth, barHeight);

            // 绘制边框
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, height - 40 - barHeight, barWidth, barHeight);

            // 绘制日期标签
            LocalDate date = now.minusDays(6 - i).toLocalDate();
            String dateStr = date.format(DateTimeFormatter.ofPattern("MM-dd"));
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("微软雅黑", Font.PLAIN, 10));
            g2d.drawString(dateStr, x, height - 20);

            // 绘制金额
            if (dailyIncome[i].compareTo(BigDecimal.ZERO) > 0) {
                String amountStr = "¥" + dailyIncome[i].toString();
                g2d.setColor(Color.BLACK);
                g2d.drawString(amountStr, x, height - 45 - barHeight);
            }
        }
    }

    private void drawRentalChart(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int width = rentalChartPanel.getWidth();
        int height = rentalChartPanel.getHeight();

        if (width <= 0 || height <= 0) return;

        // 获取显卡租赁统计数据
        List<RentalApplication> rentals = dataService.getAllRentals();
        Map<String, Integer> rentalCount = new HashMap<>();
        Map<String, BigDecimal> rentalIncome = new HashMap<>();

        for (RentalApplication rental : rentals) {
            String model = rental.getCardModel();
            rentalCount.put(model, rentalCount.getOrDefault(model, 0) + 1);
            if (rental.getTotalCost() != null) {
                rentalIncome.put(model, rentalIncome.getOrDefault(model, BigDecimal.ZERO)
                        .add(rental.getTotalCost()));
            }
        }

        // 取前5个热门型号
        List<Map.Entry<String, Integer>> topModels = rentalCount.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(5)
                .collect(java.util.stream.Collectors.toList());

        if (topModels.isEmpty()) return;

        int maxCount = topModels.get(0).getValue();
        if (maxCount == 0) maxCount = 1;

        // 绘制条形图
        int barHeight = 25;
        int startY = 40;

        g2d.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        for (int i = 0; i < topModels.size(); i++) {
            Map.Entry<String, Integer> entry = topModels.get(i);
            String model = entry.getKey();
            int count = entry.getValue();

            int y = startY + i * (barHeight + 10);

            // 绘制型号名称
            g2d.setColor(Color.BLACK);
            g2d.drawString(model, 10, y + 18);

            // 绘制条形
            int barLength = (int) ((double) count / maxCount * (width - 150));

            // 渐变色
            GradientPaint gradient = new GradientPaint(
                    120, y, new Color(52, 152, 219),
                    120 + barLength, y + barHeight, new Color(41, 128, 185));
            g2d.setPaint(gradient);
            g2d.fillRect(120, y, barLength, barHeight);

            // 绘制边框
            g2d.setColor(Color.BLACK);
            g2d.drawRect(120, y, barLength, barHeight);

            // 绘制次数
            g2d.setColor(Color.BLACK);
            g2d.drawString(count + "次", 120 + barLength + 10, y + 18);

            // 绘制收入
            BigDecimal income = rentalIncome.getOrDefault(model, BigDecimal.ZERO);
            g2d.drawString("¥" + income, 120 + barLength + 60, y + 18);
        }
    }

    public void refreshData() {
        // 获取所有数据
        List<Bill> bills = dataService.getAllBills();
        List<RentalApplication> rentals = dataService.getAllRentals();
        List<User> users = dataService.getAllUsers();
        List<GpuCard> cards = dataService.getAllGpuCards();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime today = now.toLocalDate().atStartOfDay();
        LocalDateTime monthStart = now.withDayOfMonth(1).toLocalDate().atStartOfDay();

        // 计算总收入
        BigDecimal totalIncome = bills.stream()
                .filter(b -> "租用".equals(b.getBillType()) && "已支付".equals(b.getStatus()))
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 计算今日收入
        BigDecimal todayIncome = bills.stream()
                .filter(b -> "租用".equals(b.getBillType()) && "已支付".equals(b.getStatus()))
                .filter(b -> b.getCreateTime().isAfter(today))
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 计算本月收入
        BigDecimal monthIncome = bills.stream()
                .filter(b -> "租用".equals(b.getBillType()) && "已支付".equals(b.getStatus()))
                .filter(b -> b.getCreateTime().isAfter(monthStart))
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 计算进行中租赁
        long activeRentals = rentals.stream()
                .filter(r -> "进行中".equals(r.getStatus()))
                .count();

        // 更新统计卡片
        totalIncomeLabel.setText("¥" + String.format("%.2f", totalIncome));
        todayIncomeLabel.setText("¥" + String.format("%.2f", todayIncome));
        monthIncomeLabel.setText("¥" + String.format("%.2f", monthIncome));
        activeRentalsLabel.setText(String.valueOf(activeRentals));
        totalUsersLabel.setText(String.valueOf(users.size()));
        totalCardsLabel.setText(String.valueOf(cards.size()));

        // 刷新图表
        incomeChartPanel.repaint();
        rentalChartPanel.repaint();

        // 更新热门租赁排行榜
        updateTopRentals(rentals);

        // 更新最近账单
        updateRecentBills(bills);
    }

    private void updateTopRentals(List<RentalApplication> rentals) {
        // 统计各型号租赁次数和收入
        Map<String, Integer> rentalCount = new HashMap<>();
        Map<String, BigDecimal> rentalIncome = new HashMap<>();

        for (RentalApplication rental : rentals) {
            if (rental.getCardModel() != null) {
                String model = rental.getCardModel();
                rentalCount.put(model, rentalCount.getOrDefault(model, 0) + 1);
                if (rental.getTotalCost() != null) {
                    rentalIncome.put(model, rentalIncome.getOrDefault(model, BigDecimal.ZERO)
                            .add(rental.getTotalCost()));
                }
            }
        }

        // 排序并取前10
        List<Map.Entry<String, Integer>> sorted = rentalCount.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(10)
                .collect(java.util.stream.Collectors.toList());

        topRentalsModel.setRowCount(0);
        int rank = 1;
        for (Map.Entry<String, Integer> entry : sorted) {
            String model = entry.getKey();
            int count = entry.getValue();
            BigDecimal income = rentalIncome.getOrDefault(model, BigDecimal.ZERO);

            Object[] row = {
                    rank++,
                    model,
                    count,
                    String.format("%.2f", income)
            };
            topRentalsModel.addRow(row);
        }
    }

    private void updateRecentBills(List<Bill> bills) {
        // 按时间倒序排序，取最近10条
        List<Bill> recent = bills.stream()
                .sorted((b1, b2) -> b2.getCreateTime().compareTo(b1.getCreateTime()))
                .limit(10)
                .collect(java.util.stream.Collectors.toList());

        recentBillsModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Bill bill : recent) {
            Object[] row = {
                    bill.getBillId(),
                    bill.getUsername() != null ? bill.getUsername() : "-",
                    String.format("%.2f", bill.getAmount()),
                    bill.getBillType(),
                    bill.getStatus(),
                    bill.getCreateTime().format(formatter)
            };
            recentBillsModel.addRow(row);
        }
    }
}