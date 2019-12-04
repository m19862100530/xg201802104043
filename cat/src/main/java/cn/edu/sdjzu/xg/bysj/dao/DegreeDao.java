package cn.edu.sdjzu.xg.bysj.dao;

import cn.edu.sdjzu.xg.bysj.domain.Degree;
import util.JdbcHelper;

import java.sql.*;
import java.util.Collection;
import java.util.TreeSet;

public final class DegreeDao {
	private static DegreeDao degreeDao=
			new DegreeDao();
	private DegreeDao(){}
	public static DegreeDao getInstance(){
		return degreeDao;
	}
	private static Collection<Degree> degrees;
	static{
		degrees = new TreeSet<Degree>();
		Degree doctor = new Degree(1,
				"博士","01","");
		degrees.add(doctor);
		degrees.add(new Degree(2,
				"硕士","02",""));
		degrees.add(new Degree(3,
				"学士","03",""));
	}


	public Collection<Degree> findAll() throws SQLException {
		degrees = new TreeSet<>();
		//获取数据库连接对象
		Connection connection = JdbcHelper.getConn();
		//执行sql查询语句并获得结果集对象
		Statement stmt = connection.createStatement();
		ResultSet resultSet = stmt.executeQuery("select * from degree");
		while (resultSet.next()){
			degrees.add(new Degree(resultSet.getInt("id"),resultSet.getString("no"),
					resultSet.getString("description"),resultSet.getString("remarks")));
		}
		JdbcHelper.close(stmt,connection);
		return DegreeDao.degrees;
	}

	public Degree find(Integer id) throws SQLException {
		Degree desiredDegree = null;
		Connection connection = JdbcHelper.getConn();
		//根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
		PreparedStatement preparedStatement = connection.prepareStatement(
				"SELECT * FROM degree WHERE id = ?");
		//为预编译参数赋值
		preparedStatement.setInt(1,id);
		ResultSet resultSet = preparedStatement.executeQuery();
		//由于id不能取重复值，故结果集中最多有一条记录
		//若结果集有一条记录，则以当前记录中的id,description,no,remarks值为参数，创建Degree对象
		//若结果集中没有记录，则本方法返回null
		if (resultSet.next()){
			desiredDegree = new Degree(resultSet.getInt("id"),resultSet.getString("description"),resultSet.getString("no"),resultSet.getString("remarks"));
		}
		//关闭资源
		JdbcHelper.close(resultSet,preparedStatement,connection);
		return desiredDegree;
		}

	public boolean update(Degree degree) throws SQLException {
		//获取数据库连接对象
		Connection connection = JdbcHelper.getConn();
		//根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
		PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE degree SET description = ? ,no = ?,remarks = ? WHERE id = ?");
		//为预编译语句赋值
		preparedStatement.setString(1,degree.getDescription());
		preparedStatement.setString(2,degree.getNo());
		preparedStatement.setString(3,degree.getRemarks());
		preparedStatement.setInt(4,degree.getId());
		int affectedRowNum = preparedStatement.executeUpdate();
		//执行预编译语句，用其返回值、影响的行为数为赋值affectedRowNum
		JdbcHelper.close(preparedStatement,connection);
		return affectedRowNum>0;
	}

	public boolean add(Degree degree) throws SQLException {
		//获取数据库连接对象
		Connection connection = JdbcHelper.getConn();
		//根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
		PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO degree" + "(no,description,remarks)"+" VALUES (?,?,?)");
		preparedStatement.setString(1,degree.getNo());
		preparedStatement.setString(2,degree.getDescription());
		preparedStatement.setString(3,degree.getRemarks());
		int affectedRowNum = preparedStatement.executeUpdate();
		//执行预编译语句，用其返回值、影响的行为数为赋值affectedRowNum
		JdbcHelper.close(preparedStatement,connection);
		return affectedRowNum>0;
	}

	public boolean delete(Integer id) throws SQLException{
		//获取数据库连接对象
		Connection connection = JdbcHelper.getConn();
		//创建sql语句
		String deleteDegree_sql = "DELETE FROM degree WHERE id =?";
		//根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
		PreparedStatement preparedStatement = connection.prepareStatement(deleteDegree_sql);
		preparedStatement.setInt(1, id);
		//执行预编译对象的executeUpdate方法，获取删除的记录行数
		int affectedRowNum = preparedStatement.executeUpdate();
		JdbcHelper.close(preparedStatement,connection);
		return affectedRowNum>0;
	}

	public boolean delete(Degree degree) throws SQLException {
		return delete(degree.getId());
	}

	public static void main(String[] args) throws SQLException{
		//找到id为2的degree1对象
		Degree degree1 = DegreeDao.getInstance().find(606);
		System.out.println(degree1);
		degree1.setDescription("博士");
		DegreeDao.getInstance().update(degree1);
		//找到id为2的degree2对象
		Degree degree2 = DegreeDao.getInstance().find(606);
		System.out.println(degree2.getDescription());
		System.out.println(degree2);
		Degree degree = new Degree("博士","07","");
		System.out.println(DegreeDao.getInstance().add(degree));
		//System.out.println(degree2);
	}
}

