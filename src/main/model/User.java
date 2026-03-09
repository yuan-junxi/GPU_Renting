package main.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户实体类（严格对应数据库User表）
 * 表字段映射：
 * - user_id: 主键，自增
 * - username: 用户名，唯一约束
 * - password: 密码（建议存储加密后字符串）
 * - phone: 手机号
 * - balance: 账户余额，默认0.00
 * - role: 角色（0=管理员，1=租户）
 * - create_time: 创建时间（数据库自动生成）
 */
public class User {
    // 私有成员变量（与数据库字段一一对应，驼峰命名）
    private Integer userId;
    private String username;
    private String password;
    private String phone;
    private BigDecimal balance;
    private Integer role;
    private LocalDateTime createTime;

    /**
     * 空参构造器
     * 必须保留：适配JDBC反射、JSON反序列化等场景
     */
    public User() {
        // 初始化默认值，避免空指针
        this.balance = BigDecimal.ZERO;
        this.role = 1; // 默认租户角色
        this.createTime = LocalDateTime.now();
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        // 初始化默认值，避免空指针
        this.balance = BigDecimal.ZERO;
        this.role = 1; // 默认租户角色
        this.createTime = LocalDateTime.now();
    }

    public User(String username, String password, String phone) {
        this.username = username;
        this.password = password;
        this.phone = phone;
        // 初始化默认值，避免空指针
        this.balance = BigDecimal.ZERO;
        this.role = 1; // 默认租户角色
        this.createTime = LocalDateTime.now();
    }


    /**
     * 注册专用构造器（无需主键和创建时间）
     * @param username 用户名
     * @param password 密码
     * @param phone 手机号
     * @param balance 账户余额
     * @param role 角色（0=管理员，1=租户）
     */
    public User(String username, String password, String phone, BigDecimal balance, Integer role) {
        this.username = username;
        this.password = password;
        this.phone = (phone == null ? "" : phone);
        this.balance = (balance == null ? BigDecimal.ZERO : balance);
        this.role = (role == null ? 1 : role);
    }

    /**
     * 全参构造器（查询结果映射专用）
     * @param userId 主键ID
     * @param username 用户名
     * @param password 密码
     * @param phone 手机号
     * @param balance 余额
     * @param role 角色
     * @param createTime 创建时间
     */
    public User(Integer userId, String username, String password, String phone, BigDecimal balance, Integer role, LocalDateTime createTime) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.phone = (phone == null ? "" : phone);
        this.balance = (balance == null ? BigDecimal.ZERO : balance);
        this.role = (role == null ? 1 : role);
        this.createTime = createTime;
    }

    // ==================== Getter & Setter 方法（完整且规范） ====================
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = (balance == null ? BigDecimal.ZERO : balance);
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = (role == null ? 1 : role);
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /**
     * 重写toString方法（调试专用）
     * 隐藏密码字段，避免敏感信息泄露
     */
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", balance=" + balance +
                ", role=" + (role == 0 ? "管理员" : "租户") +
                ", createTime=" + createTime +
                '}';
    }

    /**
     * 辅助方法：判断是否为管理员
     * 提升业务代码可读性
     */
    public boolean isAdmin() {
        return this.role != null && this.role == 0;
    }

    /**
     * 辅助方法：判断是否为租户
     * 提升业务代码可读性
     */
    public boolean isTenant() {
        return this.role != null && this.role == 1;
    }
}