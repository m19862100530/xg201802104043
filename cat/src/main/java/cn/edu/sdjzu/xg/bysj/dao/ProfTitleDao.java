package cn.edu.sdjzu.xg.bysj.dao;


import cn.edu.sdjzu.xg.bysj.domain.ProfTitle;
import util.JdbcHelper;

import java.sql.*;
import java.util.Collection;
import java.util.TreeSet;

public final class ProfTitleDao {
	private static ProfTitleDao profTitleDao=new ProfTitleDao();
	private ProfTitleDao(){}
	public static ProfTitleDao getInstance(){
		return profTitleDao;
	}
	private static Collection<ProfTitle> profTitles;
	public Collection<ProfTitle> findAll() throws SQLException {
		profTitles = new TreeSet<>();
		//获取数据库连接对象
		Connection connection = JdbcHelper.getConn();
		//执行sql查询语句并获得结果集对象
		Statement stmt = connection.createStatement();
		ResultSet resultSet = stmt.executeQuery("select * from proftitle");
		while (resultSet.next()){
			profTitles.add(new ProfTitle(resultSet.getInt("id"),resultSet.getString("no"),
					resultSet.getString("description"),resultSet.getString("remarks")));
		}
		JdbcHelper.close(stmt,connection);
		return ProfTitleDao.profTitles;
	}

	public ProfTitle find(Integer id) throws SQLException {
		ProfTitle desiredProfTitle = null;
		Connection connection = JdbcHelper.getConn();
		//根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
		PreparedStatement preparedStatement = connection.prepareStatement(
				"SELECT * FROM proftitle WHERE id = ?");
		//为预编译参数赋值
		preparedStatement.setInt(1,id);
		ResultSet resultSet = preparedStatement.executeQuery();
		//由于id不能取重复值，故结果集中最多有一条记录
		//若结果集有一条记录，则以当前记录中的id,description,no,remarks值为参数，创建Degree对象
		//若结果集中没有记录，则本方法返回null
		if (resultSet.next()){
			desiredProfTitle = new ProfTitle(resultSet.getInt("id"),resultSet.getString("description"),resultSet.getString("no"),resultSet.getString("remarks"));
		}
		//关闭资源
		JdbcHelper.close(resultSet,preparedStatement,connection);
		return desiredProfTitle;
	}

	public boolean update(ProfTitle profTitle) throws SQLException {
		//profTitles.remove(profTitle);
		//获取数据库连接对象
		Connection connection = JdbcHelper.getConn();
		//根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
		PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE profTitle SET description = ?,no = ?,remarks = ? WHERE id = ?");
		preparedStatement.setString(1,profTitle.getDescription());
		preparedStatement.setString(2,profTitle.getNo());
		preparedStatement.setString(3,profTitle.getRemarks());
		preparedStatement.setInt(4,profTitle.getId());
		int affectedRowNum = preparedStatement.executeUpdate();
		//执行预编译语句，用其返回值、影响的行为数为赋值affectedRowNum
		JdbcHelper.close(preparedStatement,connection);
		return affectedRowNum>0;
	}

	public boolean add(ProfTitle profTitle) throws SQLException {
		//获取数据库连接对象
		Connection connection = JdbcHelper.getConn();
		//根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
		PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO profTitle" + "(no,description,remarks)"+" VALUES (?,?,?)");
		preparedStatement.setString(1,profTitle.getNo());
		preparedStatement.setString(2,profTitle.getDescription());
		preparedStatement.setString(3,profTitle.getRemarks());
		int affectedRowNum = preparedStatement.executeUpdate();
		//执行预编译语句，用其返回值、影响的行为数为赋值affectedRowNum
		JdbcHelper.close(preparedStatement,connection);
		return affectedRowNum>0;
	}

	public boolean delete(Integer id) throws SQLException {
		//获取数据库连接对象
		Connection connection = JdbcHelper.getConn();
		//创建sql语句
		String deleteProfTitle_sql = "DELETE FROM proftitle WHERE id =?";
		//根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
		PreparedStatement preparedStatement = connection.prepareStatement(deleteProfTitle_sql);
		preparedStatement.setInt(1, id);
		//执行预编译对象的executeUpdate方法，获取删除的记录行数
		//int affectedRowNum = preparedStatement.executeUpdate();
		//执行预编译对象的executeUpdate方法，获取删除的记录行数
		int affectedRowNum = preparedStatement.executeUpdate();
		JdbcHelper.close(preparedStatement,connection);
		return affectedRowNum>0;
	}

	public boolean delete(ProfTitle profTitle) throws SQLException {
		return delete(profTitle.getId());
	}

	public static void main(String[] args) throws SQLException {
		//找到id为2的profTitle1对象
		ProfTitle profTitle1 = ProfTitleDao.getInstance().find(1);
		System.out.println(profTitle1);
		profTitle1.setDescription("管理工程");
		//更新数据
		ProfTitleDao.getInstance().update(profTitle1);
		//找到id为2的profTitle2对象，即为更新后的数据
		ProfTitle profTitle2 = ProfTitleDao.getInstance().find(1);
		System.out.println(profTitle2.getDescription());
		System.out.println(profTitle2);
		ProfTitle profTitle = new ProfTitle(9,"管理","05","");
		System.out.println(ProfTitleDao.getInstance().add(profTitle));
	}
}

