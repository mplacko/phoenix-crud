package eu.placko.examples.hbase.phoenix;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ClientOperations {
	private void createTables(Connection conn) throws SQLException {
	    dropTablesIfExists(conn);
	    
	    System.out.println("Creating tables");
	    Statement stmt = conn.createStatement();
	    stmt.execute("CREATE TABLE IF NOT EXISTS ITEM " +
	        " (id varchar not null primary key, name varchar, quantity integer)");
	    stmt.execute("CREATE TABLE IF NOT EXISTS CUSTOMER " +
	        " (id varchar not null primary key, name varchar, address varchar)");
	    stmt.execute("CREATE TABLE IF NOT EXISTS  \"ORDER\" " +
	        " (id varchar not null primary key, customer_id varchar, creation_time varchar)");
	    stmt.execute("CREATE TABLE IF NOT EXISTS ORDER_LINE_ITEM " +
	        " (order_id varchar not null, item_id varchar not null, sale_quantity integer, "
	        + " constraint pk primary key(order_id, item_id))");
	  }

	  private void dropTablesIfExists(Connection conn) throws SQLException {
	      System.out.println("Dropping tables");
	      Statement stmt = conn.createStatement();
	      stmt.execute("DROP TABLE IF EXISTS ITEM");
	      stmt.execute("DROP TABLE IF EXISTS CUSTOMER");
	      stmt.execute("DROP TABLE IF EXISTS \"ORDER\"");
	      stmt.execute("DROP TABLE IF EXISTS ORDER_LINE_ITEM");
	  }

	  private void populateData(Connection conn) throws SQLException {
	      System.out.println("Populating tables");
	      populateItemData(conn);
	      populateCustomerData(conn);
	  }

	  private void populateItemData(Connection conn) throws SQLException {
	      Statement stmt = conn.createStatement();
	      stmt.execute("UPSERT INTO ITEM VALUES('ITM001','Book', 5)");
	      stmt.execute("UPSERT INTO ITEM VALUES('ITM002','Pen', 5)");
	      stmt.execute("UPSERT INTO ITEM VALUES('ITM003','Soap', 5)");
	      stmt.execute("UPSERT INTO ITEM VALUES('ITM004','Shampoo', 5)");
	      stmt.execute("UPSERT INTO ITEM VALUES('ITM005','Phone', 5)");
	      stmt.execute("UPSERT INTO ITEM VALUES('ITM006','Charger', 5)");
	      conn.commit();
	  }

	  private void populateCustomerData(Connection conn) throws SQLException {
	      Statement stmt = conn.createStatement();
	      stmt.execute("UPSERT INTO CUSTOMER VALUES('CU001','John', 'foo')");
	      stmt.execute("UPSERT INTO CUSTOMER VALUES('CU002','Angel', 'faa')");
	      stmt.execute("UPSERT INTO CUSTOMER VALUES('CU003','David', 'soo')");
	      stmt.execute("UPSERT INTO CUSTOMER VALUES('CU004','Robert', 'laa')");
	      stmt.execute("UPSERT INTO CUSTOMER VALUES('CU005','James', 'naa')");
	      conn.commit();
	  }

	  /**
	   *
	   * @param conn: connection used for performing operation
	   * @param orderId: Order ID of the Creating Order
	   * @param customerId: Customer ID of the customer made an order
	   * @param itemVsQuantity: Items selected with quantities for order
	   * @throws SQLException
	   */
	  private void createOrder(Connection conn, String orderId, String customerId,
	      Map<String, Integer> itemVsQuantity) throws SQLException {
	      Statement stmt = conn.createStatement();
	      stmt.execute("UPSERT INTO \"ORDER\" VALUES('" + orderId + "','" + customerId + "',"
	          + " CURRENT_DATE()||' '|| CURRENT_TIME())");
	      for(Entry<String, Integer> item: itemVsQuantity.entrySet()) {
	          String itemID = item.getKey();
	          int saleQuantity = item.getValue();
	          stmt.execute("UPSERT INTO ORDER_LINE_ITEM VALUES('"+ orderId+"','" +itemID+"',1)");
	          stmt.execute("UPSERT INTO ITEM(ID, QUANTITY)"
	              + " SELECT '"+itemID+"', QUANTITY - " + saleQuantity + " FROM ITEM "
	              + " WHERE ID = '" + itemID + "'");
	      }
	  }
	
	public void run(Connection conn) throws SQLException {
		conn.setAutoCommit(false);
		
		createTables(conn);
		populateData(conn);
	    
		System.out.println("*** CREATING ORDER***");
	    Map<String, Integer> orderItems = new HashMap<>();
	    orderItems.put("ITM001", 2);
	    orderItems.put("ITM002", 3);
	    orderItems.put("ITM003", 2);
	    createOrder(conn, "OR001", "CU001", orderItems);
	    try {
	    	conn.commit();
	    } catch (SQLException e) {
	    	System.out.println(e.getMessage());
		}
		System.out.println("Found " + countRows("\"ORDER\"", conn) + " record(s) in table ORDER");
		
		System.out.println("*** RESULTS ***");
		showTables("ITEM", conn);
		showTables("CUSTOMER", conn);
		showTables("\"ORDER\"", conn);
		showTables("ORDER_LINE_ITEM", conn);

		conn.close();
	  }
	
	private void showTables(String tableName, Connection conn) throws SQLException {
		System.out.println("SELECT * FROM " + tableName);
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
		while (rs.next()) {
		  System.out.println(rs.getString(1) + " | " + rs.getString(2) + " | " + rs.getString(3));
		}
	}
	
	private int countRows(String tableName, Connection conn) throws SQLException {
		Statement stmt = conn.createStatement();
		try (ResultSet results = stmt.executeQuery("SELECT COUNT(1) FROM " + tableName)) {
	      if (!results.next()) {
	        throw new RuntimeException("Query should have result!");
	      }
	      return results.getInt(1);
	    }
	}
}