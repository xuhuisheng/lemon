package com.mossle.cms.web;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.store.StoreDTO;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.client.store.StoreClient;

import org.apache.commons.io.IOUtils;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("cms/file")
public class CmsFileController {
    private TenantHolder tenantHolder;
    private StoreClient storeClient;

    @RequestMapping("download")
    @ResponseBody
    public void download(@RequestParam("path") String path,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        StoreDTO storeDto = storeClient.getStore("cms", path, tenantId);

        IOUtils.copy(storeDto.getDataSource().getInputStream(),
                response.getOutputStream());
    }

    // ~ ======================================================================
    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setStoreClient(StoreClient storeClient) {
        this.storeClient = storeClient;
    }
}
