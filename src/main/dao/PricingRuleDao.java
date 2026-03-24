package main.dao;

import main.model.PricingRule;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 定价规则数据访问层
 */
public class PricingRuleDao extends BaseDao {

    /**
     * 查询所有定价规则
     */
    public List<PricingRule> findAll() {
        String sql = "SELECT rule_id, gpu_model, price_hourly, effective_date, expire_date, create_time FROM pricing_rule ORDER BY rule_id";
        List<Object[]> resultList = executeQuery(sql, null);
        List<PricingRule> rules = new ArrayList<>();

        for (Object[] row : resultList) {
            rules.add(mapRowToPricingRule(row));
        }
        return rules;
    }

    /**
     * 根据GPU型号查询当前有效的定价规则
     */
    public BigDecimal findPriceByModel(String model) {
        String sql = "SELECT price_hourly FROM pricing_rule WHERE gpu_model LIKE ? AND effective_date <= CURRENT_DATE AND (expire_date IS NULL OR expire_date >= CURRENT_DATE) LIMIT 1";
        List<Object[]> resultList = executeQuery(sql, new Object[]{"%" + model + "%"});

        if (resultList.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return (BigDecimal) resultList.get(0)[0];
    }

    /**
     * 根据ID查询规则
     */
    public PricingRule findById(Integer ruleId) {
        String sql = "SELECT rule_id, gpu_model, price_hourly, effective_date, expire_date, create_time FROM pricing_rule WHERE rule_id = ?";
        List<Object[]> resultList = executeQuery(sql, new Object[]{ruleId});

        if (resultList.isEmpty()) {
            return null;
        }
        return mapRowToPricingRule(resultList.get(0));
    }

    /**
     * 插入定价规则
     */
    public int insert(PricingRule rule) {
        String sql = "INSERT INTO pricing_rule (gpu_model, price_hourly, effective_date, expire_date) VALUES (?, ?, ?, ?)";
        Object[] params = {
                rule.getGpuModel(),
                rule.getPriceHourly(),
                Date.valueOf(rule.getEffectiveDate()),
                rule.getExpireDate() != null ? Date.valueOf(rule.getExpireDate()) : null
        };
        return executeUpdate(sql, params);
    }

    /**
     * 更新定价规则
     */
    public int update(PricingRule rule) {
        String sql = "UPDATE pricing_rule SET gpu_model = ?, price_hourly = ?, effective_date = ?, expire_date = ? WHERE rule_id = ?";
        Object[] params = {
                rule.getGpuModel(),
                rule.getPriceHourly(),
                Date.valueOf(rule.getEffectiveDate()),
                rule.getExpireDate() != null ? Date.valueOf(rule.getExpireDate()) : null,
                rule.getRuleId()
        };
        return executeUpdate(sql, params);
    }

    /**
     * 删除定价规则
     */
    public int delete(Integer ruleId) {
        String sql = "DELETE FROM pricing_rule WHERE rule_id = ?";
        return executeUpdate(sql, new Object[]{ruleId});
    }

    private PricingRule mapRowToPricingRule(Object[] row) {
        PricingRule rule = new PricingRule();
        rule.setRuleId(((Number) row[0]).intValue());
        rule.setGpuModel((String) row[1]);
        rule.setPriceHourly((BigDecimal) row[2]);
        rule.setEffectiveDate(((Date) row[3]).toLocalDate());
        if (row[4] != null) {
            rule.setExpireDate(((Date) row[4]).toLocalDate());
        }

        // 安全处理 createTime
        if (row.length > 5 && row[5] != null) {
            if (row[5] instanceof Timestamp) {
                rule.setCreateTime(((Timestamp) row[5]).toLocalDateTime());
            } else if (row[5] instanceof LocalDateTime) {
                rule.setCreateTime((LocalDateTime) row[5]);
            }
        }

        // 如果 createTime 仍为 null，设置默认值
        if (rule.getCreateTime() == null) {
            rule.setCreateTime(LocalDateTime.now());
        }

        return rule;
    }
}