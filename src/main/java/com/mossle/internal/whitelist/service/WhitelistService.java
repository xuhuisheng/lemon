package com.mossle.internal.whitelist.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.core.mapper.BeanMapper;

import com.mossle.internal.whitelist.persistence.domain.WhitelistApp;
import com.mossle.internal.whitelist.persistence.domain.WhitelistHost;
import com.mossle.internal.whitelist.persistence.domain.WhitelistIp;
import com.mossle.internal.whitelist.persistence.domain.WhitelistType;
import com.mossle.internal.whitelist.persistence.manager.WhitelistAppManager;
import com.mossle.internal.whitelist.persistence.manager.WhitelistHostManager;
import com.mossle.internal.whitelist.persistence.manager.WhitelistIpManager;
import com.mossle.internal.whitelist.persistence.manager.WhitelistTypeManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WhitelistService {
    private static Logger logger = LoggerFactory
            .getLogger(WhitelistService.class);
    private WhitelistAppManager whitelistAppManager;
    private WhitelistTypeManager whitelistTypeManager;
    private WhitelistHostManager whitelistHostManager;
    private WhitelistIpManager whitelistIpManager;
    private BeanMapper beanMapper = new BeanMapper();

    public void saveWhitelistApp(WhitelistApp whitelistApp,
            Long whitelistTypeId, String hostContent, String ipContent,
            String username, String tenantId) {
        Long id = whitelistApp.getId();
        WhitelistApp dest = null;

        if (id != null) {
            whitelistApp.setWhitelistHosts(null);
            whitelistApp.setWhitelistIps(null);
            dest = whitelistAppManager.get(id);
            beanMapper.copy(whitelistApp, dest);
        } else {
            dest = whitelistApp;
            dest.setUserId(username);
            dest.setTenantId(tenantId);
        }

        // type
        WhitelistType whitelistType = whitelistTypeManager.get(whitelistTypeId);
        dest.setWhitelistType(whitelistType);

        // host
        this.processHost(dest, Arrays.asList(hostContent.split("\n")));

        // ip
        this.processIp(dest, Arrays.asList(ipContent.split("\n")));

        whitelistAppManager.save(dest);
    }

    public List<String> trim(List<String> list) {
        List<String> targetList = new ArrayList<String>();

        for (String text : list) {
            text = text.trim();

            if ("".equals(text)) {
                continue;
            }

            if (!targetList.contains(text)) {
                targetList.add(text);
            }
        }

        return targetList;
    }

    public void processHost(WhitelistApp whitelistApp, List<String> hosts) {
        hosts = this.trim(hosts);
        logger.info("hosts : {}", hosts);

        List<WhitelistHost> whitelistHosts = new ArrayList<WhitelistHost>(
                whitelistApp.getWhitelistHosts());
        logger.info("whitelistHosts : {}", whitelistHosts);

        for (WhitelistHost whitelistHost : whitelistHosts) {
            String value = whitelistHost.getValue();

            logger.info("hosts.contains(value) : {}", hosts.contains(value));

            if (!hosts.contains(value)) {
                whitelistHostManager.remove(whitelistHost);
                whitelistApp.getWhitelistHosts().remove(whitelistHost);
            }
        }

        logger.info("whitelistApp.getWhitelistHosts() : {}",
                whitelistApp.getWhitelistHosts());

        int index = 0;

        for (String host : hosts) {
            index++;

            boolean isExists = false;

            for (WhitelistHost whitelistHost : whitelistApp.getWhitelistHosts()) {
                String value = whitelistHost.getValue();

                if (host.equals(value)) {
                    isExists = true;
                    whitelistHost.setPriority(index);
                    whitelistHostManager.save(whitelistHost);

                    break;
                }
            }

            if (!isExists) {
                WhitelistHost whitelistHost = new WhitelistHost();
                whitelistHost.setValue(host);
                whitelistHost.setPriority(index);
                whitelistHost.setWhitelistApp(whitelistApp);
                whitelistHost.setTenantId(whitelistApp.getTenantId());
                whitelistHostManager.save(whitelistHost);
            }
        }
    }

    public void processIp(WhitelistApp whitelistApp, List<String> ips) {
        ips = this.trim(ips);

        List<WhitelistIp> whitelistIps = new ArrayList<WhitelistIp>(
                whitelistApp.getWhitelistIps());

        for (WhitelistIp whitelistIp : whitelistIps) {
            String value = whitelistIp.getValue();

            if (!ips.contains(value)) {
                whitelistIpManager.remove(whitelistIp);
                whitelistApp.getWhitelistIps().remove(whitelistIp);
            }
        }

        int index = 0;

        for (String ip : ips) {
            index++;

            boolean isExists = false;

            for (WhitelistIp whitelistIp : whitelistApp.getWhitelistIps()) {
                String value = whitelistIp.getValue();

                if (ip.equals(value)) {
                    isExists = true;
                    whitelistIp.setPriority(index);
                    whitelistIpManager.save(whitelistIp);

                    break;
                }
            }

            if (!isExists) {
                WhitelistIp whitelistIp = new WhitelistIp();
                whitelistIp.setValue(ip);
                whitelistIp.setPriority(index);
                whitelistIp.setWhitelistApp(whitelistApp);
                whitelistIp.setTenantId(whitelistApp.getTenantId());
                whitelistIpManager.save(whitelistIp);
            }
        }
    }

    @Resource
    public void setWhitelistAppManager(WhitelistAppManager whitelistAppManager) {
        this.whitelistAppManager = whitelistAppManager;
    }

    @Resource
    public void setWhitelistTypeManager(
            WhitelistTypeManager whitelistTypeManager) {
        this.whitelistTypeManager = whitelistTypeManager;
    }

    @Resource
    public void setWhitelistHostManager(
            WhitelistHostManager whitelistHostManager) {
        this.whitelistHostManager = whitelistHostManager;
    }

    @Resource
    public void setWhitelistIpManager(WhitelistIpManager whitelistIpManager) {
        this.whitelistIpManager = whitelistIpManager;
    }
}
