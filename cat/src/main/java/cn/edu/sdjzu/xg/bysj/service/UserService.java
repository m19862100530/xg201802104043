package cn.edu.sdjzu.xg.bysj.service;


import cn.edu.sdjzu.xg.bysj.dao.UserDao;
import cn.edu.sdjzu.xg.bysj.domain.Teacher;
import cn.edu.sdjzu.xg.bysj.domain.User;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

public final class UserService {
    private UserDao userDao = UserDao.getInstance();
    private static UserService userService = new UserService();

    public UserService() {
    }

    public static UserService getInstance(){
        return UserService.userService;
    }
    public Collection<User> findAll(Teacher teacher) throws SQLException {
        Collection<User> users = new HashSet<User>();
        for(User user: userDao.findAll()){
            if(user.getTeacher()==teacher){
                users.add(user);
            }
        }
        return users;
    }
    public Collection<User> findAll()throws SQLException{
        return userDao.findAll();
    }

    public Collection<User> getUsers() throws SQLException {
        return userDao.findAll();
    }

    public User find(Integer id) throws SQLException {
        return userDao.find(id);
    }

    public boolean updateUser(User user) throws SQLException {
        userDao.delete(user);
        return userDao.add(user);
    }

    public boolean addUser(User user) throws SQLException {
        return userDao.add(user);
    }

    public boolean deleteUser(Integer id) throws SQLException {
        User user = this.find(id);
        return this.deleteUser(user);
    }

    public boolean deleteUser(User user) throws SQLException {
        return userDao.delete(user);
    }


    public User login(String username, String password) throws SQLException {
        Collection<User> users = this.getUsers();
        User desiredUser = null;
        for(User user:users){
            if(username.equals(user.getUsername()) && password.equals(user.getPassword())){
                desiredUser = user;
            }
        }
        return desiredUser;
    }
    public User findByUsername(String username) throws SQLException {
        return userDao.findByUsername(username);
    }
    public boolean change(User user) throws SQLException{
        return userDao.changePassword(user);
    }

}
