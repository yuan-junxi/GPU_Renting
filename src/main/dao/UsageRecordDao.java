package main.dao;

import main.model.UsageRecord;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用记录数据访问层
 */
public class UsageRecordDao extends BaseDao {

    /**
     * 查询所有使用记录
     */
    public List<UsageRecord> findAll() {
        String sql = "SELECT u.record_id, u.rental_id, u.user_id, u.card_id, u.hours, u.amount, " +
                "u.balance_before, u.balance_after, u.create_time, " +
                "us.username, c.model as card_model " +
                "FROM usage_record u " +
                "LEFT JOIN User us ON u.user_id = us.user_id " +
                "LEFT JOIN gpu_card c ON u.card_id = c.card_id " +
                "ORDER BY u.create_time DESC";
        List<Object[]> resultList = executeQuery(sql, null);
        List<UsageRecord> records = new ArrayList<>();

        for (Object[] row : resultList) {
            records.add(mapRowToUsageRecord(row));
        }
        return records;
    }

    /**
     * 根据用户ID查询使用记录
     */
    public List<UsageRecord> findByUserId(Integer userId) {
        String sql = "SELECT u.record_id, u.rental_id, u.user_id, u.card_id, u.hours, u.amount, " +
                "u.balance_before, u.balance_after, u.create_time, " +
                "us.username, c.model as card_model " +
                "FROM usage_record u " +
                "LEFT JOIN User us ON u.user_id = us.user_id " +
                "LEFT JOIN gpu_card c ON u.card_id = c.card_id " +
                "WHERE u.user_id = ? ORDER BY u.create_time DESC";
        List<Object[]> resultList = executeQuery(sql, new Object[]{userId});
        List<UsageRecord> records = new ArrayList<>();

        for (Object[] row : resultList) {
            records.add(mapRowToUsageRecord(row));
        }
        return records;
    }

    /**
     * 根据租用ID查询使用记录
     */
    public List<UsageRecord> findByRentalId(Integer rentalId) {
        String sql = "SELECT u.record_id, u.rental_id, u.user_id, u.card_id, u.hours, u.amount, " +
                "u.balance_before, u.balance_after, u.create_time, " +
                "us.username, c.model as card_model " +
                "FROM usage_record u " +
                "LEFT JOIN User us ON u.user_id = us.user_id " +
                "LEFT JOIN gpu_card c ON u.card_id = c.card_id " +
                "WHERE u.rental_id = ? ORDER BY u.create_time DESC";
        List<Object[]> resultList = executeQuery(sql, new Object[]{rentalId});
        List<UsageRecord> records = new ArrayList<>();

        for (Object[] row : resultList) {
            records.add(mapRowToUsageRecord(row));
        }
        return records;
    }

    /**
     * 插入使用记录
     */
    public int insert(UsageRecord record) {
        String sql = "INSERT INTO usage_record (rental_id, user_id, card_id, hours, amount, balance_before, balance_after) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Object[] params = {
                record.getRentalId(),
                record.getUserId(),
                record.getCardId(),
                record.getHours(),
                record.getAmount(),
                record.getBalanceBefore(),
                record.getBalanceAfter()
        };
        return executeUpdate(sql, params);
    }

    private UsageRecord mapRowToUsageRecord(Object[] row) {
        UsageRecord record = new UsageRecord();
        int index = 0;

        record.setRecordId(((Number) row[index++]).intValue());
        record.setRentalId(((Number) row[index++]).intValue());
        record.setUserId(((Number) row[index++]).intValue());
        record.setCardId(((Number) row[index++]).intValue());
        record.setHours((BigDecimal) row[index++]);
        record.setAmount((BigDecimal) row[index++]);
        record.setBalanceBefore((BigDecimal) row[index++]);
        record.setBalanceAfter((BigDecimal) row[index++]);

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

        record.setUsername((String) row[index++]);
        record.setCardModel((String) row[index]);

        return record;
    }
}