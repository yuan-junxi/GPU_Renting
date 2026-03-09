package main.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class User {
    private Integer userId;
    private String username;
    private String password;
    private String phone;
    private BigDecimal balance;
    private Integer role; // 0:管理员, 1:租户
    private String status; // "active", "disabled"
    private LocalDateTime createTime;

    public User() {}

    public User(Integer userId, String username, String password, String phone,
                BigDecimal balance, Integer role, String status, LocalDateTime createTime) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.balance = balance;
        this.role = role;
        this.status = status;
        this.createTime = createTime;
    }

    // Getters and Setters
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public Integer getRole() { return role; }
    public void setRole(Integer role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}