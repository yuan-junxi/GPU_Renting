package main.dao;

import main.model.GpuCard;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * GPU卡数据访问层
 */
public class GpuCardDao extends BaseDao {

    /**
     * 查询所有显卡
     */
    public List<GpuCard> findAll() {
        String sql = "SELECT c.card_id, c.server_id, c.model, c.memory_gb, c.price_hourly, c.status, c.create_time, s.server_name " +
                "FROM gpu_card c LEFT JOIN gpu_server s ON c.server_id = s.server_id ORDER BY c.card_id";
        List<Object[]> resultList = executeQuery(sql, null);
        List<GpuCard> cards = new ArrayList<>();

        for (Object[] row : resultList) {
            cards.add(mapRowToGpuCard(row));
        }
        return cards;
    }

    /**
     * 查询空闲显卡
     */
    public List<GpuCard> findAvailable() {
        String sql = "SELECT c.card_id, c.server_id, c.model, c.memory_gb, c.price_hourly, c.status, c.create_time, s.server_name " +
                "FROM gpu_card c LEFT JOIN gpu_server s ON c.server_id = s.server_id WHERE c.status = 'idle' ORDER BY c.card_id";
        List<Object[]> resultList = executeQuery(sql, null);
        List<GpuCard> cards = new ArrayList<>();

        for (Object[] row : resultList) {
            cards.add(mapRowToGpuCard(row));
        }
        return cards;
    }

    // GpuCardDao.java
    /**
     * 直接更新显卡状态（传入的已经是数据库状态）
     */
    public int updateStatusDirect(Integer cardId, String dbStatus) {
        String sql = "UPDATE gpu_card SET status = ? WHERE card_id = ?";
        return executeUpdate(sql, new Object[]{dbStatus, cardId});
    }

    /**
     * 根据型号查询显卡
     */
    public List<GpuCard> findByModel(String model) {
        String sql = "SELECT c.card_id, c.server_id, c.model, c.memory_gb, c.price_hourly, c.status, c.create_time, s.server_name " +
                "FROM gpu_card c LEFT JOIN gpu_server s ON c.server_id = s.server_id WHERE c.model LIKE ? ORDER BY c.card_id";
        List<Object[]> resultList = executeQuery(sql, new Object[]{"%" + model + "%"});
        List<GpuCard> cards = new ArrayList<>();

        for (Object[] row : resultList) {
            cards.add(mapRowToGpuCard(row));
        }
        return cards;
    }

    /**
     * 根据ID查询显卡
     */
    public GpuCard findById(Integer cardId) {
        String sql = "SELECT c.card_id, c.server_id, c.model, c.memory_gb, c.price_hourly, c.status, c.create_time, s.server_name " +
                "FROM gpu_card c LEFT JOIN gpu_server s ON c.server_id = s.server_id WHERE c.card_id = ?";
        List<Object[]> resultList = executeQuery(sql, new Object[]{cardId});

        if (resultList.isEmpty()) {
            return null;
        }
        return mapRowToGpuCard(resultList.get(0));
    }

    /**
     * 插入显卡
     */
    public int insert(GpuCard card) {
        String sql = "INSERT INTO gpu_card (server_id, model, memory_gb, price_hourly, status) VALUES (?, ?, ?, ?, ?)";
        Object[] params = {
                card.getServerId(),
                card.getModel(),
                card.getMemoryGb(),
                card.getPriceHourly(),
                card.getStatus() != null ? card.getStatus() : "idle"
        };
        return executeUpdate(sql, params);
    }

    /**
     * 更新显卡
     */
    public int update(GpuCard card) {
        String sql = "UPDATE gpu_card SET server_id = ?, model = ?, memory_gb = ?, price_hourly = ?, status = ? WHERE card_id = ?";
        Object[] params = {
                card.getServerId(),
                card.getModel(),
                card.getMemoryGb(),
                card.getPriceHourly(),
                card.getStatus(),
                card.getCardId()
        };
        return executeUpdate(sql, params);
    }

    /**
     * 更新显卡状态
     */
    public int updateStatus(Integer cardId, String status) {
        String sql = "UPDATE gpu_card SET status = ? WHERE card_id = ?";
        return executeUpdate(sql, new Object[]{status, cardId});
    }

    /**
     * 删除显卡
     */
    public int delete(Integer cardId) {
        String sql = "DELETE FROM gpu_card WHERE card_id = ?";
        return executeUpdate(sql, new Object[]{cardId});
    }

    private GpuCard mapRowToGpuCard(Object[] row) {
        GpuCard card = new GpuCard();
        int index = 0;

        card.setCardId(((Number) row[index++]).intValue());
        card.setServerId(((Number) row[index++]).intValue());
        card.setModel((String) row[index++]);
        card.setMemoryGb(((Number) row[index++]).intValue());
        card.setPriceHourly((BigDecimal) row[index++]);

        // 将英文状态转换为中文
        String dbStatus = (String) row[index++];
        String chineseStatus = convertStatusToChinese(dbStatus);
        card.setStatus(chineseStatus);

        // 安全处理 createTime
        if (row[index] != null) {
            if (row[index] instanceof Timestamp) {
                card.setCreateTime(((Timestamp) row[index]).toLocalDateTime());
            } else if (row[index] instanceof LocalDateTime) {
                card.setCreateTime((LocalDateTime) row[index]);
            }
        }
        index++;

        // 如果 createTime 仍为 null，设置默认值
        if (card.getCreateTime() == null) {
            card.setCreateTime(LocalDateTime.now());
        }

        // 处理 serverName（可能为 null）
        if (row.length > index && row[index] != null) {
            card.setServerName((String) row[index]);
        } else {
            card.setServerName("未知服务器");
        }

        return card;
    }

    /**
     * 将数据库状态转换为中文
     */
    private String convertStatusToChinese(String status) {
        if (status == null) return "未知";
        switch (status) {
            case "idle":
                return "空闲";
            case "rented":
                return "已租";
            case "maintenance":
                return "维护";
            default:
                return status;
        }
    }
}