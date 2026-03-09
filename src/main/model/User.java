package main.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class User {
    private Integer userId;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private BigDecimal balance;
    private String role;
    private String status;
    private LocalDateTime createTime;

    // 构造方法
    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.balance = BigDecimal.ZERO;
        this.role = "TENANT";
        this.status = "正常";
        this.createTime = LocalDateTime.now();
    }

    // getter和setter
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    @Override
    public String toString() {
        return "User{" + "username='" + username + '\'' + ", role='" + role + '\'' + '}';
    }
}