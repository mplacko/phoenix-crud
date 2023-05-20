package eu.placko.examples.hbase.phoenix;

import java.sql.Connection;
import java.sql.DriverManager;

public class ClientConnect {	
	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			throw new IllegalArgumentException("Usage: eu.placko.examples.hbase.phoenix.ClientConnect \"jdbc:phoenix:thin:url=https://<pqs.endpoint>:8765;serialization=PROTOBUF;authentication=SPNEGO;principal=<user@realm>;keytab=/etc/security/keytabs/<user>.keytab\"");
		    }
		
		connect(args[0]);
    }
	
	private static void connect(String jdbcUrl) throws Exception {
	    Class.forName("org.apache.phoenix.queryserver.client.Driver");
	    
	    try (Connection conn = DriverManager.getConnection(jdbcUrl)){
	    	System.out.println("Connection created");
	    	
	    	ClientOperations cl = new ClientOperations();
	        cl.run(conn);
	    }
    }
}