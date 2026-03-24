package main.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PricingRule {
    private Integer ruleId;
    private String gpuModel;
    private BigDecimal priceHourly;
    private LocalDate effectiveDate;
    private LocalDate expireDate;
    private LocalDateTime createTime;

    public PricingRule() {}

    public PricingRule(Integer ruleId, String gpuModel, BigDecimal priceHourly,
                       LocalDate effectiveDate, LocalDate expireDate, LocalDateTime createTime) {
        this.ruleId = ruleId;
        this.gpuModel = gpuModel;
        this.priceHourly = priceHourly;
        this.effectiveDate = effectiveDate;
        this.expireDate = expireDate;
        this.createTime = createTime;
    }

    // Getters and Setters
    public Integer getRuleId() { return ruleId; }
    public void setRuleId(Integer ruleId) { this.ruleId = ruleId; }
    public String getGpuModel() { return gpuModel; }
    public void setGpuModel(String gpuModel) { this.gpuModel = gpuModel; }
    public BigDecimal getPriceHourly() { return priceHourly; }
    public void setPriceHourly(BigDecimal priceHourly) { this.priceHourly = priceHourly; }
    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }
    public LocalDate getExpireDate() { return expireDate; }
    public void setExpireDate(LocalDate expireDate) { this.expireDate = expireDate; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}