package main.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Bill {
    private Integer billId;
    private Integer userId;
    private Integer rentalId;
    private Integer recordId;
    private BigDecimal amount;
    private String billType; // "租用", "充值"
    private String status; // "已支付", "待支付", "逾期"
    private LocalDateTime createTime;
    private LocalDateTime payTime;

    // 关联字段
    private String username;

    public Bill() {}

    // Getters and Setters
    public Integer getBillId() { return billId; }
    public void setBillId(Integer billId) { this.billId = billId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Integer getRentalId() { return rentalId; }
    public void setRentalId(Integer rentalId) { this.rentalId = rentalId; }
    public Integer getRecordId() { return recordId; }
    public void setRecordId(Integer recordId) { this.recordId = recordId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getBillType() { return billType; }
    public void setBillType(String billType) { this.billType = billType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getPayTime() { return payTime; }
    public void setPayTime(LocalDateTime payTime) { this.payTime = payTime; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}