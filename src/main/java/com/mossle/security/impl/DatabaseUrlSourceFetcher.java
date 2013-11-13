package com.mossle.security.impl;

import com.mossle.security.api.UrlSourceFetcher;

import org.springframework.beans.factory.InitializingBean;

public class DatabaseUrlSourceFetcher extends AbstractDatabaseSourceFetcher
        implements UrlSourceFetcher, InitializingBean {
    private String appCode;

    public void afterPropertiesSet() throws Exception {
        if (getQuery() != null) {
            return;
        }

        String sql = "select ac.value as access,p.name as perm"
                + " from AUTH_ACCESS ac,AUTH_PERM p"
                + " where ac.perm_id=p.id and ac.type='URL'"
                + " order by priority";
        this.setQuery(sql);
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }
}
