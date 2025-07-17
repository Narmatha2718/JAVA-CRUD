import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
	public static void main(String args[]) throws ClassNotFoundException, SQLException
	{
		Class.forName("com.mysql.cj.jdbc.Driver");
		String url="jdbc:mysql://localhost:3306/test_db";
		String username="root";
		String password="narmatha@123";
		Connection con=DriverManager.getConnection(url, username, password);
		Statement stmt=con.createStatement();
		String query="create table student_information (id int,name varchar(20),age int)";
		stmt.executeUpdate(query);//insert,modify,update,delete
		
		
		System.out.println("Table created successfully");
	}
}
