package com.mossle.cms.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.store.StoreDTO;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.client.store.StoreClient;

import com.mossle.cms.CmsConstants;
import com.mossle.cms.persistence.domain.CmsArticle;
import com.mossle.cms.persistence.domain.CmsAttachment;
import com.mossle.cms.persistence.domain.CmsCatalog;
import com.mossle.cms.persistence.domain.CmsComment;
import com.mossle.cms.persistence.manager.CmsArticleManager;
import com.mossle.cms.persistence.manager.CmsAttachmentManager;
import com.mossle.cms.persistence.manager.CmsCatalogManager;
import com.mossle.cms.persistence.manager.CmsCommentManager;
import com.mossle.cms.service.RenderService;
import com.mossle.cms.support.CommentDTO;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.store.MultipartFileDataSource;

import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 搜索.
 */
@Controller
@RequestMapping("search")
public class SearchController {
    private Exportor exportor;
    private MessageHelper messageHelper;
    private CurrentUserHolder currentUserHolder;
    private TenantHolder tenantHolder;
    private BeanMapper beanMapper = new BeanMapper();
    private JsonMapper jsonMapper = new JsonMapper();

    /**
     * 搜索页.
     */
    @RequestMapping("")
    public String index(
            @RequestParam(value = "q", required = false) String query,
            Model model) {
        if (StringUtils.isBlank(query)) {
            return "search/index";
        }

        return "search/result";
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
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
