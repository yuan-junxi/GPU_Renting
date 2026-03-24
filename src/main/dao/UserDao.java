package main.dao;

import main.model.User;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户表数据访问层
 * 核心功能：用户相关的数据库操作
 */
public class UserDao extends BaseDao {

    /**
     * 核心功能1：通过用户名查询用户所有字段
     * @param username 用户名
     * @return User对象（包含所有字段），无数据返回null
     */
    public User findByUsername(String username) {
        // SQL查询所有字段（与数据库User表字段一一对应）
        String sql = "SELECT user_id, username, password, phone, balance, role, status, create_time FROM User WHERE username = ?";
        List<Object[]> resultList = executeQuery(sql, new Object[]{username});

        if (resultList.isEmpty()) {
            return null;
        }
        // 解析结果集为User对象
        Object[] row = resultList.get(0);
        return mapRowToUser(row);
    }

    /**
     * 核心功能2：根据用户ID查询用户
     * @param userId 用户ID
     * @return User对象，无数据返回null
     */
    public User findById(Integer userId) {
        String sql = "SELECT user_id, username, password, phone, balance, role, status, create_time FROM User WHERE user_id = ?";
        List<Object[]> resultList = executeQuery(sql, new Object[]{userId});

        if (resultList.isEmpty()) {
            return null;
        }
        Object[] row = resultList.get(0);
        return mapRowToUser(row);
    }

    /**
     * 核心功能3：检查用户名是否存在（注册校验用）
     * @param username 用户名
     * @return 存在返回true，不存在返回false
     */
    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM User WHERE username = ?";
        List<Object[]> resultList = executeQuery(sql, new Object[]{username});

        if (!resultList.isEmpty()) {
            // 兼容MySQL COUNT(*)返回的Long/Integer类型
            Number count = (Number) resultList.get(0)[0];
            return count.intValue() > 0;
        }
        return false;
    }

    /**
     * 核心功能4：插入新用户数据（注册功能）
     * @param user 待插入的用户对象（需包含username/password/phone，其他字段可默认）
     * @return 插入成功返回受影响行数（1），失败返回0或抛出异常
     */
    public int insertUser(User user) {
        // SQL插入语句：create_time由数据库默认生成，balance默认0，role默认1（租户），status默认active
        String sql = "INSERT INTO User (username, password, phone, balance, role, status) VALUES (?, ?, ?, ?, ?, ?)";

        // 构造参数数组（空值兜底，避免插入null导致数据库异常）
        Object[] params = {
                user.getUsername().trim(), // 用户名去空格
                user.getPassword().trim(), // 密码去空格
                // 手机号空值处理：null/空字符串转为""
                (user.getPhone() == null ? "" : user.getPhone().trim()),
                // 余额默认0：null转为BigDecimal.ZERO
                (user.getBalance() == null ? BigDecimal.ZERO : user.getBalance()),
                // 角色默认1（租户）：null转为1
                (user.getRole() == null ? 1 : user.getRole()),
                // 状态默认active
                (user.getStatus() == null ? "active" : user.getStatus())
        };

        // 调用BaseDao的更新方法执行插入
        return executeUpdate(sql, params);
    }

    /**
     * 功能5：查询所有用户
     * @return 所有用户列表
     */
    public List<User> findAll() {
        String sql = "SELECT user_id, username, password, phone, balance, role, status, create_time FROM User ORDER BY user_id";
        List<Object[]> resultList = executeQuery(sql, null);
        List<User> users = new ArrayList<>();

        for (Object[] row : resultList) {
            users.add(mapRowToUser(row));
        }
        return users;
    }

    /**
     * 功能6：更新用户状态
     * @param userId 用户ID
     * @param status 状态（active/disabled）
     * @return 更新成功返回true
     */
    public boolean updateStatus(Integer userId, String status) {
        String sql = "UPDATE User SET status = ? WHERE user_id = ?";
        int result = executeUpdate(sql, new Object[]{status, userId});
        return result > 0;
    }

    /**
     * 功能7：更新用户余额
     * @param userId 用户ID
     * @param balance 新余额
     * @return 更新成功返回true
     */
    public boolean updateBalance(Integer userId, BigDecimal balance) {
        String sql = "UPDATE User SET balance = ? WHERE user_id = ?";
        int result = executeUpdate(sql, new Object[]{balance, userId});
        return result > 0;
    }

    /**
     * 私有工具方法：将数据库行数据转换为User实体（映射所有字段）
     * 字段顺序：user_id, username, password, phone, balance, role, status, create_time
     */
    private User mapRowToUser(Object[] row) {
        User user = new User();
        int index = 0;

        user.setUserId(((Number) row[index++]).intValue());       // user_id
        user.setUsername((String) row[index++]);                  // username
        user.setPassword((String) row[index++]);                  // password
        user.setPhone((String) row[index++]);                     // phone
        user.setBalance((BigDecimal) row[index++]);               // balance
        user.setRole(((Number) row[index++]).intValue());         // role

        // status 字段可能为null
        if (row[index] != null) {
            user.setStatus((String) row[index]);
        }
        index++;

        // create_time 时间类型转换
        if (row[index] instanceof Timestamp) {
            user.setCreateTime(((Timestamp) row[index]).toLocalDateTime());
        } else if (row[index] instanceof LocalDateTime) {
            user.setCreateTime((LocalDateTime) row[index]);
        }

        return user;
    }
}