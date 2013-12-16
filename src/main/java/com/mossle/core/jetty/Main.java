package com.mossle.core.jetty;

import java.io.File;

import java.net.URL;

import java.security.ProtectionDomain;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class Main {
    public static final int DEFAULT_PORT = 8080;
    public static final int ERROR_CODE = 100;

    protected Main() {
    }

    public static void main(String[] args) throws Exception {
        String contextPath = System.getProperty("contextPath", "/");
        int port = Integer.getInteger("port", DEFAULT_PORT);
        String tempDir = System.getProperty("tempDir", "work");

        Server server = createServer(contextPath, port, tempDir);

        server.start();
        server.join();
    }

    private static Server createServer(String contextPath, int port,
            String tempDir) {
        // use Eclipse JDT compiler
        System.setProperty("org.apache.jasper.compiler.disablejsr199", "true");

        Server server = new Server(port);
        server.setStopAtShutdown(true);

        ProtectionDomain protectionDomain = Main.class.getProtectionDomain();
        URL location = protectionDomain.getCodeSource().getLocation();

        String warFile = location.toExternalForm();
        WebAppContext context = new WebAppContext(warFile, contextPath);
        context.setServer(server);

        // 设置work dir,war包将解压到该目录，jsp编译后的文件也将放入其中。
        String currentDir = new File(location.getPath()).getParent();
        File workDir = new File(currentDir, tempDir);
        context.setTempDirectory(workDir);

        server.setHandler(context);

        return server;
    }
}
