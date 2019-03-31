package com.mossle.cms.web.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.store.StoreDTO;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.client.store.StoreClient;

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
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.store.MultipartFileDataSource;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("cms/admin/one-catalog")
public class OneCatalogController {
    private CmsArticleManager cmsArticleManager;
    private CmsCatalogManager cmsCatalogManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;
    private RenderService renderService;
    private TenantHolder tenantHolder;
    private CmsAttachmentManager cmsAttachmentManager;
    private StoreClient storeClient;
    private CurrentUserHolder currentUserHolder;
    private CmsCommentManager cmsCommentManager;

    @RequestMapping("{code}/list")
    public String list(@PathVariable("code") String code,
            @ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        CmsCatalog cmsCatalog = this.cmsCatalogManager.findUniqueBy("code",
                code);
        Long cmsCatalogId = cmsCatalog.getId();
        model.addAttribute("cmsCatalog", cmsCatalog);

        page.setDefaultOrder("publishTime", Page.DESC);

        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        propertyFilters.add(new PropertyFilter("EQL_cmsCatalog.id", Long
                .toString(cmsCatalogId)));
        page = cmsArticleManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "cms/admin/one-catalog/list";
    }

    @RequestMapping("{code}/input")
    public String input(@PathVariable("code") String code,
            @RequestParam(value = "id", required = false) Long id, Model model) {
        CmsCatalog cmsCatalog = this.cmsCatalogManager.findUniqueBy("code",
                code);

        model.addAttribute("cmsCatalog", cmsCatalog);

        if (id != null) {
            CmsArticle cmsArticle = cmsArticleManager.get(id);
            model.addAttribute("model", cmsArticle);
        }

        return "cms/admin/one-catalog/input";
    }

    @RequestMapping("{code}/save")
    public String save(
            @PathVariable("code") String code,
            @ModelAttribute CmsArticle cmsArticle,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
            RedirectAttributes redirectAttributes, Model model)
            throws Exception {
        CmsCatalog cmsCatalog = this.cmsCatalogManager.findUniqueBy("code",
                code);

        model.addAttribute("cmsCatalog", cmsCatalog);

        String tenantId = tenantHolder.getTenantId();
        Long id = cmsArticle.getId();
        CmsArticle dest = null;

        if (id != null) {
            dest = cmsArticleManager.get(id);
            beanMapper.copy(cmsArticle, dest);
        } else {
            dest = cmsArticle;
        }

        if (id == null) {
            dest.setUserId(currentUserHolder.getUserId());
            dest.setCreateTime(new Date());
            dest.setTenantId(tenantId);
        }

        dest.setCmsCatalog(cmsCatalog);
        cmsArticleManager.save(dest);

        // attachment
        if (file != null) {
            StoreDTO storeDto = storeClient.saveStore("cms/html/r/attachments",
                    new MultipartFileDataSource(file), tenantId);
            CmsAttachment cmsAttachment = new CmsAttachment();
            cmsAttachment.setCmsArticle(dest);
            cmsAttachment.setName(file.getOriginalFilename());
            cmsAttachment.setPath(storeDto.getKey());
            cmsAttachmentManager.save(cmsAttachment);
        }

        // logo
        if (logoFile != null) {
            StoreDTO storeDto = storeClient.saveStore("cms/html/r/attachments",
                    new MultipartFileDataSource(logoFile), tenantId);
            dest.setLogo(storeDto.getKey());
        }

        cmsArticleManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/cms/admin/one-catalog/" + code + "/list";
    }

    /**
     * 删除文章.
     */
    @RequestMapping("remove")
    public String remove(@PathVariable("code") String code,
            @RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<CmsArticle> cmsArticles = cmsArticleManager
                .findByIds(selectedItem);

        for (CmsArticle cmsArticle : cmsArticles) {
            cmsCommentManager.removeAll(cmsArticle.getCmsComments());
            cmsArticleManager.remove(cmsArticle);
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/cms/admin/one-catalog/" + code + "/list";
    }

    /**
     * 发布.
     */
    @RequestMapping("{code}/publish")
    public String publish(@PathVariable("code") String code,
            @RequestParam("id") Long id) {
        CmsArticle cmsArticle = cmsArticleManager.get(id);
        cmsArticle.setPublishTime(new Date());
        cmsArticle.setStatus(1);
        renderService.render(cmsArticle);
        cmsArticleManager.save(cmsArticle);

        return "redirect:/cms/admin/one-catalog/" + code + "/list";
    }

    /**
     * 下线.
     */
    @RequestMapping("{code}/withdraw")
    public String withdraw(@PathVariable("code") String code,
            @RequestParam("id") Long id) {
        CmsArticle cmsArticle = cmsArticleManager.get(id);
        cmsArticle.setStatus(0);
        renderService.render(cmsArticle);
        cmsArticleManager.save(cmsArticle);

        return "redirect:/cms/admin/one-catalog/" + code + "/list";
    }

    /**
     * 查看.
     */
    @RequestMapping("{code}/view")
    public String view(@PathVariable("code") String code,
            @RequestParam("id") Long id, @ModelAttribute Page page, Model model) {
        List<CmsCatalog> cmsCatalogs = this.cmsCatalogManager.getAll();
        CmsArticle cmsArticle = this.cmsArticleManager.get(id);
        page = this.cmsCommentManager
                .pagedQuery(
                        "from CmsComment where cmsArticle=? and conversation=null order by id desc",
                        page.getPageNo(), page.getPageSize(), cmsArticle);

        List<CmsComment> cmsComments = (List<CmsComment>) page.getResult();

        List<CommentDTO> commentDtos = new ArrayList<CommentDTO>();
        page.setResult(commentDtos);

        for (CmsComment cmsComment : cmsComments) {
            CommentDTO commentDto = new CommentDTO();
            commentDto.setCmsComment(cmsComment);

            String hql = "from CmsComment where conversation=? order by id asc";
            commentDto.setChildren(cmsCommentManager.find(hql,
                    cmsComment.getId()));
            commentDtos.add(commentDto);
        }

        String html = renderService.view(cmsArticle, cmsCatalogs, page);

        model.addAttribute("html", html);

        return "cms/admin/one-catalog/view";
    }

    // ~ ======================================================================
    @Resource
    public void setCmsArticleManager(CmsArticleManager cmsArticleManager) {
        this.cmsArticleManager = cmsArticleManager;
    }

    @Resource
    public void setCmsCatalogManager(CmsCatalogManager cmsCatalogManager) {
        this.cmsCatalogManager = cmsCatalogManager;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setRenderService(RenderService renderService) {
        this.renderService = renderService;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setCmsAttachmentManager(
            CmsAttachmentManager cmsAttachmentManager) {
        this.cmsAttachmentManager = cmsAttachmentManager;
    }

    @Resource
    public void setStoreClient(StoreClient storeClient) {
        this.storeClient = storeClient;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setCmsCommentManager(CmsCommentManager cmsCommentManager) {
        this.cmsCommentManager = cmsCommentManager;
    }
}
