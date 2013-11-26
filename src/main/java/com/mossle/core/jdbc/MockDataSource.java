package com.mossle.core.jdbc;

import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * mock dataSource.
 * 
 * @author Lingo
 */
public class MockDataSource implements DataSource {
    /**
     * get connection.
     * 
     * @return Connection
     * @throws SQLException
     *             sql exception
     */
    public Connection getConnection() throws SQLException {
        return null;
    }

    /**
     * get connection.
     * 
     * @param username
     *            String
     * @param password
     *            String
     * @return Connection
     * @throws SQLException
     *             sql exception
     */
    public Connection getConnection(String username, String password)
            throws SQLException {
        return null;
    }

    /**
     * get log writer.
     * 
     * @return PrintWriter
     * @throws SQLException
     *             sql exception
     */
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    /**
     * set log writer.
     * 
     * @param out
     *            PrintWriter
     * @throws SQLException
     *             sql exception
     */
    public void setLogWriter(PrintWriter out) throws SQLException {
    }

    /**
     * set login timeout.
     * 
     * @param seconds
     *            int
     * @throws SQLException
     *             sql exception
     */
    public void setLoginTimeout(int seconds) throws SQLException {
    }

    /**
     * get login timeout.
     * 
     * @return login timeout
     * @throws SQLException
     *             sql exception
     */
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    // JDK 6
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    // JDK 6
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    // jdk7
    public Logger getParentLogger() {
        return null;
    }
}
