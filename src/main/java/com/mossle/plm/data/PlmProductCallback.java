package com.mossle.plm.data;

import java.util.List;

import com.mossle.core.csv.CsvCallback;

import com.mossle.plm.persistence.domain.PlmProduct;
import com.mossle.plm.persistence.manager.PlmProductManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlmProductCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(PlmProductCallback.class);
    private PlmProductManager plmProductManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        logger.debug("default tenant id : {}", defaultTenantId);

        String code = list.get(0);
        String name = list.get(1);

        if (StringUtils.isBlank(code)) {
            logger.warn("code cannot be blank {} {}", lineNo, list);

            return;
        }

        code = code.toLowerCase();

        this.createOrUpdatePlmProduct(code, name, lineNo);
    }

    public void createOrUpdatePlmProduct(String code, String name, int lineNo) {
        PlmProduct plmProduct = this.plmProductManager.findUniqueBy("code",
                code);

        if (plmProduct == null) {
            // insert
            plmProduct = new PlmProduct();
            plmProduct.setCode(code);
            plmProduct.setName(name);
            // plmProduct.setTenantId(defaultTenantId);
            this.plmProductManager.save(plmProduct);

            return;
        }

        if (!name.equals(plmProduct.getName())) {
            logger.info("{} update {} to {}", code, plmProduct.getName(), name);
            plmProduct.setName(name);
            plmProductManager.save(plmProduct);

            return;
        }
    }

    // ~
    public void setPlmProductManager(PlmProductManager plmProductManager) {
        this.plmProductManager = plmProductManager;
    }

    public void setDefaultTenantId(String defaultTenantId) {
        this.defaultTenantId = defaultTenantId;
    }
}
