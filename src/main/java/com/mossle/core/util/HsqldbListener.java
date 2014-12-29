package com.mossle.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.hsqldb.Server;

/**
 * 控制数据库的周期. context启动的时候，启动hsqldb数据库服务器，context关闭时shutdown数据库服务器
 * 
 * @author Lingo
 * @version 1.0
 * @since 2007-03-13
 * @see javax.servlet.ServletContextListener
 */
public class HsqldbListener implements ServletContextListener {
    /**
     * 配置文件中的占位符，代表webapp发布后的根目录.
     */
    public static final String TOKEN = "${webapp.root}";

    /**
     * 等待数据库停止的最大时间.
     */
    public static final int WAIT_TIME = 1000;

    /**
     * jdbc的url.
     */
    private String url;

    /**
     * 登陆用户名.
     */
    private String username;

    /**
     * 登陆密码.
     */
    private String password;

    /**
     * 处理context初始化事件.
     * 
     * @param sce
     *            ServletContextEvent
     */
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Properties prop = new Properties();
            File file = new File(System.getProperty("appserver.home")
                    + "/data/db.properties");

            if (!file.exists()) {
                System.out.println("data/db.properties isnot exists.");

                return;
            }

            InputStream inputStream = new FileInputStream(file);

            try {
                prop.load(inputStream);
            } finally {
                inputStream.close();
            }

            username = prop.getProperty("db.username");
            password = prop.getProperty("db.password");

            String databaseName = prop.getProperty("db.databaseName");
            int port = Integer.parseInt(prop.getProperty("db.port"));
            String hsqlPath = prop.getProperty("db.path");

            // FIXME: 因为要用到getRealPath方法获得路径，在使用war包发布的时候会出现问题
            if (hsqlPath.startsWith(TOKEN)) {
                String webappRoot = sce.getServletContext().getRealPath("/");
                hsqlPath = hsqlPath.substring(TOKEN.length());
                // if(hsqlPath.charAt(0) == '/' || hsqlPath.charAt(0) == '\\')
                // {
                // hsqlPath = hsqlPath.substring(1);
                // }
                hsqlPath = webappRoot + hsqlPath;
            }

            String databasePath = hsqlPath + "/" + databaseName;
            url = "jdbc:hsqldb:hsql://localhost:" + port + "/" + databaseName;

            Server server = new Server();
            server.setDatabaseName(0, databaseName);

            // ServletContext sc = sce.getServletContext();
            server.setDatabasePath(0, databasePath);
            server.setPort(port);
            server.setSilent(true);
            server.start();
            Thread.sleep(WAIT_TIME);
        } catch (IOException ex) {
            System.out.println("HsqldbListener : contextInitialized : error : "
                    + ex);
        } catch (InterruptedException ex) {
            System.out.println("HsqldbListener : contextInitialized : error : "
                    + ex);
        }
    }

    /**
     * 处理context销毁事件.
     * 
     * @param sce
     *            ServletContextEvent
     */
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            Class.forName("org.hsqldb.jdbcDriver");

            Connection conn = null;
            Statement state = null;

            try {
                // 向数据库发送shutdown命令，关闭数据库
                conn = DriverManager.getConnection(url, username, password);
                state = conn.createStatement();
                state.executeUpdate("SHUTDOWN;");
            } catch (SQLException ex1) {
                System.err.println("关闭数据库时出现异常：" + ex1);
            } finally {
                // 确保关闭Statement
                if (state != null) {
                    try {
                        state.close();
                        state = null;
                    } catch (SQLException ex1) {
                        System.err.println(ex1);
                    }
                }

                // 确保关闭Connection
                if (conn != null) {
                    try {
                        conn.close();
                        conn = null;
                    } catch (SQLException ex1) {
                        System.err.println(ex1);
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            System.err.println("HsqldbListener : contextDestoryed : error : "
                    + ex);

            // ex.printStackTrace();
        }
    }
}
