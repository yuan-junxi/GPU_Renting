package main.dao;

import main.model.Bill;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 账单数据访问层
 */
public class BillDao extends BaseDao {

    /**
     * 查询所有账单
     */
    public List<Bill> findAll() {
        String sql = "SELECT b.bill_id, b.user_id, b.rental_id, b.record_id, b.amount, " +
                "b.bill_type, b.status, b.create_time, b.pay_time, u.username " +
                "FROM bill b LEFT JOIN User u ON b.user_id = u.user_id " +
                "ORDER BY b.create_time DESC";
        List<Object[]> resultList = executeQuery(sql, null);
        List<Bill> bills = new ArrayList<>();

        for (Object[] row : resultList) {
            bills.add(mapRowToBill(row));
        }
        return bills;
    }

    /**
     * 根据用户ID查询账单
     */
    public List<Bill> findByUserId(Integer userId) {
        String sql = "SELECT b.bill_id, b.user_id, b.rental_id, b.record_id, b.amount, " +
                "b.bill_type, b.status, b.create_time, b.pay_time, u.username " +
                "FROM bill b LEFT JOIN User u ON b.user_id = u.user_id " +
                "WHERE b.user_id = ? ORDER BY b.create_time DESC";
        List<Object[]> resultList = executeQuery(sql, new Object[]{userId});
        List<Bill> bills = new ArrayList<>();

        for (Object[] row : resultList) {
            bills.add(mapRowToBill(row));
        }
        return bills;
    }

    /**
     * 查询所有租用账单（已支付）
     */
    public BigDecimal getTotalRentalIncome() {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM bill WHERE bill_type = 'rent' AND status = 'paid'";
        List<Object[]> resultList = executeQuery(sql, null);
        if (!resultList.isEmpty()) {
            return (BigDecimal) resultList.get(0)[0];
        }
        return BigDecimal.ZERO;
    }

    /**
     * 按时间段统计收入
     */
    public Map<String, BigDecimal> getIncomeByPeriod(LocalDateTime start, LocalDateTime end) {
        Map<String, BigDecimal> result = new HashMap<>();
        result.put("total", BigDecimal.ZERO);
        result.put("count", BigDecimal.ZERO);

        String sql = "SELECT COALESCE(SUM(amount), 0) as total, COUNT(*) as count " +
                "FROM bill WHERE bill_type = 'rent' AND status = 'paid' " +
                "AND create_time BETWEEN ? AND ?";
        Object[] params = {
                Timestamp.valueOf(start),
                Timestamp.valueOf(end)
        };
        List<Object[]> resultList = executeQuery(sql, params);

        if (!resultList.isEmpty()) {
            result.put("total", (BigDecimal) resultList.get(0)[0]);
            result.put("count", new BigDecimal(((Number) resultList.get(0)[1]).intValue()));
        }
        return result;
    }

    /**
     * 插入账单
     */
    public int insert(Bill bill) {
        String sql = "INSERT INTO bill (user_id, rental_id, record_id, amount, bill_type, status, pay_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        // 将中文账单类型和状态转换为数据库格式
        String dbBillType = convertBillTypeToDB(bill.getBillType());
        String dbStatus = convertStatusToDB(bill.getStatus());

        Object[] params = {
                bill.getUserId(),
                bill.getRentalId(),
                bill.getRecordId(),
                bill.getAmount(),
                dbBillType,
                dbStatus,
                bill.getPayTime() != null ? Timestamp.valueOf(bill.getPayTime()) : null
        };
        return executeUpdate(sql, params);
    }

    /**
     * 更新账单状态
     */
    public int updateStatus(Integer billId, String status, LocalDateTime payTime) {
        String sql = "UPDATE bill SET status = ?, pay_time = ? WHERE bill_id = ?";

        // 将中文状态转换为数据库格式
        String dbStatus = convertStatusToDB(status);

        Object[] params = {
                dbStatus,
                payTime != null ? Timestamp.valueOf(payTime) : null,
                billId
        };
        return executeUpdate(sql, params);
    }

    /**
     * 将中文账单类型转换为数据库格式
     */
    private String convertBillTypeToDB(String billType) {
        if (billType == null) return "rent";
        switch (billType) {
            case "租用":
                return "rent";
            case "充值":
                return "recharge";
            default:
                return billType;
        }
    }

    /**
     * 将数据库账单类型转换为中文
     */
    private String convertBillTypeToChinese(String billType) {
        if (billType == null) return "未知";
        switch (billType) {
            case "rent":
                return "租用";
            case "recharge":
                return "充值";
            default:
                return billType;
        }
    }

    /**
     * 将中文状态转换为数据库格式
     */
    private String convertStatusToDB(String status) {
        if (status == null) return "pending";
        switch (status) {
            case "已支付":
                return "paid";
            case "待支付":
                return "pending";
            case "逾期":
                return "overdue";
            default:
                return status;
        }
    }

    /**
     * 将数据库状态转换为中文
     */
    private String convertStatusToChinese(String status) {
        if (status == null) return "未知";
        switch (status) {
            case "paid":
                return "已支付";
            case "pending":
                return "待支付";
            case "overdue":
                return "逾期";
            default:
                return status;
        }
    }

    private Bill mapRowToBill(Object[] row) {
        Bill bill = new Bill();
        int index = 0;

        try {
            bill.setBillId(((Number) row[index++]).intValue());
            bill.setUserId(((Number) row[index++]).intValue());

            if (row[index] != null) {
                bill.setRentalId(((Number) row[index]).intValue());
            }
            index++;

            if (row[index] != null) {
                bill.setRecordId(((Number) row[index]).intValue());
            }
            index++;

            bill.setAmount((BigDecimal) row[index++]);

            // 账单类型 - 转换为中文
            String dbBillType = (String) row[index++];
            bill.setBillType(convertBillTypeToChinese(dbBillType));

            // 状态 - 转换为中文
            String dbStatus = (String) row[index++];
            bill.setStatus(convertStatusToChinese(dbStatus));

            // 处理 createTime - 支持 Timestamp 和 LocalDateTime
            if (row[index] != null) {
                if (row[index] instanceof Timestamp) {
                    bill.setCreateTime(((Timestamp) row[index]).toLocalDateTime());
                } else if (row[index] instanceof LocalDateTime) {
                    bill.setCreateTime((LocalDateTime) row[index]);
                }
            }
            index++;

            // 如果 createTime 仍为 null，设置默认值
            if (bill.getCreateTime() == null) {
                bill.setCreateTime(LocalDateTime.now());
            }

            // 处理 payTime - 支持 Timestamp 和 LocalDateTime
            if (row[index] != null) {
                if (row[index] instanceof Timestamp) {
                    bill.setPayTime(((Timestamp) row[index]).toLocalDateTime());
                } else if (row[index] instanceof LocalDateTime) {
                    bill.setPayTime((LocalDateTime) row[index]);
                }
            }
            index++;

            // username
            if (row.length > index && row[index] != null) {
                bill.setUsername((String) row[index]);
            }

            System.out.println("映射账单 - ID: " + bill.getBillId() +
                    ", 类型: " + bill.getBillType() +
                    ", 状态: " + bill.getStatus() +
                    ", 支付时间: " + bill.getPayTime());

        } catch (Exception e) {
            System.err.println("映射账单记录时出错: " + e.getMessage());
            e.printStackTrace();
        }

        return bill;
    }
}