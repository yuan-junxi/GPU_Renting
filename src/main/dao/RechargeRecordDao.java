package main.dao;

import main.model.RechargeRecord;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 充值记录数据访问层
 */
public class RechargeRecordDao extends BaseDao {

    /**
     * 查询所有充值记录
     */
    public List<RechargeRecord> findAll() {
        String sql = "SELECT r.recharge_id, r.user_id, r.amount, r.balance_before, " +
                "r.balance_after, r.payment_method, r.status, r.create_time, u.username " +
                "FROM recharge_record r LEFT JOIN User u ON r.user_id = u.user_id " +
                "ORDER BY r.create_time DESC";
        List<Object[]> resultList = executeQuery(sql, null);
        List<RechargeRecord> records = new ArrayList<>();

        for (Object[] row : resultList) {
            records.add(mapRowToRechargeRecord(row));
        }
        return records;
    }

    /**
     * 根据用户ID查询充值记录
     */
    public List<RechargeRecord> findByUserId(Integer userId) {
        String sql = "SELECT r.recharge_id, r.user_id, r.amount, r.balance_before, " +
                "r.balance_after, r.payment_method, r.status, r.create_time, u.username " +
                "FROM recharge_record r LEFT JOIN User u ON r.user_id = u.user_id " +
                "WHERE r.user_id = ? ORDER BY r.create_time DESC";
        List<Object[]> resultList = executeQuery(sql, new Object[]{userId});
        List<RechargeRecord> records = new ArrayList<>();

        for (Object[] row : resultList) {
            records.add(mapRowToRechargeRecord(row));
        }
        return records;
    }

    /**
     * 插入充值记录
     */
    public int insert(RechargeRecord record) {
        String sql = "INSERT INTO recharge_record (user_id, amount, balance_before, balance_after, payment_method, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        Object[] params = {
                record.getUserId(),
                record.getAmount(),
                record.getBalanceBefore(),
                record.getBalanceAfter(),
                record.getPaymentMethod(),
                record.getStatus() != null ? record.getStatus() : "success"
        };
        return executeUpdate(sql, params);
    }

    private RechargeRecord mapRowToRechargeRecord(Object[] row) {
        RechargeRecord record = new RechargeRecord();
        int index = 0;

        record.setRechargeId(((Number) row[index++]).intValue());
        record.setUserId(((Number) row[index++]).intValue());
        record.setAmount((BigDecimal) row[index++]);
        record.setBalanceBefore((BigDecimal) row[index++]);
        record.setBalanceAfter((BigDecimal) row[index++]);
        record.setPaymentMethod((String) row[index++]);
        record.setStatus((String) row[index++]);

        // 安全处理 createTime
        if (row[index] != null) {
            if (row[index] instanceof Timestamp) {
                record.setCreateTime(((Timestamp) row[index]).toLocalDateTime());
            } else if (row[index] instanceof LocalDateTime) {
                record.setCreateTime((LocalDateTime) row[index]);
            }
        }
        index++;

        // 如果 createTime 仍为 null，设置默认值
        if (record.getCreateTime() == null) {
            record.setCreateTime(LocalDateTime.now());
        }

        record.setUsername((String) row[index]);

        return record;
    }
}