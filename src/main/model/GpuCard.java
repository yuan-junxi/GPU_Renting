package main.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GpuCard {
    private Integer cardId;
    private Integer serverId;
    private String model;
    private Integer memoryGb;
    private BigDecimal priceHourly;
    private String status; // "空闲", "已租", "维护"
    private LocalDateTime createTime;
    private String serverName; // 关联字段

    public GpuCard() {}

    public GpuCard(Integer cardId, Integer serverId, String model, Integer memoryGb,
                   BigDecimal priceHourly, String status, LocalDateTime createTime) {
        this.cardId = cardId;
        this.serverId = serverId;
        this.model = model;
        this.memoryGb = memoryGb;
        this.priceHourly = priceHourly;
        this.status = status;
        this.createTime = createTime;
    }

    // Getters and Setters
    public Integer getCardId() { return cardId; }
    public void setCardId(Integer cardId) { this.cardId = cardId; }
    public Integer getServerId() { return serverId; }
    public void setServerId(Integer serverId) { this.serverId = serverId; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Integer getMemoryGb() { return memoryGb; }
    public void setMemoryGb(Integer memoryGb) { this.memoryGb = memoryGb; }
    public BigDecimal getPriceHourly() { return priceHourly; }
    public void setPriceHourly(BigDecimal priceHourly) { this.priceHourly = priceHourly; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public String getServerName() { return serverName; }
    public void setServerName(String serverName) { this.serverName = serverName; }
}