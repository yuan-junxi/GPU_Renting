package main.dao;


import main.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaseDao {

    protected Connection conn;
    protected PreparedStatement ps;
    protected ResultSet rs;

    /**
     * 通用增删改
     */
    protected int executeUpdate(String sql, Object[] params) {
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);

            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
            }

            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    /**
     * 通用查询（返回List<Object[]>）
     */
    protected List<Object[]> executeQuery(String sql, Object[] params) {
        List<Object[]> list = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);

            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
            }

            rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return list;
    }
}