package main.service;


import main.dao.UserDao;
import main.gui.Login.LoginFrame;
import main.model.User;

import javax.swing.*;

public class UserService {

    private UserDao userDao = new UserDao();

    /**
     * 登录
     * @return User对象（登录成功）, null（失败）
     */
    public int login(String username, String password) {
        /*
        * 1.空值
        * 2.用户名不存在
        * 3.密码错误
        * 4.登录成功
        * */

        // 1.空值验证
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            return 1;
        }

        //TODO 查用户名是否存在

        // 调用DAO查询
        User user = userDao.findByUsernameAndPassword(username, password);

        // 2.用户名不存在
        if (user == null) {
            return 2;
        }

        // 3.密码错误
        if (user.getPassword() != password) {
            return 3;
        }

        return 4;
    }

    /**
     * 注册
     * @return 1成功，0失败，-1用户名已存在，-2参数无效
     */
    public int register(String username, String password, String confirmPassword) {

        // 1.空值校验
        if(username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return 1;
        }

        // 2.验证两次密码
        if (!password.equals(confirmPassword)) {
            return 2;
        }

        //TODO 查用户名是否存在

        // 3. 用户名唯一性校验
        if (userDao.existsByUsername(username)) {
            return 3;
        }

        // 创建用户对象
        User user = new User(username, password);

        // 插入数据库
        userDao.insert(user);

        return 4;
    }
}