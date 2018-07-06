package com.mossle.cdn;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

public class CdnStrategy {
    private static Logger logger = LoggerFactory.getLogger(CdnStrategy.class);
    private String baseDir;
    private boolean copyDir;
    private ServletContext servletContext;

    @PostConstruct
    public void init() throws Exception {
        if (copyDir) {
            long startTime = System.currentTimeMillis();
            this.copyResources();

            long endTime = System.currentTimeMillis();
            logger.info("cdn init cost {} ms", (endTime - startTime));
        }
    }

    public void copyResources() throws IOException {
        // copy from webapp/cdn to mossle.store/cdn
        File srcDir = new File(servletContext.getRealPath("/") + "/cdn");
        File destDir = new File(baseDir);

        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        logger.info("CDN copy from {} to {}", srcDir, destDir);
        FileUtils.copyDirectory(srcDir, destDir, true);
    }

    @Value("${cdn.baseDir}")
    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    @Value("${cdn.copyDir}")
    public void setCopyDir(boolean copyDir) {
        this.copyDir = copyDir;
    }

    @Resource
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
