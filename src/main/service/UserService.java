package main.service;


import main.dao.UserDao;
import main.gui.Login.LoginFrame;
import main.model.User;

import javax.swing.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UserService {


    private UserDao userDao = new UserDao();

    /**
     * 登录
     * @return User对象（登录成功）, null（失败）
     */
    public int login(String username, String password) {
        // 1. 空值校验
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            return 1;
        }

        // 2. 查询用户
        User user = userDao.findByUsername(username);

        // 3. 用户名不存在
        if (user == null) {
            return 2;
        }

        // 4. 密码错误（这里假设密码明文存储，实际应用应加密比对）
        if (!password.equals(user.getPassword())) {
            return 3;
        }

        // 5. 检查账户状态
        if ("disabled".equals(user.getStatus())) {
            return 6;  // 账户已被禁用
        }

        // 6. 根据角色返回不同值
        if (user.getRole() == 0) {
            return 4;  // 管理员登录成功
        } else {
            return 5;  // 普通用户登录成功
        }
    }

    /**
     * 注册
     * @return 1成功，0失败，-1用户名已存在，-2参数无效
     */
    public int register(String username, String password, String confirmPassword, String phone) {

        /*
         * 1.空值
         * 2.验证两次密码是否一致
         * 3.用户名是否唯一
         * 4.注册成功
         * */

        // 1.空值校验
        if(username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return 1;
        }

        // 2.验证两次密码
        if (!password.equals(confirmPassword)) {
            return 2;
        }

        // 3. 用户名唯一性校验
        if (userDao.isUsernameExists(username)) {
            return 3;
        }

        // 创建用户对象
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(1);
        user.setPhone(phone);
        user.setCreateTime(LocalDateTime.now());
        user.setBalance(BigDecimal.ZERO);


        // 插入数据库
        userDao.insertUser(user);

        return 4;
    }

    public User getUserByUsername(String username) {
        return userDao.findByUsername(username);
    }
}