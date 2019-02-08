package com.revature.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BasicConnectionPool implements ConnectionPool {

	private static final Logger logger = LogManager.getLogger(BasicConnectionPool.class);
	
	/**
	 * H2 Database Credentials
	 */
	private static final String url = "jdbc:h2:mem:db";
	private static final String username = "sa";
	private static final String password = "";
	private static final BasicConnectionPool instance = new BasicConnectionPool();
	private static final int INITIAL_POOL_SIZE = 25;
	private final List<Connection> connections = new ArrayList<>(INITIAL_POOL_SIZE);
	private final List<Connection> usedConnections = new ArrayList<>(INITIAL_POOL_SIZE);
	
	// Restrict Instantiation
	private BasicConnectionPool() {
		super();
		logger.trace("Instantiating BasicConnectionPool");
		for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
			try {
				this.connections.add(DriverManager.getConnection(url, username, password));
			} catch (SQLException e) {
				logger.fatal("Failed to obtain connection: {}", e.getMessage());
				logger.fatal("SQL State: {}, Error Code: {}", e.getSQLState(), e.getErrorCode());
				throw new RuntimeException(e);
			}
		}
	}
	
	// Singleton Method
	public static ConnectionPool getInstance() {
		return instance;
	}
	
	@Override
	public Connection getConnection() {
		logger.trace("Getting Connection from ConnectionPool...");
		Connection connection = this.connections.remove(INITIAL_POOL_SIZE - this.usedConnections.size() - 1);
		this.usedConnections.add(connection);
		return connection;
	}

	@Override
	public boolean releaseConnection(Connection connection) {
		logger.trace("Releasing Connection...");
		this.connections.add(connection);
		return this.usedConnections.remove(connection);
	}

	@Override
	public void shutdown() {
		logger.trace("Shutting down Connection Pool...");
		for (Connection c : this.connections) {
			try {
				c.close();
			} catch (SQLException e) {
				logger.fatal("Failed to release connection: {}", e.getMessage());
				logger.fatal("SQL State: {}, Error Code: {}", e.getSQLState(), e.getErrorCode());
				throw new RuntimeException(e);
			}
		}
		
		for (Connection c : this.usedConnections) {
			try {
				c.close();
			} catch (SQLException e) {
				logger.fatal("Failed to release connection: {}", e.getMessage());
				logger.fatal("SQL State: {}, Error Code: {}", e.getSQLState(), e.getErrorCode());
				throw new RuntimeException(e);
			}
		}
	}

}
