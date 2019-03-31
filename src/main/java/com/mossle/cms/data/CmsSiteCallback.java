package com.mossle.cms.data;

import java.util.List;

import com.mossle.cms.persistence.domain.CmsSite;
import com.mossle.cms.persistence.manager.CmsSiteManager;

import com.mossle.core.csv.CsvCallback;

public class CmsSiteCallback implements CsvCallback {
    private CmsSiteManager cmsSiteManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        String code = list.get(0);
        String name = list.get(1);
        String templateCode = list.get(2);

        code = code.toLowerCase();

        CmsSite cmsSite = cmsSiteManager.findUniqueBy("code", code);

        if (cmsSite != null) {
            return;
        }

        cmsSite = new CmsSite();
        cmsSite.setCode(code);
        cmsSite.setName(name);
        cmsSite.setTemplateCode(templateCode);
        cmsSite.setTenantId(defaultTenantId);
        cmsSiteManager.save(cmsSite);
    }

    public void setCmsSiteManager(CmsSiteManager cmsSiteManager) {
        this.cmsSiteManager = cmsSiteManager;
    }
}
