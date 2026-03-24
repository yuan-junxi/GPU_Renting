package main.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RentalApplication {
    private Integer rentalId;
    private Integer userId;
    private Integer cardId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal totalHours;
    private BigDecimal totalCost;
    private String status; // "进行中", "已结束", "强制结束"
    private LocalDateTime createTime;

    // 关联字段
    private String username;
    private String cardModel;
    private BigDecimal priceHourly;

    public RentalApplication() {}

    // Getters and Setters
    public Integer getRentalId() { return rentalId; }
    public void setRentalId(Integer rentalId) { this.rentalId = rentalId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Integer getCardId() { return cardId; }
    public void setCardId(Integer cardId) { this.cardId = cardId; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public BigDecimal getTotalHours() { return totalHours; }
    public void setTotalHours(BigDecimal totalHours) { this.totalHours = totalHours; }
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getCardModel() { return cardModel; }
    public void setCardModel(String cardModel) { this.cardModel = cardModel; }
    public BigDecimal getPriceHourly() { return priceHourly; }
    public void setPriceHourly(BigDecimal priceHourly) { this.priceHourly = priceHourly; }
}