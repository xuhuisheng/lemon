package com.mossle.core.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpConnectionPool implements Runnable {
    private static Logger logger = LoggerFactory
            .getLogger(HttpConnectionPool.class);
    public static final int TEN_SECOND = 10000;
    private List<HttpConnectionInfo> activeHttpConnectionInfos = new ArrayList<HttpConnectionInfo>();
    private List<HttpConnectionInfo> suspendedHttpConnectionInfos = new ArrayList<HttpConnectionInfo>();
    private String urls;
    private AtomicInteger index = new AtomicInteger(0);
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Thread thread;
    private boolean running;

    public void init() {
        if (urls == null) {
            throw new IllegalArgumentException("urls cannot be null");
        }

        if (urls.trim().length() == 0) {
            throw new IllegalArgumentException("urls cannot be empty");
        }

        for (String url : urls.split(",")) {
            HttpConnectionInfo httpConnectionInfo = new HttpConnectionInfo(url);
            activeHttpConnectionInfos.add(httpConnectionInfo);
        }

        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public void destroy() {
        running = false;
        thread = null;
    }

    public String process(String method, Map<String, String> queryParams,
            Map<String, String> formParams, Map<String, String> headParams) {
        boolean success = false;
        HttpConnectionResult httpConnectionResult = null;

        while (!success) {
            HttpConnectionInfo httpConnectionInfo = this
                    .tryToGetHttpConnectionInfo();
            httpConnectionResult = httpConnectionInfo.process(method,
                    queryParams, formParams, headParams);
            success = httpConnectionResult.isSuccess();

            if (!success) {
                this.suspend(httpConnectionInfo);
            }
        }

        return httpConnectionResult.getContent();
    }

    private HttpConnectionInfo tryToGetHttpConnectionInfo() {
        readWriteLock.readLock().lock();

        try {
            if (activeHttpConnectionInfos.isEmpty()) {
                logger.error("suspended HttpConnectionInfos size is {}",
                        suspendedHttpConnectionInfos.size());
                throw new IllegalStateException(
                        "active HttpConnectionInfos is empty");
            }

            int temperaryIndex = index.incrementAndGet()
                    % activeHttpConnectionInfos.size();

            return activeHttpConnectionInfos.get(temperaryIndex);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    private void suspend(HttpConnectionInfo httpConnectionInfo) {
        readWriteLock.writeLock().lock();

        try {
            logger.info("suspend {}", httpConnectionInfo.getUrl());
            activeHttpConnectionInfos.remove(httpConnectionInfo);
            suspendedHttpConnectionInfos.add(httpConnectionInfo);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private void active(HttpConnectionInfo httpConnectionInfo) {
        readWriteLock.writeLock().lock();

        try {
            logger.info("active {}", httpConnectionInfo.getUrl());
            activeHttpConnectionInfos.add(httpConnectionInfo);
            suspendedHttpConnectionInfos.remove(httpConnectionInfo);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void run() {
        while (running) {
            try {
                Thread.sleep(TEN_SECOND);
            } catch (InterruptedException ex) {
                logger.info(ex.getMessage(), ex);
            }

            List<HttpConnectionInfo> checkedHttpConnectionInfos = new ArrayList(
                    suspendedHttpConnectionInfos);
            logger.debug("suspended : {}", checkedHttpConnectionInfos.size());

            for (HttpConnectionInfo httpConnectionInfo : checkedHttpConnectionInfos) {
                logger.debug("check : {}", httpConnectionInfo.getUrl());

                if (httpConnectionInfo.check()) {
                    active(httpConnectionInfo);
                } else {
                    logger.info("{} still broken", httpConnectionInfo.getUrl());
                }
            }
        }
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    public List<HttpConnectionInfo> getActiveHttpConnectionInfos() {
        return activeHttpConnectionInfos;
    }

    public List<HttpConnectionInfo> getSuspendedHttpConnectionInfos() {
        return suspendedHttpConnectionInfos;
    }
}
