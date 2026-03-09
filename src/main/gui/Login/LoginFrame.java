package main.gui.Login;

import main.gui.admin.AdminMainFrame;
import main.gui.user.UserMainFrame;
import main.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * 全新设计的GPU租赁系统登录注册界面
 * 特点：布局清晰、交互友好、校验完善、代码健壮
 * 新增：注册模块添加手机号字段
 */
public class LoginFrame extends JFrame {
    // 模拟用户数据库（线程安全的HashMap）
    private final Map<String, String> userDatabase = new HashMap<>();

    // 登录模块组件
    private JTextField tfLoginUsername;
    private JPasswordField pfLoginPassword;

    // 注册模块组件（新增手机号输入框）
    private JTextField tfRegUsername;
    private JPasswordField pfRegPassword;
    private JPasswordField pfRegConfirmPassword;
    private JTextField tfRegPhone; // 新增：手机号输入框

    public LoginFrame() {
        // 初始化界面
        initGUI();
    }

    /**
     * 初始化图形界面（核心布局设计）
     */
    private void initGUI() {
        // ========== 主窗口设置 ==========
        setTitle("GPU租赁管理系统 - 登录中心");
        setSize(700, 550);          // 调整窗口高度，适配新增的手机号字段
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 屏幕居中
        setResizable(false);         // 固定窗口大小，避免布局错乱
        setBackground(Color.WHITE);

        // ========== 主面板（整体布局） ==========
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(Color.WHITE);

        // ========== 标题区域 ==========
        JLabel titleLabel = new JLabel("GPU租赁管理系统", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 60, 110));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // ========== 内容面板（登录+注册左右分栏） ==========
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        contentPanel.setBackground(Color.WHITE);

        // 添加登录面板和注册面板
        contentPanel.add(createLoginPanel());
        contentPanel.add(createRegisterPanel());

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // ========== 底部信息 ==========
        JLabel footerLabel = new JLabel("© 2026 GPU租赁系统 - 所有权利保留", SwingConstants.CENTER);
        footerLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        footerLabel.setForeground(Color.GRAY);
        mainPanel.add(footerLabel, BorderLayout.SOUTH);

        // 添加主面板到窗口
        add(mainPanel);
    }

    /**
     * 创建登录面板（独立模块）
     */
    private JPanel createLoginPanel() {
        // 登录面板主容器
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        // 登录标题
        JLabel loginTitle = new JLabel("用户登录");
        loginTitle.setFont(new Font("微软雅黑", Font.BOLD, 18));
        loginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginTitle.setForeground(new Color(30, 60, 110));

        // 用户名输入区域
        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        usernamePanel.setBackground(Color.WHITE);
        JLabel lblUsername = new JLabel("用户名：");
        lblUsername.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        tfLoginUsername = new JTextField(18);
        tfLoginUsername.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        tfLoginUsername.setPreferredSize(new Dimension(0, 35));
        usernamePanel.add(lblUsername);
        usernamePanel.add(tfLoginUsername);

        // 密码输入区域
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        passwordPanel.setBackground(Color.WHITE);
        JLabel lblPassword = new JLabel("密　码：");
        lblPassword.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        pfLoginPassword = new JPasswordField(18);
        pfLoginPassword.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        pfLoginPassword.setPreferredSize(new Dimension(0, 35));
        passwordPanel.add(lblPassword);
        passwordPanel.add(pfLoginPassword);

        // 登录按钮
        JButton btnLogin = new JButton("登 录");
        btnLogin.setFont(new Font("微软雅黑", Font.BOLD, 14));
        btnLogin.setBackground(new Color(41, 128, 185));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(0, 40));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(new LoginActionHandler());

        // 组装登录面板（添加间距，提升美观度）
        loginPanel.add(loginTitle);
        loginPanel.add(Box.createVerticalStrut(30));
        loginPanel.add(usernamePanel);
        loginPanel.add(Box.createVerticalStrut(15));
        loginPanel.add(passwordPanel);
        loginPanel.add(Box.createVerticalStrut(25));
        loginPanel.add(btnLogin);
        loginPanel.add(Box.createVerticalStrut(15));

        return loginPanel;
    }

    /**
     * 创建注册面板（独立模块）- 新增手机号字段
     */
    private JPanel createRegisterPanel() {
        // 主面板用绝对布局兜底，确保尺寸设置不失效
        JPanel registerPanel = new JPanel(null);
        registerPanel.setBackground(Color.WHITE);
        registerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        // ========== 注册标题 ==========
        JLabel regTitle = new JLabel("用户注册");
        regTitle.setFont(new Font("微软雅黑", Font.BOLD, 18));
        regTitle.setForeground(new Color(22, 160, 133));
        regTitle.setBounds(120, 10, 150, 30);

        // ========== 用户名输入框 ==========
        JLabel lblRegUsername = new JLabel("用户名：");
        lblRegUsername.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        lblRegUsername.setBounds(30, 60, 80, 40);

        tfRegUsername = new JTextField();
        tfRegUsername.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        tfRegUsername.setBounds(110, 60, 180, 40);
        tfRegUsername.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));

        // ========== 密码输入框 ==========
        JLabel lblRegPassword = new JLabel("密　码：");
        lblRegPassword.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        lblRegPassword.setBounds(30, 110, 80, 40);

        pfRegPassword = new JPasswordField();
        pfRegPassword.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        pfRegPassword.setBounds(110, 110, 180, 40);
        pfRegPassword.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));

        // ========== 确认密码输入框 ==========
        JLabel lblRegConfirm = new JLabel("确认密码：");
        lblRegConfirm.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        lblRegConfirm.setBounds(30, 160, 80, 40);

        pfRegConfirmPassword = new JPasswordField();
        pfRegConfirmPassword.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        pfRegConfirmPassword.setBounds(110, 160, 180, 40);
        pfRegConfirmPassword.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));

        // ========== 新增：手机号输入框 ==========
        JLabel lblRegPhone = new JLabel("手机号：");
        lblRegPhone.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        lblRegPhone.setBounds(30, 210, 80, 40);

        tfRegPhone = new JTextField();
        tfRegPhone.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        tfRegPhone.setBounds(110, 210, 180, 40);
        tfRegPhone.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));

        // ========== 注册按钮（调整位置适配手机号字段） ==========
        JButton btnRegister = new JButton("注 册");
        btnRegister.setFont(new Font("微软雅黑", Font.BOLD, 14));
        btnRegister.setBackground(new Color(22, 160, 133));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setBorderPainted(false);
        btnRegister.setFocusPainted(false);
        btnRegister.setBounds(80, 270, 200, 45); // 向下调整按钮位置
        btnRegister.addActionListener(new RegisterActionHandler());

        // ========== 添加所有组件 ==========
        registerPanel.add(regTitle);
        registerPanel.add(lblRegUsername);
        registerPanel.add(tfRegUsername);
        registerPanel.add(lblRegPassword);
        registerPanel.add(pfRegPassword);
        registerPanel.add(lblRegConfirm);
        registerPanel.add(pfRegConfirmPassword);
        registerPanel.add(lblRegPhone);       // 新增：添加手机号标签
        registerPanel.add(tfRegPhone);        // 新增：添加手机号输入框
        registerPanel.add(btnRegister);

        return registerPanel;
    }

    /**
     * 登录事件处理器（独立类，解耦）
     */
    private class LoginActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 获取输入值（去除首尾空格）
            String username = tfLoginUsername.getText().trim();
            String password = new String(pfLoginPassword.getPassword()).trim();

            System.out.println("username:"+username);
            System.out.println("password:"+password);
            System.out.println("login success!");

            //调用Service层去查询账号密码对应的状态
            UserService userService = new UserService();
            int status=userService.login(username, password);

            /*
             * 1.空值
             * 2.用户名不存在
             * 3.密码错误
             * 4.登录成功
             * */

            // 1. 空值校验
            if (status==1) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "用户名或密码为空！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. 用户名存在性校验
            if (status==2) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "用户名不存在！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 3. 密码正确性校验
            if (status==3) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "密码错误！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 4.登录成功
            if (status==4) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "登录成功！欢迎管理员使用GPU租赁系统，" + username,
                        "成功", JOptionPane.INFORMATION_MESSAGE);
                // 清空登录表单
                tfLoginUsername.setText("");
                pfLoginPassword.setText("");

                // 登录成功后跳转到主界面
                dispose(); // 关闭登录窗口
                new AdminMainFrame(username).setVisible(true);; // 打开管理员主界面
                return;
            }

            JOptionPane.showMessageDialog(LoginFrame.this,
                    "登录成功！欢迎用户使用GPU租赁系统，" + username,
                    "成功", JOptionPane.INFORMATION_MESSAGE);

            // 清空登录表单
            tfLoginUsername.setText("");
            pfLoginPassword.setText("");

            // 登录成功后跳转到主界面
            dispose(); // 关闭登录窗口

            new UserMainFrame(username).setVisible(true); // 打开用户主界面

            JOptionPane.showMessageDialog(LoginFrame.this,
                    "登录成功！欢迎管理员使用GPU租赁系统，" + username,
                    "成功", JOptionPane.INFORMATION_MESSAGE);

        }
    }

    /**
     * 注册事件处理器（独立类，解耦）- 新增手机号参数传递
     */
    private class RegisterActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 获取输入值（新增手机号）
            String username = tfRegUsername.getText().trim();
            String password = new String(pfRegPassword.getPassword()).trim();
            String confirmPwd = new String(pfRegConfirmPassword.getPassword()).trim();
            String phone = tfRegPhone.getText().trim(); // 新增：获取手机号

            System.out.println("username:"+username+"password:"+password+"confirmPwd:"+confirmPwd+"phone:"+phone);

            // 新增：手机号非空校验（可选，根据业务需求调整）
            if (phone.isEmpty()) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "请输入手机号！", "错误", JOptionPane.ERROR_MESSAGE);
                tfRegPhone.requestFocus(); // 光标定位到手机号输入框
                return;
            }

            // 新增：手机号格式简单校验（可选）
            if (!phone.matches("^1[3-9]\\d{9}$")) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "请输入正确的手机号格式！", "错误", JOptionPane.ERROR_MESSAGE);
                tfRegPhone.selectAll(); // 全选错误手机号
                tfRegPhone.requestFocus();
                return;
            }

            UserService userService = new UserService();
            // 新增：传递手机号参数到Service层（需同步修改UserService的register方法）
            int status=userService.register(username, password, confirmPwd, phone);

            // 1. 空值校验
            if (status==1) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "请输入用户名！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. 密码一致性校验
            if (status==2) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "两次输入的密码不一致！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 3. 用户名唯一性校验
            if (status==3) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "用户名已存在！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 4. 注册成功
            if(status==4){
                userDatabase.put(username, password);
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "注册成功！您可以使用新账号登录了",
                        "成功", JOptionPane.INFORMATION_MESSAGE);
            }

            // 清空注册表单（新增清空手机号）
            tfRegUsername.setText("");
            pfRegPassword.setText("");
            pfRegConfirmPassword.setText("");
            tfRegPhone.setText(""); // 新增：清空手机号输入框
        }
    }

    /**
     * 程序入口（规范的Swing启动方式）
     */
    public static void main(String[] args) {
        // 设置系统原生外观，提升兼容性
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // 在EDT线程中创建UI（Swing规范）
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}