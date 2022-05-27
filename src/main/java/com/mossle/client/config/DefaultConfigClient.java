package com.mossle.client.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

public class DefaultConfigClient implements ConfigClient {
    private String appName = "app";
    private String localPath = "/tmp";
    private RemoteConfigProvider remoteConfigProvider = new MockRemoteConfigProvider();

    // cache
    private Config appConfig;

    public Config getConfig() {
        Config config = appConfig;

        if (config != null) {
            return config;
        }

        config = findConfigFromRemote();

        if (config != null) {
            appConfig = config;

            return config;
        }

        config = findConfigFromLocal();

        if (config != null) {
            appConfig = config;

            return config;
        }

        throw new IllegalStateException("cannot find config");
    }

    public Config findConfigFromLocal() {
        InputStream is = null;

        try {
            is = new FileInputStream(calculateLocalFilePath());

            String content = IOUtils.toString(is, "UTF-8");

            return new DefaultConfig(content);
        } catch (Exception ex) {
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception ex) {
                }

                is = null;
            }
        }
    }

    public void saveConfigToLocal(Config config) {
        OutputStream os = null;

        try {
            os = new FileOutputStream(calculateLocalFilePath());
            os.write(config.getContent().getBytes("UTF-8"));
        } catch (Exception ex) {
        } finally {
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (Exception ex) {
                }

                os = null;
            }
        }
    }

    public Config findConfigFromRemote() {
        return remoteConfigProvider.findConfigByApp(appName);
    }

    public String calculateLocalFilePath() {
        return localPath + "/" + appName + ".properties";
    }
}
