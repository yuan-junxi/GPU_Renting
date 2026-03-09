package main.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC基础工具类
 * 封装：获取连接、执行查询、执行更新、关闭资源
 */
public class BaseDao {
    // 修正：字符编码改为utf-8（JDK识别），保留mysql的utf8mb4特性
    private static final String DB_URL = "jdbc:mysql://localhost:3306/gpu_renting?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&characterSetResults=utf8mb4";
    private static final String DB_USER = "root";       // 你的MySQL用户名
    private static final String DB_PASSWORD = "123456"; // 你的MySQL密码

    /**
     * 获取数据库连接（修复字符编码问题，增强错误提示）
     */
    protected Connection getConnection() {
        Connection conn = null;
        try {
            // 兼容8.x/5.x驱动
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("成功加载MySQL 8.x驱动");
            } catch (ClassNotFoundException e1) {
                Class.forName("com.mysql.jdbc.Driver");
                System.out.println("成功加载MySQL 5.x驱动");
            }

            // 连接数据库（10秒超时）
            DriverManager.setLoginTimeout(10);
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("数据库连接成功！");

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "加载MySQL驱动失败！\n" +
                            "解决：确保已导入mysql-connector-java-8.0.x.jar并添加到项目库",
                    e
            );
        } catch (SQLException e) {
            String errorMsg = "获取数据库连接失败！\n";
            if (e.getMessage().contains("Access denied")) {
                errorMsg += "原因：用户名/密码错误（当前：user=" + DB_USER + "）\n";
                errorMsg += "解决：修改DB_USER/DB_PASSWORD为实际MySQL账号密码";
            } else if (e.getMessage().contains("Unknown database")) {
                errorMsg += "原因：数据库gpu_renting不存在\n";
                errorMsg += "解决：执行CREATE DATABASE gpu_renting DEFAULT CHARSET utf8mb4;";
            } else if (e.getMessage().contains("Unsupported character encoding")) {
                errorMsg += "原因：字符编码配置错误\n";
                errorMsg += "解决：确保URL中characterEncoding=utf-8（不是utf8mb4）";
            } else {
                errorMsg += "原因：" + e.getMessage();
            }
            throw new RuntimeException(errorMsg, e);
        }
        return conn;
    }

    /**
     * 执行查询SQL（返回多行结果）
     */
    protected List<Object[]> executeQuery(String sql, Object[] params) {
        List<Object[]> resultList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            // 设置参数（空值安全处理）
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }

            // 执行查询
            rs = pstmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // 解析结果集
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                resultList.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("执行查询SQL失败：" + sql + "\n错误信息：" + e.getMessage(), e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return resultList;
    }

    /**
     * 执行更新SQL（INSERT/UPDATE/DELETE）
     */
    protected int executeUpdate(String sql, Object[] params) {
        int affectedRows = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            // 设置参数（空值安全处理）
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }

            // 执行更新
            affectedRows = pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("执行更新SQL失败：" + sql + "\n错误信息：" + e.getMessage(), e);
        } finally {
            closeResources(conn, pstmt, null);
        }
        return affectedRows;
    }

    /**
     * 关闭JDBC资源（安全关闭，避免空指针）
     */
    protected void closeResources(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            System.err.println("关闭ResultSet失败：" + e.getMessage());
        }
        try {
            if (pstmt != null) pstmt.close();
        } catch (SQLException e) {
            System.err.println("关闭PreparedStatement失败：" + e.getMessage());
        }
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("关闭Connection失败：" + e.getMessage());
        }
    }

    // 测试连接（可选）
    public static void main(String[] args) {
        BaseDao baseDao = new BaseDao();
        try {
            Connection conn = baseDao.getConnection();
            System.out.println("连接测试成功！");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}