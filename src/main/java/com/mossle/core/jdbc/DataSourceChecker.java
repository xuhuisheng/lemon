package com.mossle.core.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourceChecker {
    private static Logger logger = LoggerFactory
            .getLogger(DataSourceChecker.class);
    private Connection conn;

    public void check(DataSource dataSource) throws SQLException {
        try {
            conn = dataSource.getConnection();
        } catch (SQLException ex) {
            logger.warn("error open connection", ex);
            throw ex;
        } finally {
            closeConnection();
        }
    }

    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                logger.info("error on close connection", ex);
            }
        }
    }
}
