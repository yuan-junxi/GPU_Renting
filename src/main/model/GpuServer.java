package main.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GpuServer {
    private Integer serverId;
    private String serverNo;
    private String serverName;
    private String location;
    private String ipAddress;
    private String status; // "在线", "维护", "离线"
    private LocalDateTime createTime;

    public GpuServer() {}

    public GpuServer(Integer serverId, String serverNo, String serverName, String location,
                     String ipAddress, String status, LocalDateTime createTime) {
        this.serverId = serverId;
        this.serverNo = serverNo;
        this.serverName = serverName;
        this.location = location;
        this.ipAddress = ipAddress;
        this.status = status;
        this.createTime = createTime;
    }

    // Getters and Setters
    public Integer getServerId() { return serverId; }
    public void setServerId(Integer serverId) { this.serverId = serverId; }
    public String getServerNo() { return serverNo; }
    public void setServerNo(String serverNo) { this.serverNo = serverNo; }
    public String getServerName() { return serverName; }
    public void setServerName(String serverName) { this.serverName = serverName; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}