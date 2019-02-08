package com.revature.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.connection.BasicConnectionPool;
import com.revature.connection.ConnectionPool;
import com.revature.model.Todo;

public class ExampleDatabaseTest {

	protected static final Logger logger = LogManager.getLogger(ExampleDatabaseTest.class);
	private static ConnectionPool connectionPool;
	
	private static final String CREATE_TODO_TABLE = "create table todos(id int primary key, title varchar(255), body varchar(255))";
	private static final String DROP_TODO_TABLE = "drop table todos";
	private static final String INSERT_TODO = "insert into todos (id, title, body) values (?, ?, ?)";
	private static final String SELECT_ALL_TODOS = "select * from todos";
	private static final String SELECT_ONE_TODO = "select * from todos where id = ?";
	private static final String UPDATE_TODO = "update todos set title = ?, body = ? where id = ?";
	private static final String DELETE_TODO = "delete from todos where id = ?";
	private static final String DELETE_ALL_TODOS = "delete from todos";
	
	@BeforeClass
	public static void setUpDatabase() throws SQLException {
		// Instantiate Connection pool before any tests execute
		connectionPool = BasicConnectionPool.getInstance();
		
		Connection conn = connectionPool.getConnection();
		
		// Assert that the connection is properly instantiated from CP
		assertNotNull(conn);
		
		// Create table for Todo
		Statement stmt = conn.createStatement();
		stmt.execute(CREATE_TODO_TABLE);
		
		// Assert connection properly gets released
		assertTrue("Connection failed to be released", connectionPool.releaseConnection(conn));
	}

	@Before
	public void setUpTestData() throws SQLException {
		Todo todo = new Todo(1, "Demo title", "Demo body");
		Connection conn = connectionPool.getConnection();
		PreparedStatement stmt = conn.prepareStatement(INSERT_TODO);
		stmt.setInt(1, 1);
		stmt.setString(2, todo.getTitle());
		stmt.setString(3, todo.getBody());
		
		// Assert that the Todo gets inserted
		assertEquals(1, stmt.executeUpdate());
		
		// Release connection
		assertTrue("Connection failed to be released", connectionPool.releaseConnection(conn));
	}
	
	@Test
	public void readAll() throws SQLException {
		Connection conn = connectionPool.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(SELECT_ALL_TODOS);
		List<Todo> todos = new ArrayList<>();
		
		while (rs.next()) {
			todos.add(new Todo(rs.getInt("id"), rs.getString("title"), rs.getString("body")));
		}
		
		// Assert the table only has one record
		assertEquals(1, todos.size());
		
		// Release connection
		assertTrue("Connection failed to be released", connectionPool.releaseConnection(conn));
	}
	
	@Test
	public void readOne() throws SQLException {
		Connection conn = connectionPool.getConnection();
		PreparedStatement stmt = conn.prepareStatement(SELECT_ONE_TODO);
		stmt.setInt(1, 1);
		
		ResultSet rs = stmt.executeQuery();
		Todo todo = new Todo(0, "", "");
		while (rs.next()) 
			todo = new Todo(rs.getInt("id"), rs.getString("title"), rs.getString("body"));
		
		// Assert state of Todo is correct
		assertEquals("Demo title", todo.getTitle());
		assertEquals("Demo body", todo.getBody());
		
		// Release connection
		assertTrue("Connection failed to be released", connectionPool.releaseConnection(conn));
	}
	
	@Test
	public void insert() throws SQLException {
		Connection conn = connectionPool.getConnection();
		PreparedStatement stmt = conn.prepareStatement(INSERT_TODO);
		stmt.setInt(1, 2);
		stmt.setString(2, "Demo title 2");
		stmt.setString(3, "Demo body 2");
		
		// Assert that the Todo was inserted
		assertEquals(1, stmt.executeUpdate());
		
		stmt = conn.prepareStatement(SELECT_ALL_TODOS);
		List<Todo> todos = new ArrayList<>();
		ResultSet rs = stmt.executeQuery();
		while (rs.next())
			todos.add(new Todo(rs.getInt("id"), rs.getString("title"), rs.getString("body")));
		
		// Assert that the Todo was inserted
		assertEquals(2, todos.size());
		
		// Release connection
		assertTrue("Connection failed to be released", connectionPool.releaseConnection(conn));
	}
	
	@Test
	public void update() throws SQLException {
		Connection conn = connectionPool.getConnection();
		PreparedStatement stmt = conn.prepareStatement(UPDATE_TODO);
		stmt.setString(1, "UPDATED");
		stmt.setString(2, "UPDATED");
		stmt.setInt(3, 1);
		
		// Assert that the Todo was updated
		assertEquals(1, stmt.executeUpdate());
		
		// Get Todo for state validation
		stmt = conn.prepareStatement(SELECT_ONE_TODO);
		stmt.setInt(1, 1);
		
		ResultSet rs = stmt.executeQuery();
		Todo todo = new Todo(0, "", "");
		while (rs.next()) 
			todo = new Todo(rs.getInt("id"), rs.getString("title"), rs.getString("body"));
		
		assertEquals("UPDATED", todo.getTitle());
		assertEquals("UPDATED", todo.getBody());
		
		// Release connection
		assertTrue("Connection failed to be released", connectionPool.releaseConnection(conn));
	}
	
	@Test
	public void delete() throws SQLException {
		Connection conn = connectionPool.getConnection();
		PreparedStatement stmt = conn.prepareStatement(DELETE_TODO);
		stmt.setInt(1, 1);
		
		// Assert that the Todo was deleted
		assertEquals(1, stmt.executeUpdate());
	}
	
	@After
	public void tearDownTestData() throws SQLException {
		Connection conn = connectionPool.getConnection();
		Statement stmt = conn.createStatement();
		
		stmt.execute(DELETE_ALL_TODOS);
		// Release connection
		assertTrue("Connection failed to be released", connectionPool.releaseConnection(conn));
	}
	
	@AfterClass
	public static void tearDownDatabase() throws SQLException {
		Connection conn = connectionPool.getConnection();
		Statement stmt = conn.createStatement();
		stmt.execute(DROP_TODO_TABLE);
		
		
		connectionPool.shutdown();
	}
}
