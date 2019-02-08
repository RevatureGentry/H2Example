package com.revature.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConnectionFactory {
	
	private static final Logger logger = LogManager.getLogger(ConnectionFactory.class);
	
	// Restrict Instantiation
	private ConnectionFactory() {}
	
	public static final Connection getConnection() {
		try {
			return DriverManager.getConnection("jdbc:h2:mem:db", "sa", "");
		} catch (SQLException e) {
			logger.fatal("Failed to retrieve connection: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
