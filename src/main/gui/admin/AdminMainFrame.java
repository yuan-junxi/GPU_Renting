package main.gui.admin;

import main.model.User;
import main.service.UserService;

import javax.swing.*;
import java.awt.*;

public class AdminMainFrame extends JFrame {
    private String username;
    private User currentUser;
    private JTabbedPane tabbedPane;
    private UserService userService;

    // 各功能面板
    private UserManagementPanel userManagementPanel;
    private GpuManagementPanel gpuManagementPanel;
    private ServerManagementPanel serverManagementPanel;
    private PricingRulesPanel pricingRulesPanel;
    private ReportPanel reportPanel;

    public AdminMainFrame(String username) {
        this.username = username;
        this.userService = new UserService();
        this.currentUser = userService.getUserByUsername(username);

        // 设置全局字体和UI样式
        setUIFont();

        initComponents();
        setupLayout();
        setupMenuBar();

        setTitle("GPU租赁系统 - 管理端 [" + username + "]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);
    }

    /**
     * 设置全局字体和UI样式
     */
    private void setUIFont() {
        // 设置全局字体
        Font font = new Font("微软雅黑", Font.PLAIN, 12);
        UIManager.put("Button.font", font);
        UIManager.put("Label.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("Table.font", font);
        UIManager.put("TableHeader.font", new Font("微软雅黑", Font.BOLD, 13));
        UIManager.put("ComboBox.font", font);
        UIManager.put("TabbedPane.font", new Font("微软雅黑", Font.PLAIN, 14));
        UIManager.put("Menu.font", new Font("微软雅黑", Font.PLAIN, 14));
        UIManager.put("MenuItem.font", new Font("微软雅黑", Font.PLAIN, 14));

        // 设置表头背景色和前景色
        UIManager.put("TableHeader.background", new Color(52, 152, 219));
        UIManager.put("TableHeader.foreground", Color.WHITE);
    }

    private void initComponents() {
        // 创建选项卡面板
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        tabbedPane.setBackground(new Color(245, 245, 245));

        // 初始化各个功能面板
        userManagementPanel = new UserManagementPanel();
        gpuManagementPanel = new GpuManagementPanel();
        serverManagementPanel = new ServerManagementPanel();
        pricingRulesPanel = new PricingRulesPanel();
        reportPanel = new ReportPanel();

        // 添加选项卡
        tabbedPane.addTab("用户管理", userManagementPanel);
        tabbedPane.addTab("显卡管理", gpuManagementPanel);
        tabbedPane.addTab("服务器管理", serverManagementPanel);
        tabbedPane.addTab("计费规则", pricingRulesPanel);
        tabbedPane.addTab("报表查询", reportPanel);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // 顶部信息栏
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // 中间选项卡面板
        add(tabbedPane, BorderLayout.CENTER);

        // 底部状态栏
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);

        // 启动定时更新状态栏时间
        startStatusBarTimer(statusPanel);
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // 系统菜单
        JMenu systemMenu = new JMenu("系统");
        systemMenu.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        JMenuItem refreshItem = new JMenuItem("刷新所有数据");
        refreshItem.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        refreshItem.addActionListener(e -> refreshAllData());

        JMenuItem exitItem = new JMenuItem("退出");
        exitItem.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        exitItem.addActionListener(e -> logout());

        systemMenu.add(refreshItem);
        systemMenu.addSeparator();
        systemMenu.add(exitItem);

        // 帮助菜单
        JMenu helpMenu = new JMenu("帮助");
        helpMenu.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        JMenuItem aboutItem = new JMenuItem("关于系统");
        aboutItem.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        aboutItem.addActionListener(e -> showAboutDialog());

        helpMenu.add(aboutItem);

        menuBar.add(systemMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(44, 62, 80));
        topPanel.setPreferredSize(new Dimension(getWidth(), 65));

        // 左侧Logo和标题
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        JLabel logoLabel = new JLabel("🎮 GPU租赁系统");
        logoLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        logoLabel.setForeground(Color.WHITE);

        JLabel adminLabel = new JLabel("【管理端】");
        adminLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        adminLabel.setForeground(new Color(255, 215, 0));
        adminLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        leftPanel.add(logoLabel);
        leftPanel.add(adminLabel);

        // 右侧用户信息
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));

        JLabel welcomeLabel = new JLabel("欢迎，管理员 " + username);
        welcomeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        welcomeLabel.setForeground(Color.WHITE);

        JButton logoutBtn = new JButton("退出登录");
        logoutBtn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        logoutBtn.setBackground(new Color(231, 76, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setPreferredSize(new Dimension(100, 32));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> logout());

        rightPanel.add(welcomeLabel);
        rightPanel.add(Box.createHorizontalStrut(15));
        rightPanel.add(logoutBtn);

        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        return topPanel;
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        statusPanel.setBackground(new Color(236, 240, 241));
        statusPanel.setPreferredSize(new Dimension(getWidth(), 35));
        statusPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(189, 195, 199)));

        JLabel statusLabel = new JLabel("当前时间: " + java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(52, 73, 94));

        JLabel userCountLabel = new JLabel("在线状态: 管理端已连接");
        userCountLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        userCountLabel.setForeground(new Color(46, 204, 113));

        statusPanel.add(statusLabel);
        statusPanel.add(userCountLabel);

        return statusPanel;
    }

    /**
     * 启动定时器更新状态栏时间
     */
    private void startStatusBarTimer(JPanel statusPanel) {
        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            Component[] components = statusPanel.getComponents();
            if (components.length > 0 && components[0] instanceof JLabel) {
                JLabel timeLabel = (JLabel) components[0];
                timeLabel.setText("当前时间: " + java.time.LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
        });
        timer.start();
    }

    private void refreshAllData() {
        // 刷新各个面板的数据
        if (userManagementPanel != null) {
            userManagementPanel.refreshData();
        }
        if (gpuManagementPanel != null) {
            gpuManagementPanel.refreshData();
        }
        if (serverManagementPanel != null) {
            serverManagementPanel.refreshData();
        }
        if (pricingRulesPanel != null) {
            pricingRulesPanel.refreshData();
        }
        if (reportPanel != null) {
            reportPanel.refreshData();
        }

        JOptionPane.showMessageDialog(this,
                "所有数据已刷新成功！",
                "刷新成功",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要退出登录吗？",
                "确认退出",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new main.gui.Login.LoginFrame().setVisible(true);
        }
    }

    private void showAboutDialog() {
        String message = "=================================\n" +
                "      GPU租赁系统 - 管理端\n" +
                "=================================\n\n" +
                "版本: 1.0.0\n" +
                "© 2026 版权所有\n\n" +
                "【管理端功能】\n" +
                "• 用户管理 - 查询、禁用/启用用户\n" +
                "• 显卡管理 - 增删改查显卡，修改状态\n" +
                "• 服务器管理 - 管理GPU服务器\n" +
                "• 计费规则 - 设置各型号小时单价\n" +
                "• 报表查询 - 查看收入统计和租赁情况\n\n" +
                "=================================";

        JTextArea textArea = new JTextArea(message);
        textArea.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        textArea.setEditable(false);
        textArea.setBackground(new Color(245, 245, 245));
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JOptionPane.showMessageDialog(this,
                new JScrollPane(textArea),
                "关于系统",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 获取当前登录用户
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * 刷新当前用户信息
     */
    public void refreshCurrentUser() {
        this.currentUser = userService.getUserByUsername(username);
    }
}