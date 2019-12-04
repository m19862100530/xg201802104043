package cn.edu.sdjzu.xg.bysj.dao;

import cn.edu.sdjzu.xg.bysj.domain.Department;
import cn.edu.sdjzu.xg.bysj.domain.School;
import util.JdbcHelper;

import java.sql.*;
import java.util.Collection;
import java.util.TreeSet;

public final class DepartmentDao {
    private static Collection<Department> departments;
    static {
        School managementSchool = null;

        try {
            managementSchool = SchoolDao.getInstance().find(2);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        departments = new TreeSet<Department>();
        Department department = new Department(1, "工程管理", "0201", "",
                managementSchool);
        departments.add(department);
        departments
                .add(new Department(2, "信息管理", "0202", "", managementSchool));
        departments
                .add(new Department(3, "工程造价", "0203", "", managementSchool));
        departments
                .add(new Department(4, "工业工程", "0204", "", managementSchool));
    }
    private static DepartmentDao departmentDao=new DepartmentDao();
    private DepartmentDao(){}
    public static DepartmentDao getInstance(){
        return departmentDao;
    }


    public Collection<Department> findAll() throws SQLException {
        departments = new TreeSet<>();
        //获取数据库连接对象
        Connection connection = JdbcHelper.getConn();
        //执行sql查询语句并获得结果集对象
        Statement stmt = connection.createStatement();
        ResultSet resultSet = stmt.executeQuery("select * from department");
        while (resultSet.next()){
            //找到外键的id对应的对象
            School school = SchoolDao.getInstance().find(resultSet.getInt("school_id"));
            departments.add(new Department(resultSet.getInt("id"),resultSet.getString("no"),
                    resultSet.getString("description"),resultSet.getString("remarks"),school));
        }
        JdbcHelper.close(stmt,connection);
        return DepartmentDao.departments;
    }

    public Department find(Integer id) throws SQLException {
        Department desiredDepartment = null;
        Connection connection = JdbcHelper.getConn();
        //根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM department WHERE id = ?");
        //为预编译参数赋值
        preparedStatement.setInt(1,id);
        ResultSet resultSet = preparedStatement.executeQuery();
        //由于id不能取重复值，故结果集中最多有一条记录
        //若结果集有一条记录，则以当前记录中的id,description,no,remarks值为参数，创建Degree对象
        //若结果集中没有记录，则本方法返回null
        if (resultSet.next()){
            School school = SchoolDao.getInstance().find(resultSet.getInt("school_id"));
            desiredDepartment = new Department(resultSet.getInt("id"),resultSet.getString("description"),resultSet.getString("no"),resultSet.getString("remarks"),school);
        }
        //关闭资源
        JdbcHelper.close(resultSet,preparedStatement,connection);
        return desiredDepartment;
    }

    public boolean update(Department department) throws SQLException {
        //获取数据库连接对象
        Connection connection = JdbcHelper.getConn();
        //根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
        PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE department SET description = ?,no = ?,remarks = ? WHERE id = ?");
        preparedStatement.setString(1,department.getDescription());
        preparedStatement.setString(2,department.getNo());
        preparedStatement.setString(3,department.getRemarks());
        preparedStatement.setInt(4,department.getId());
        int affectedRowNum = preparedStatement.executeUpdate();
        //执行预编译语句，用其返回值、影响的行为数为赋值affectedRowNum
        JdbcHelper.close(preparedStatement,connection);
        return affectedRowNum>0;
    }

    public boolean add(Department department) throws SQLException {
        //获取数据库连接对象
        Connection connection = JdbcHelper.getConn();
        //根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO department" + "(no,description,remarks,school_id)"+" VALUES (?,?,?,?)");
        preparedStatement.setString(1,department.getNo());
        preparedStatement.setString(2,department.getDescription());
        preparedStatement.setString(3,department.getRemarks());
        preparedStatement.setInt(4,department.getSchool().getId());
        int affectedRowNum = preparedStatement.executeUpdate();
        //执行预编译语句，用其返回值、影响的行为数为赋值affectedRowNum
        JdbcHelper.close(preparedStatement,connection);
        return affectedRowNum>0;
    }

    public boolean delete(Integer id) throws SQLException {
        //获取数据库连接对象
        Connection connection = JdbcHelper.getConn();
        //创建sql语句
        String deleteDepartment_sql = "DELETE FROM department WHERE id =?";
        //根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
        PreparedStatement preparedStatement = connection.prepareStatement(deleteDepartment_sql);
        preparedStatement.setInt(1, id);
        //执行预编译对象的executeUpdate方法，获取删除的记录行数
        int affectedRowNum = preparedStatement.executeUpdate();
        JdbcHelper.close(preparedStatement,connection);
        return affectedRowNum>0;
    }

    public boolean delete(Department department) throws SQLException {
        return delete(department.getId());
    }
    public Collection<Department> findAllBySchool(Integer schoolId) throws SQLException {
        departments = new TreeSet<>();
        Connection connection = JdbcHelper.getConn();
        //根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM department WHERE school_id = ?");
        //为预编译参数赋值
        preparedStatement.setInt(1,schoolId);
        ResultSet resultSet = preparedStatement.executeQuery();
        //由于id不能取重复值，故结果集中最多有一条记录
        //若结果集有一条记录，则以当前记录中的id,description,no,remarks值为参数，创建Degree对象
        //若结果集中没有记录，则本方法返回null
        while (resultSet.next()){
            School school = SchoolDao.getInstance().find(resultSet.getInt("school_id"));
            departments.add(new Department(resultSet.getInt("id"),resultSet.getString("no"),resultSet.getString("description"),resultSet.getString("remarks"),school));
        }
        //关闭资源
        JdbcHelper.close(resultSet,preparedStatement,connection);
        return departments;
    }


    public static void main(String[] args) throws SQLException {
        //找到id为2的department1对象
        Department department1 = DepartmentDao.getInstance().find(2);
        System.out.println(department1);
        department1.setDescription("工程管理");
        //更新数据
        DepartmentDao.getInstance().update(department1);
        //找到id为2的department2对象，即为department1更新后的数据
        Department department2 = DepartmentDao.getInstance().find(2);
        System.out.println( department2.getDescription());
        System.out.println(department2);
        Department department = new Department("工程管理","06","", SchoolDao.getInstance().find(5));
        System.out.println(DepartmentDao.getInstance().add(department));
    }
}