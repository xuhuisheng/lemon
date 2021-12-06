package com.mossle.plm.web;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;

import com.mossle.api.store.StoreDTO;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.client.store.StoreClient;

import com.mossle.core.store.MultipartFileDataSource;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传图片.
 */
@Controller
@RequestMapping("plm")
public class PlmUploadController {
    private StoreClient storeClient;
    private TenantHolder tenantHolder;

    /**
     * 上传图片.
     */
    @RequestMapping("plm-upload-image")
    @ResponseBody
    public String uploadImage(@RequestParam("CKEditorFuncNum") String callback,
            @RequestParam("upload") MultipartFile attachment,
            HttpServletRequest request) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        StoreDTO storeDto = this.storeClient.saveStore("upload/plm/images",
                new MultipartFileDataSource(attachment), tenantId);

        String url = request.getContextPath() + "/upload/plm/images/"
                + storeDto.getKey();

        return "<script type='text/javascript'>"
                + "window.parent.CKEDITOR.tools.callFunction(" + callback
                + ",'" + url + "','')" + "</script>";
    }

    // ~ ======================================================================
    @Resource
    public void setStoreClient(StoreClient storeClient) {
        this.storeClient = storeClient;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
