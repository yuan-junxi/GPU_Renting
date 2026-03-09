package main.dao;


import main.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class UserDao extends BaseDao {

    /**
     * 根据用户名查询用户（登录用）
     */
    public User findByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE username = ?";
        List<Object[]> list = executeQuery(sql, new Object[]{username});

        if (list.isEmpty()) {
            return null;
        }

        Object[] row = list.get(0);
        return mapRowToUser(row);
    }

    /**
     * 根据用户名和密码查询（登录验证）
     */
    public User findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
        List<Object[]> list = executeQuery(sql, new Object[]{username, password});

        if (list.isEmpty()) {
            return null;
        }

        return mapRowToUser(list.get(0));
    }

    /**
     * 插入新用户（注册）
     */
    public int insert(User user) {
        String sql = "INSERT INTO Users (username, password, real_name, phone, balance, role, status, create_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] params = {
                user.getUsername(),
                user.getPassword(),
                user.getRealName(),
                user.getPhone(),
                user.getBalance(),
                user.getRole(),
                user.getStatus(),
                LocalDateTime.now()
        };
        return executeUpdate(sql, params);
    }

    /**
     * 检查用户名是否存在（注册时用）
     */
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM Users WHERE username = ?";
        List<Object[]> list = executeQuery(sql, new Object[]{username});

        if (!list.isEmpty()) {
            Long count = (Long) list.get(0)[0];
            return count > 0;
        }
        return false;
    }

    /**
     * 将数据库行转换为User对象
     */
    private User mapRowToUser(Object[] row) {
        User user = new User();
        user.setUserId((Integer) row[0]);
        user.setUsername((String) row[1]);
        user.setPassword((String) row[2]);
        user.setRealName((String) row[3]);
        user.setPhone((String) row[4]);
        user.setBalance((BigDecimal) row[5]);
        user.setRole((String) row[6]);
        user.setStatus((String) row[7]);
        user.setCreateTime((LocalDateTime) row[8]);
        return user;
    }
}