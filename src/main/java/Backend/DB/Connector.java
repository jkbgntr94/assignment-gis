package Backend.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Connector {

     	private Connection connection;
		
		private Statement statement;
		
		public Connector() throws SQLException, ClassNotFoundException {
			createConnection();
		}
		
		private void createConnection() throws SQLException, ClassNotFoundException {
			Class.forName("org.postgresql.Driver"); 
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/parks", "postgres", "heslo");
			statement = connection.createStatement();
		}
		
		public Statement getStatement() {
			return statement;
		}
	
	
}
