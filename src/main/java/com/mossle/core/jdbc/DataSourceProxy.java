package com.mossle.core.jdbc;

import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * dataSource proxy.
 * 
 * @author Lingo
 */
public class DataSourceProxy extends MockDataSource {
    private DataSource targetDataSource;

    /**
     * get connection.
     * 
     * @return Connection
     * @throws SQLException
     *             sql exception
     */
    public Connection getConnection() throws SQLException {
        return this.targetDataSource.getConnection();
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
        return this.targetDataSource.getConnection(username, password);
    }

    /**
     * get log writer.
     * 
     * @return PrintWriter
     * @throws SQLException
     *             sql exception
     */
    public PrintWriter getLogWriter() throws SQLException {
        return this.targetDataSource.getLogWriter();
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
        this.targetDataSource.setLogWriter(out);
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
        this.targetDataSource.setLoginTimeout(seconds);
    }

    /**
     * get login timeout.
     * 
     * @return login timeout
     * @throws SQLException
     *             sql exception
     */
    public int getLoginTimeout() throws SQLException {
        return this.targetDataSource.getLoginTimeout();
    }

    // JDK 6
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.targetDataSource.isWrapperFor(iface);
    }

    // JDK 6
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return this.targetDataSource.unwrap(iface);
    }

    // ~ ======================================================================
    public DataSource getTargetDataSource() {
        return this.targetDataSource;
    }

    public void setTargetDataSource(DataSource targetDataSource) {
        this.targetDataSource = targetDataSource;
    }
}
