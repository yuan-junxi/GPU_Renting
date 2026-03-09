package main.dao;

import main.model.User;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户表数据访问层（精简版）
 * 核心功能：1. 检查用户名是否存在  2. 根据用户名查询用户所有字段
 */
public class UserDao extends BaseDao {

    /**
     * 核心功能1：通过用户名查询用户所有字段
     * @param username 用户名
     * @return User对象（包含所有字段），无数据返回null
     */
    public User findByUsername(String username) {
        // SQL查询所有字段（与数据库User表字段一一对应）
        String sql = "SELECT user_id, username, password, phone, balance, role, create_time FROM User WHERE username = ?";
        List<Object[]> resultList = executeQuery(sql, new Object[]{username});

        if (resultList.isEmpty()) {
            return null;
        }
        // 解析结果集为User对象
        Object[] row = resultList.get(0);
        return mapRowToUser(row);
    }

    /**
     * 核心功能2：检查用户名是否存在（注册校验用）
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
     * 私有工具方法：将数据库行数据转换为User实体（映射所有字段）
     */
    private User mapRowToUser(Object[] row) {
        User user = new User();
        // 字段顺序严格匹配SQL查询顺序：user_id, username, password, phone, balance, role, create_time
        user.setUserId(((Number) row[0]).intValue());       // 主键ID
        user.setUsername((String) row[1]);                  // 用户名
        user.setPassword((String) row[2]);                  // 密码
        user.setPhone((String) row[3]);                     // 手机号
        user.setBalance((BigDecimal) row[4]);               // 账户余额
        user.setRole(((Number) row[5]).intValue());         // 角色（0=管理员，1=租户）
        // 时间类型转换（DATETIME → LocalDateTime）
        if (row[6] instanceof Timestamp) {
            user.setCreateTime(((Timestamp) row[6]).toLocalDateTime());
        } else if (row[6] instanceof LocalDateTime) {
            user.setCreateTime((LocalDateTime) row[6]);
        }
        return user;
    }

    /**
     * 核心功能3：插入新用户数据（注册功能）
     * @param user 待插入的用户对象（需包含username/password/phone，其他字段可默认）
     * @return 插入成功返回受影响行数（1），失败返回0或抛出异常
     */
    public int insertUser(User user) {
        // SQL插入语句：create_time由数据库默认生成，balance默认0，role默认1（租户）
        String sql = "INSERT INTO User (username, password, phone, balance, role) VALUES (?, ?, ?, ?, ?)";

        // 构造参数数组（空值兜底，避免插入null导致数据库异常）
        Object[] params = {
                user.getUsername().trim(), // 用户名去空格
                user.getPassword().trim(), // 密码去空格
                // 手机号空值处理：null/空字符串转为""
                (user.getPhone() == null ? "" : user.getPhone().trim()),
                // 余额默认0：null转为BigDecimal.ZERO
                (user.getBalance() == null ? BigDecimal.ZERO : user.getBalance()),
                // 角色默认1（租户）：null转为1
                (user.getRole() == null ? 1 : user.getRole())
        };

        // 调用BaseDao的更新方法执行插入
        return executeUpdate(sql, params);
    }
}