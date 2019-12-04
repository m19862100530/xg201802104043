package cn.edu.sdjzu.xg.bysj.dao;


import cn.edu.sdjzu.xg.bysj.domain.School;
import util.JdbcHelper;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

public final class SchoolDao {
	private static SchoolDao schoolDao = new SchoolDao();
	public static SchoolDao getInstance(){
		return schoolDao;
	}
	private static Collection<School> schools;
	static{
		schools = new TreeSet<School>();
		School school = new School("土木工程","01","");
		schools.add(school);
		schools.add(new School("管理工程","02","最好的学院"));
		schools.add(new School("市政工程","03",""));
		schools.add(new School("艺术","04",""));
	}
	
	public SchoolDao(){}
	public Collection<School> findAll() throws SQLException {
		schools = new HashSet<>();
		//获取数据库连接对象
		Connection connection = JdbcHelper.getConn();
		//执行sql查询语句并获得结果集对象
		Statement stmt = connection.createStatement();
		ResultSet resultSet = stmt.executeQuery("select * from school");
		while (resultSet.next()){
			schools.add(new School(resultSet.getInt("id"),resultSet.getString("no"),
					resultSet.getString("description"),resultSet.getString("remarks")));
		}
		JdbcHelper.close(stmt,connection);
		return SchoolDao.schools;
	}

	public School find(Integer id) throws SQLException {
		School desiredSchool = null;
		Connection connection = JdbcHelper.getConn();
		//根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
		PreparedStatement preparedStatement = connection.prepareStatement(
				"SELECT * FROM school WHERE id = ?");
		//为预编译参数赋值
		preparedStatement.setInt(1,id);
		ResultSet resultSet = preparedStatement.executeQuery();
		//由于id不能取重复值，故结果集中最多有一条记录
		//若结果集有一条记录，则以当前记录中的id,description,no,remarks值为参数，创建School对象
		//若结果集中没有记录，则本方法返回null
		if (resultSet.next()){
			desiredSchool = new School(resultSet.getInt("id"),resultSet.getString("description"),resultSet.getString("no"),resultSet.getString("remarks"));
		}
		//关闭资源
		JdbcHelper.close(resultSet,preparedStatement,connection);
		return desiredSchool;
	}

	public boolean update(School school) throws SQLException {
		//获取数据库连接对象
		Connection connection = JdbcHelper.getConn();
		//根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
		PreparedStatement preparedStatement = connection.prepareStatement(
				"UPDATE school SET description = ? ,no = ?,remarks = ? WHERE id = ?");
		//为预编译语句赋值
		preparedStatement.setString(1,school.getDescription());
		preparedStatement.setString(2,school.getNo());
		preparedStatement.setString(3,school.getRemarks());
		preparedStatement.setInt(4,school.getId());
		int affectedRowNum = preparedStatement.executeUpdate();
		//执行预编译语句，用其返回值、影响的行为数为赋值affectedRowNum
		JdbcHelper.close(preparedStatement,connection);
		return affectedRowNum>0;
	}

	public boolean add(School school) throws SQLException {
		//获取数据库连接对象
		Connection connection = JdbcHelper.getConn();
		//根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
		PreparedStatement preparedStatement = connection.prepareStatement(
				"INSERT INTO school" + "(no,description,remarks)"+" VALUES (?,?,?)");
		preparedStatement.setString(1,school.getNo());
		preparedStatement.setString(2,school.getDescription());
		preparedStatement.setString(3,school.getRemarks());
		int affectedRowNum = preparedStatement.executeUpdate();
		//执行预编译语句，用其返回值、影响的行为数为赋值affectedRowNum
		JdbcHelper.close(preparedStatement,connection);
		return affectedRowNum>0;
	}

	public boolean delete(Integer id) throws SQLException{
		//获取数据库连接对象
		Connection connection = JdbcHelper.getConn();
		//创建sql语句
		String deleteSchool_sql = "DELETE FROM school WHERE id =?";
		//根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
		PreparedStatement preparedStatement = connection.prepareStatement(deleteSchool_sql);
		preparedStatement.setInt(1, id);
		//执行预编译对象的executeUpdate方法，获取删除的记录行数
		int affectedRowNum = preparedStatement.executeUpdate();
		JdbcHelper.close(preparedStatement,connection);
		return affectedRowNum>0;
	}

	public boolean delete(School school) throws SQLException {
		return delete(school.getId());
	}

	public static void main(String[] args) throws SQLException{
		//找到id为2的school1对象
		School school1 = SchoolDao.getInstance().find(1001);
		System.out.println(school1);
		school1.setDescription("土木");
		SchoolDao.getInstance().update(school1);
		//找到id为2的school2对象
		School school2 = SchoolDao.getInstance().find(1001);
		System.out.println(school2.getDescription());
		System.out.println(school2);
		School school = new School("土木工程","07","");
		System.out.println(SchoolDao.getInstance().add(school));
		//System.out.println(school2);
	}
}
