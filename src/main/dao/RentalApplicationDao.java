package main.dao;

import main.model.RentalApplication;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 租用申请数据访问层
 */
public class RentalApplicationDao extends BaseDao {

    /**
     * 查询所有租用记录
     */
    public List<RentalApplication> findAll() {
        String sql = "SELECT r.rental_id, r.user_id, r.card_id, r.start_time, r.end_time, " +
                "r.total_hours, r.total_cost, r.status, r.create_time, " +
                "u.username, c.model as card_model, c.price_hourly " +
                "FROM rental_application r " +
                "LEFT JOIN User u ON r.user_id = u.user_id " +
                "LEFT JOIN gpu_card c ON r.card_id = c.card_id " +
                "ORDER BY r.rental_id DESC";
        List<Object[]> resultList = executeQuery(sql, null);
        List<RentalApplication> rentals = new ArrayList<>();

        for (Object[] row : resultList) {
            rentals.add(mapRowToRentalApplication(row));
        }
        return rentals;
    }

    /**
     * 根据用户ID查询租用记录
     */
    public List<RentalApplication> findByUserId(Integer userId) {
        String sql = "SELECT r.rental_id, r.user_id, r.card_id, r.start_time, r.end_time, " +
                "r.total_hours, r.total_cost, r.status, r.create_time, " +
                "u.username, c.model as card_model, c.price_hourly " +
                "FROM rental_application r " +
                "LEFT JOIN User u ON r.user_id = u.user_id " +
                "LEFT JOIN gpu_card c ON r.card_id = c.card_id " +
                "WHERE r.user_id = ? ORDER BY r.rental_id DESC";
        List<Object[]> resultList = executeQuery(sql, new Object[]{userId});
        List<RentalApplication> rentals = new ArrayList<>();

        for (Object[] row : resultList) {
            rentals.add(mapRowToRentalApplication(row));
        }
        return rentals;
    }

    /**
     * 查询进行中的租用记录
     */
    public List<RentalApplication> findActive() {
        String sql = "SELECT r.rental_id, r.user_id, r.card_id, r.start_time, r.end_time, " +
                "r.total_hours, r.total_cost, r.status, r.create_time, " +
                "u.username, c.model as card_model, c.price_hourly " +
                "FROM rental_application r " +
                "LEFT JOIN User u ON r.user_id = u.user_id " +
                "LEFT JOIN gpu_card c ON r.card_id = c.card_id " +
                "WHERE r.status = 'ongoing' ORDER BY r.rental_id DESC";
        List<Object[]> resultList = executeQuery(sql, null);
        List<RentalApplication> rentals = new ArrayList<>();

        for (Object[] row : resultList) {
            rentals.add(mapRowToRentalApplication(row));
        }
        return rentals;
    }

    /**
     * 根据ID查询租用记录
     */
    public RentalApplication findById(Integer rentalId) {
        String sql = "SELECT r.rental_id, r.user_id, r.card_id, r.start_time, r.end_time, " +
                "r.total_hours, r.total_cost, r.status, r.create_time, " +
                "u.username, c.model as card_model, c.price_hourly " +
                "FROM rental_application r " +
                "LEFT JOIN User u ON r.user_id = u.user_id " +
                "LEFT JOIN gpu_card c ON r.card_id = c.card_id " +
                "WHERE r.rental_id = ?";
        List<Object[]> resultList = executeQuery(sql, new Object[]{rentalId});

        if (resultList.isEmpty()) {
            return null;
        }
        return mapRowToRentalApplication(resultList.get(0));
    }

    /**
     * 插入租用记录
     */
    public int insert(RentalApplication rental) {
        String sql = "INSERT INTO rental_application (user_id, card_id, start_time, status) VALUES (?, ?, ?, ?)";

        // 确保 start_time 不为空
        LocalDateTime startTime = rental.getStartTime();
        if (startTime == null) {
            startTime = LocalDateTime.now();
            rental.setStartTime(startTime);
        }

        // 将中文状态转换为数据库状态
        String dbStatus = convertStatusToDB(rental.getStatus());

        Object[] params = {
                rental.getUserId(),
                rental.getCardId(),
                Timestamp.valueOf(startTime),
                dbStatus != null ? dbStatus : "ongoing"
        };
        return executeUpdate(sql, params);
    }
    /**
     * 结束租用（更新结束时间、总时长、总费用、状态）
     */
    public int endRental(Integer rentalId, LocalDateTime endTime, BigDecimal totalHours, BigDecimal totalCost) {
        String sql = "UPDATE rental_application SET end_time = ?, total_hours = ?, total_cost = ?, status = 'ended' WHERE rental_id = ? AND status = 'ongoing'";
        Object[] params = {
                Timestamp.valueOf(endTime),
                totalHours,
                totalCost,
                rentalId
        };
        return executeUpdate(sql, params);
    }

    /**
     * 检查显卡是否已被租用（进行中）
     */
    public boolean isCardRented(Integer cardId) {
        String sql = "SELECT COUNT(*) FROM rental_application WHERE card_id = ? AND status = 'ongoing'";
        List<Object[]> resultList = executeQuery(sql, new Object[]{cardId});
        if (!resultList.isEmpty()) {
            Number count = (Number) resultList.get(0)[0];
            return count.intValue() > 0;
        }
        return false;
    }

    /**
     * 将数据库状态转换为中文
     */
    private String convertStatusToChinese(String status) {
        if (status == null) return "未知";
        switch (status) {
            case "ongoing":
                return "进行中";
            case "ended":
                return "已结束";
            case "force_end":
                return "强制结束";
            default:
                return status;
        }
    }

    /**
     * 将中文状态转换为数据库状态
     */
    private String convertStatusToDB(String status) {
        if (status == null) return "ongoing";
        switch (status) {
            case "进行中":
                return "ongoing";
            case "已结束":
                return "ended";
            case "强制结束":
                return "force_end";
            default:
                return status;
        }
    }

    private RentalApplication mapRowToRentalApplication(Object[] row) {
        RentalApplication rental = new RentalApplication();
        int index = 0;

        try {
            // 1. rental_id
            rental.setRentalId(((Number) row[index++]).intValue());

            // 2. user_id
            rental.setUserId(((Number) row[index++]).intValue());

            // 3. card_id
            rental.setCardId(((Number) row[index++]).intValue());

            // 4. start_time - 修复：支持 Timestamp 和 LocalDateTime
            if (row[index] != null) {
                if (row[index] instanceof Timestamp) {
                    rental.setStartTime(((Timestamp) row[index]).toLocalDateTime());
                    System.out.println("设置开始时间(Timestamp): " + rental.getStartTime());
                } else if (row[index] instanceof LocalDateTime) {
                    rental.setStartTime((LocalDateTime) row[index]);
                    System.out.println("设置开始时间(LocalDateTime): " + rental.getStartTime());
                } else {
                    System.out.println("开始时间类型未知: " + row[index].getClass());
                }
            } else {
                System.out.println("开始时间为空");
            }
            index++;

            // 5. end_time - 修复：支持 Timestamp 和 LocalDateTime
            if (row[index] != null) {
                if (row[index] instanceof Timestamp) {
                    rental.setEndTime(((Timestamp) row[index]).toLocalDateTime());
                } else if (row[index] instanceof LocalDateTime) {
                    rental.setEndTime((LocalDateTime) row[index]);
                }
            }
            index++;

            // 6. total_hours
            if (row[index] != null) {
                rental.setTotalHours((BigDecimal) row[index]);
            }
            index++;

            // 7. total_cost
            if (row[index] != null) {
                rental.setTotalCost((BigDecimal) row[index]);
            }
            index++;

            // 8. status - 转换为中文
            String dbStatus = (String) row[index++];
            rental.setStatus(convertStatusToChinese(dbStatus));

            // 9. create_time - 修复：支持 Timestamp 和 LocalDateTime
            if (row[index] != null) {
                if (row[index] instanceof Timestamp) {
                    rental.setCreateTime(((Timestamp) row[index]).toLocalDateTime());
                } else if (row[index] instanceof LocalDateTime) {
                    rental.setCreateTime((LocalDateTime) row[index]);
                }
            }
            index++;

            // 10. username
            if (row.length > index && row[index] != null) {
                rental.setUsername((String) row[index]);
            }
            index++;

            // 11. card_model
            if (row.length > index && row[index] != null) {
                rental.setCardModel((String) row[index]);
            }
            index++;

            // 12. price_hourly
            if (row.length > index && row[index] != null) {
                rental.setPriceHourly((BigDecimal) row[index]);
            }

            System.out.println("映射完成 - 租赁ID: " + rental.getRentalId() +
                    ", 开始时间: " + rental.getStartTime() +
                    ", 状态: " + rental.getStatus());

        } catch (Exception e) {
            System.err.println("映射租赁记录时出错: " + e.getMessage());
            e.printStackTrace();
        }

        return rental;
    }
}