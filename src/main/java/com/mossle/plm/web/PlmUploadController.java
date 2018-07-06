package com.mossle.plm.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.store.StoreConnector;
import com.mossle.api.store.StoreDTO;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.store.MultipartFileDataSource;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 上传图片.
 */
@Controller
@RequestMapping("plm")
public class PlmUploadController {
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;
    private StoreConnector storeConnector;
    private JsonMapper jsonMapper = new JsonMapper();
    private CurrentUserHolder currentUserHolder;
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
        StoreDTO storeDto = storeConnector.saveStore("upload/plm/images",
                new MultipartFileDataSource(attachment), tenantId);

        String url = request.getContextPath() + "/upload/plm/images/"
                + storeDto.getKey();

        return "<script type='text/javascript'>"
                + "window.parent.CKEDITOR.tools.callFunction(" + callback
                + ",'" + url + "','')" + "</script>";
    }

    // ~ ======================================================================
    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
