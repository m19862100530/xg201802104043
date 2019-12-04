package cn.edu.sdjzu.xg.bysj.dao;


import cn.edu.sdjzu.xg.bysj.domain.Degree;
import cn.edu.sdjzu.xg.bysj.domain.Department;
import cn.edu.sdjzu.xg.bysj.domain.ProfTitle;
import cn.edu.sdjzu.xg.bysj.domain.Teacher;
import util.JdbcHelper;

import java.sql.*;
import java.util.Collection;
import java.util.Date;
import java.util.TreeSet;

public final class TeacherDao {
    private static TeacherDao teacherDao=new TeacherDao();
    private TeacherDao(){}
    public static TeacherDao getInstance(){
        return teacherDao;
    }
    private static Collection<Teacher> teachers;



    public Collection<Teacher> findAll() throws SQLException {
        teachers = new TreeSet<>();
        //获取数据库连接对象
        Connection connection = JdbcHelper.getConn();
        //执行sql查询语句并获得结果集对象
        Statement stmt = connection.createStatement();
        //查询语句
        ResultSet resultSet = stmt.executeQuery("select * from teacher");
        while (resultSet.next()){
            //找到外键的id对应的对象
            ProfTitle profTitle = ProfTitleDao.getInstance().find(resultSet.getInt("profTitle_id"));
            Degree degree = DegreeDao.getInstance().find(resultSet.getInt("degree_id"));
            Department department = DepartmentDao.getInstance().find(resultSet.getInt("department_id"));
            teachers.add(new Teacher(resultSet.getInt("id"),resultSet.getString("no"),resultSet.getString("name"),profTitle,degree,department));
        }
        JdbcHelper.close(stmt,connection);
        return TeacherDao.teachers;
    }

    public Teacher find(Integer id) throws SQLException {
        Teacher desiredTeacher = null;
        Connection connection = JdbcHelper.getConn();
        //根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM teacher WHERE id = ?");
        //为预编译参数赋值
        preparedStatement.setInt(1,id);
        ResultSet resultSet = preparedStatement.executeQuery();
        //由于id不能取重复值，故结果集中最多有一条记录
        //若结果集有一条记录，则以当前记录中的id,description,no,remarks值为参数，创建Degree对象
        //若结果集中没有记录，则本方法返回null
        if (resultSet.next()){
            ProfTitle profTitle = ProfTitleDao.getInstance().find(resultSet.getInt("profTitle_id"));
            Degree degree = DegreeDao.getInstance().find(resultSet.getInt("degree_id"));
            Department department = DepartmentDao.getInstance().find(resultSet.getInt("department_id"));
            desiredTeacher = new Teacher(resultSet.getInt("id"),resultSet.getString("no"),resultSet.getString("name"), profTitle,degree,department);
        }
        //关闭资源
        JdbcHelper.close(resultSet,preparedStatement,connection);
        return desiredTeacher;
    }

    public boolean update(Teacher teacher) throws SQLException {
        //获取数据库连接对象
        Connection connection = JdbcHelper.getConn();
        //根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
        PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE teacher SET name = ? WHERE id = ?");
        preparedStatement.setString(1,teacher.getName());
        preparedStatement.setInt(2,teacher.getId());
        int affectedRowNum = preparedStatement.executeUpdate();
        //执行预编译语句，用其返回值、影响的行为数为赋值affectedRowNum
        JdbcHelper.close(preparedStatement,connection);
        return affectedRowNum>0;
    }

    public boolean add(Teacher teacher) throws SQLException {
        Date date = new Date();
        int teacher_id = 0;
        int affectedRowNum = 0;
        //获取数据库连接对象
        Connection connection =null;
        PreparedStatement preparedStatement = null;
        try {
            connection = JdbcHelper.getConn();
            connection.setAutoCommit(false);
            //根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO teacher (no,name,profTitle_id,degree_id,department_id) VALUES (?,?,?,?,?)");
            preparedStatement.setString(1, teacher.getNo());
            preparedStatement.setString(2, teacher.getName());
            preparedStatement.setInt(3, teacher.getTitle().getId());
            preparedStatement.setInt(4, teacher.getDegree().getId());
            preparedStatement.setInt(5, teacher.getDepartment().getId());
            preparedStatement.execute();
            String findTeacher_sql = "select * FROM teacher WHERE name=? and no=? ";
            //在该连接上创建预编译语句对象
            preparedStatement = connection.prepareStatement(findTeacher_sql);
            //为预编译参数赋值
            preparedStatement.setString(1,teacher.getName());
            preparedStatement.setString(2,teacher.getNo());
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                 teacher_id = resultSet.getInt("id");
            }
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO user(username,password,logintime,teacher_id) VALUES (?,?,?,?)");
            preparedStatement.setString(1, teacher.getNo());
            preparedStatement.setString(2, teacher.getNo());
            preparedStatement.setDate(3,new java.sql.Date(date.getTime()));
            preparedStatement.setInt(4, teacher_id);
            preparedStatement.execute();
            connection.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage() + "\n errorCode = " + e.getErrorCode());
        }
        try {
            if (connection != null) {
                connection.rollback();
            }
        }catch (SQLException e1){
            e1.printStackTrace();
        }finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
            JdbcHelper.close(preparedStatement,connection);
            return affectedRowNum>0;
        }
    }


    public boolean delete(Integer id) throws SQLException {
        //获取数据库连接对象
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        int affectedRows =0;
        try {
            connection = JdbcHelper.getConn();
            connection.setAutoCommit(false);
            //根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
            preparedStatement = connection.prepareStatement(
                    "DELETE from user where id = ?");
            preparedStatement.setInt(1, id);
            affectedRows = preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement(
                    "DELETE FROM teacher WHERE id = ? ");
            preparedStatement.setInt(1, id);
            affectedRows = preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage() + "\n errorCode = " + e.getErrorCode());
        }
        try {
            if (connection != null) {
                connection.rollback();
            }
        }catch (SQLException e1){
            e1.printStackTrace();
        }finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
            //关闭preparedStatement对象
            JdbcHelper.close(preparedStatement,connection);
            return affectedRows>0;
        }
    }

    public boolean delete(Teacher teacher) throws SQLException {
        return delete(teacher.getId());
    }

    public static void main(String[] args) throws SQLException {
        Degree degree = DegreeDao.getInstance().find(1);
        Department department = DepartmentDao.getInstance().find(1);
        ProfTitle profTitle = ProfTitleDao.getInstance().find(1);
        Teacher teacher = new Teacher("07","王建国",profTitle,degree,department);
        System.out.println(teacherDao.add(teacher));

    }
}