package main.dao;

import main.model.GpuServer;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * GPU服务器数据访问层
 */
public class GpuServerDao extends BaseDao {

    /**
     * 查询所有服务器
     */
    public List<GpuServer> findAll() {
        String sql = "SELECT server_id, server_no, server_name, location, ip_address, status, create_time FROM gpu_server ORDER BY server_id";
        List<Object[]> resultList = executeQuery(sql, null);
        List<GpuServer> servers = new ArrayList<>();

        for (Object[] row : resultList) {
            servers.add(mapRowToGpuServer(row));
        }
        return servers;
    }

    /**
     * 根据ID查询服务器
     */
    public GpuServer findById(Integer serverId) {
        String sql = "SELECT server_id, server_no, server_name, location, ip_address, status, create_time FROM gpu_server WHERE server_id = ?";
        List<Object[]> resultList = executeQuery(sql, new Object[]{serverId});

        if (resultList.isEmpty()) {
            return null;
        }
        return mapRowToGpuServer(resultList.get(0));
    }

    /**
     * 插入服务器
     */
    public int insert(GpuServer server) {
        String sql = "INSERT INTO gpu_server (server_no, server_name, location, ip_address, status) VALUES (?, ?, ?, ?, ?)";
        Object[] params = {
                server.getServerNo(),
                server.getServerName(),
                server.getLocation(),
                server.getIpAddress(),
                server.getStatus()
        };
        return executeUpdate(sql, params);
    }

    /**
     * 更新服务器
     */
    public int update(GpuServer server) {
        String sql = "UPDATE gpu_server SET server_no = ?, server_name = ?, location = ?, ip_address = ?, status = ? WHERE server_id = ?";
        Object[] params = {
                server.getServerNo(),
                server.getServerName(),
                server.getLocation(),
                server.getIpAddress(),
                server.getStatus(),
                server.getServerId()
        };
        return executeUpdate(sql, params);
    }

    /**
     * 删除服务器
     */
    public int delete(Integer serverId) {
        String sql = "DELETE FROM gpu_server WHERE server_id = ?";
        return executeUpdate(sql, new Object[]{serverId});
    }

    /**
     * 检查服务器下是否有显卡
     */
    public boolean hasCards(Integer serverId) {
        String sql = "SELECT COUNT(*) FROM gpu_card WHERE server_id = ?";
        List<Object[]> resultList = executeQuery(sql, new Object[]{serverId});
        if (!resultList.isEmpty()) {
            Number count = (Number) resultList.get(0)[0];
            return count.intValue() > 0;
        }
        return false;
    }

    private GpuServer mapRowToGpuServer(Object[] row) {
        GpuServer server = new GpuServer();
        server.setServerId(((Number) row[0]).intValue());
        server.setServerNo((String) row[1]);
        server.setServerName((String) row[2]);
        server.setLocation((String) row[3]);
        server.setIpAddress((String) row[4]);
        server.setStatus((String) row[5]);

        // 安全处理 createTime
        if (row.length > 6 && row[6] != null) {
            if (row[6] instanceof Timestamp) {
                server.setCreateTime(((Timestamp) row[6]).toLocalDateTime());
            } else if (row[6] instanceof LocalDateTime) {
                server.setCreateTime((LocalDateTime) row[6]);
            }
        }

        // 如果 createTime 仍为 null，设置默认值
        if (server.getCreateTime() == null) {
            server.setCreateTime(LocalDateTime.now());
        }

        return server;
    }
}