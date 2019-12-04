package cn.edu.sdjzu.xg.bysj.dao;

import cn.edu.sdjzu.xg.bysj.domain.Teacher;
import cn.edu.sdjzu.xg.bysj.domain.User;
import util.JdbcHelper;

import java.sql.*;
import java.util.Collection;
import java.util.Date;
import java.util.TreeSet;


public final class UserDao {
	private static UserDao userDao=new UserDao();
	private UserDao(){}
	public static UserDao getInstance(){
		return userDao;
	}
	private static Collection<User> users;
	static{
		TeacherDao teacherDao = TeacherDao.getInstance();
		users = new TreeSet<User>();
		User user = null;
		try {
			user = new User(1,"st","st",new Date(),teacherDao.find(1));
			users.add(user);
			users.add(new User(2,"lx","lx",new Date(),teacherDao.find(2)));
			users.add(new User(3,"wx","wx",new Date(),teacherDao.find(3)));
			users.add(new User(4,"lf","lf",new Date(),teacherDao.find(4)));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public Collection<User> findAll() throws SQLException {
		users = new TreeSet<>();
		//获取数据库连接对象
		Connection connection = JdbcHelper.getConn();
		//执行sql查询语句并获得结果集对象
		Statement stmt = connection.createStatement();
		//查询语句
		ResultSet resultSet = stmt.executeQuery("select * from user");
		while (resultSet.next()){
			//找到外键的id对应的对象
			Teacher teacher =TeacherDao.getInstance().find(resultSet.getInt("teacher_id"));
			users.add(new User(resultSet.getInt("id"),resultSet.getString("username"),resultSet.getString("password"),resultSet.getDate("logintime"),teacher));
		}
		JdbcHelper.close(stmt,connection);
		return UserDao.users;
	}


	public boolean update(User user) throws SQLException {
		//获取数据库连接对象
		Connection connection = JdbcHelper.getConn();
		//根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
		PreparedStatement preparedStatement = connection.prepareStatement(
				"UPDATE user SET password = ? WHERE id = ?");
		preparedStatement.setString(1,user.getPassword());
		preparedStatement.setInt(2,user.getId());
		int affectedRowNum = preparedStatement.executeUpdate();
		//执行预编译语句，用其返回值、影响的行为数为赋值affectedRowNum
		JdbcHelper.close(preparedStatement,connection);
		return affectedRowNum>0;
	}

	public boolean add(User user) throws SQLException {
		//获取数据库连接对象
		Connection connection = JdbcHelper.getConn();
		//根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
		PreparedStatement preparedStatement = connection.prepareStatement(
				"INSERT INTO user (username,password,logintime,teacher_id)"+" VALUES (?,?,?,?)");
		preparedStatement.setString(1,user.getUsername());
		preparedStatement.setString(2,user.getPassword());
		preparedStatement.setDate(3, (java.sql.Date) user.getLoginTime());
		preparedStatement.setInt(4,user.getTeacher().getId());
		int affectedRowNum = preparedStatement.executeUpdate();
		//执行预编译语句，用其返回值、影响的行为数为赋值affectedRowNum
		JdbcHelper.close(preparedStatement,connection);
		return affectedRowNum>0;
	}

	public boolean delete(Integer id) throws SQLException {
		//获取数据库连接对象
		Connection connection = JdbcHelper.getConn();
		//创建sql语句
		String deleteUser_sql = "DELETE FROM user WHERE id =?";
		//根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
		PreparedStatement preparedStatement = connection.prepareStatement(deleteUser_sql);
		preparedStatement.setInt(1, id);
		//执行预编译对象的executeUpdate方法，获取删除的记录行数
		int affectedRowNum = preparedStatement.executeUpdate();
		JdbcHelper.close(preparedStatement,connection);
		return affectedRowNum>0;
	}

	public boolean delete(User user) throws SQLException {
		return delete(user.getId());
	}
	
	
	public static void main(String[] args) throws SQLException {
		UserDao dao = new UserDao();
		Collection<User> users = dao.findAll();
		display(users);
	}

	private static void display(Collection<User> users) {
		for (User user : users) {
			System.out.println(user);
		}
	}
	public boolean changePassword(User user) throws SQLException {
		//获取数据库连接对象
		Connection connection = JdbcHelper.getConn();
		//根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
		PreparedStatement preparedStatement = connection.prepareStatement(
				"UPDATE USER SET password = ? WHERE id = ?");
		preparedStatement.setString(1,user.getPassword());
		preparedStatement.setInt(2,user.getId());
		int affectedRowNum = preparedStatement.executeUpdate();
		//执行预编译语句，用其返回值、影响的行为数为赋值affectedRowNum
		JdbcHelper.close(preparedStatement,connection);
		return affectedRowNum>0;
	}
	public User find(Integer id) throws SQLException{
		User desiredUser = null;
		Connection connection = JdbcHelper.getConn();
		//根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
		PreparedStatement preparedStatement = connection.prepareStatement(
				"SELECT * FROM user WHERE id = ?");
		//为预编译参数赋值
		preparedStatement.setInt(1,id);
		ResultSet resultSet = preparedStatement.executeQuery();
		//由于id不能取重复值，故结果集中最多有一条记录
		//若结果集有一条记录，则以当前记录中的id,description,no,remarks值为参数，创建Degree对象
		//若结果集中没有记录，则本方法返回null
		if (resultSet.next()){
			Teacher teacher = TeacherDao.getInstance().find(resultSet.getInt("teacher_id"));
			desiredUser = new User(resultSet.getInt("id"),resultSet.getString("username"),resultSet.getString("password"),resultSet.getDate("logintime"),teacher);
		}
		//关闭资源
		JdbcHelper.close(resultSet,preparedStatement,connection);
		return desiredUser;
	}
	public User findByUsername(String username) throws SQLException{
		User users = null;
		Connection connection = JdbcHelper.getConn();
		//根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
		PreparedStatement preparedStatement = connection.prepareStatement(
				"SELECT * FROM user WHERE username = ?");
		//为预编译参数赋值
		preparedStatement.setString(1,username);
		ResultSet resultSet = preparedStatement.executeQuery();
		//由于id不能取重复值，故结果集中最多有一条记录
		//若结果集有一条记录，则以当前记录中的id,description,no,remarks值为参数，创建Degree对象
		//若结果集中没有记录，则本方法返回null
		if (resultSet.next()){
			Teacher teacher = TeacherDao.getInstance().find(resultSet.getInt("teacher_id"));
			users=new User(resultSet.getInt("id"),resultSet.getString("username"),resultSet.getString("password"),resultSet.getDate("logintime"),teacher);
		}
		//关闭资源
		JdbcHelper.close(resultSet,preparedStatement,connection);
		return users;
	}
}
