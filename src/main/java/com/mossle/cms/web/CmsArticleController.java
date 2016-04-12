package com.mossle.cms.web;

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

import com.mossle.cms.CmsConstants;
import com.mossle.cms.persistence.domain.CmsArticle;
import com.mossle.cms.persistence.domain.CmsAttachment;
import com.mossle.cms.persistence.manager.CmsArticleManager;
import com.mossle.cms.persistence.manager.CmsAttachmentManager;
import com.mossle.cms.persistence.manager.CmsCatalogManager;
import com.mossle.cms.persistence.manager.CmsCommentManager;
import com.mossle.cms.service.RenderService;

import com.mossle.core.auth.CurrentUserHolder;
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
 * 文章.
 */
@Controller
@RequestMapping("cms")
public class CmsArticleController {
    private CmsArticleManager cmsArticleManager;
    private CmsCatalogManager cmsCatalogManager;
    private CmsAttachmentManager cmsAttachmentManager;
    private CmsCommentManager cmsCommentManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;
    private RenderService renderService;
    private StoreConnector storeConnector;
    private JsonMapper jsonMapper = new JsonMapper();
    private CurrentUserHolder currentUserHolder;
    private TenantHolder tenantHolder;

    /**
     * 文章列表.
     */
    @RequestMapping("cms-article-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = cmsArticleManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "cms/cms-article-list";
    }

    /**
     * 编辑.
     */
    @RequestMapping("cms-article-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        String tenantId = tenantHolder.getTenantId();

        if (id != null) {
            CmsArticle cmsArticle = cmsArticleManager.get(id);
            model.addAttribute("model", cmsArticle);
        }

        model.addAttribute("cmsCatalogs",
                cmsCatalogManager.findBy("tenantId", tenantId));

        return "cms/cms-article-input";
    }

    /**
     * 保存文章.
     */
    @RequestMapping("cms-article-save")
    public String save(@ModelAttribute CmsArticle cmsArticle,
            @RequestParam("cmsCatalogId") Long cmsCatalogId,
            @RequestParam(value = "file", required = false) MultipartFile file,
            RedirectAttributes redirectAttributes) throws Exception {
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

        dest.setCmsCatalog(cmsCatalogManager.get(cmsCatalogId));
        cmsArticleManager.save(dest);

        // attachment
        if (file != null) {
            StoreDTO storeDto = storeConnector.saveStore(
                    "cms/html/r/attachments",
                    new MultipartFileDataSource(file), tenantId);
            CmsAttachment cmsAttachment = new CmsAttachment();
            cmsAttachment.setCmsArticle(dest);
            cmsAttachment.setName(file.getOriginalFilename());
            cmsAttachment.setPath(storeDto.getKey());
            cmsAttachmentManager.save(cmsAttachment);
        }

        cmsArticleManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/cms/cms-article-list.do";
    }

    /**
     * 删除文章.
     */
    @RequestMapping("cms-article-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<CmsArticle> cmsArticles = cmsArticleManager
                .findByIds(selectedItem);

        for (CmsArticle cmsArticle : cmsArticles) {
            cmsCommentManager.removeAll(cmsArticle.getCmsComments());
            cmsArticleManager.remove(cmsArticle);
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/cms/cms-article-list.do";
    }

    /**
     * 导出
     */
    @RequestMapping("cms-article-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = cmsArticleManager.pagedQuery(page, propertyFilters);

        List<CmsArticle> cmsArticles = (List<CmsArticle>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("cmsArticle");
        tableModel.addHeaders("id", "name");
        tableModel.setData(cmsArticles);
        exportor.export(request, response, tableModel);
    }

    /**
     * 检查重名.
     */
    @RequestMapping("cms-article-checkName")
    @ResponseBody
    public boolean checkName(@RequestParam("name") String name,
            @RequestParam(value = "id", required = false) Long id)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        String hql = "from CmsArticle where name=? and tenantId=?";
        Object[] params = { name, tenantId };

        if (id != null) {
            hql = "from CmsArticle where name=? and tenantId=? and id<>?";
            params = new Object[] { name, tenantId, id };
        }

        CmsArticle cmsArticle = cmsArticleManager.findUnique(hql, params);

        boolean result = (cmsArticle == null);

        return result;
    }

    /**
     * 发布.
     */
    @RequestMapping("cms-article-publish")
    public String publish(@RequestParam("id") Long id) {
        CmsArticle cmsArticle = cmsArticleManager.get(id);
        cmsArticle.setPublishTime(new Date());
        cmsArticle.setStatus(1);
        renderService.render(cmsArticle);

        return "redirect:/cms/cms-article-list.do";
    }

    /**
     * 查看.
     */
    @RequestMapping("cms-article-view")
    public String view(@RequestParam("id") Long id, Model model) {
        CmsArticle cmsArticle = cmsArticleManager.get(id);
        String html = renderService.view(cmsArticle);

        model.addAttribute("html", html);

        return "cms/cms-article-view";
    }

    /**
     * 上传图片.
     */
    @RequestMapping("cms-article-uploadImage")
    @ResponseBody
    public String uploadImage(@RequestParam("CKEditorFuncNum") String callback,
            @RequestParam("upload") MultipartFile attachment) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        StoreDTO storeDto = storeConnector.saveStore("cms/html/r/images",
                new MultipartFileDataSource(attachment), tenantId);

        return "<script type='text/javascript'>"
                + "window.parent.CKEDITOR.tools.callFunction(" + callback
                + ",'" + "r/images/" + storeDto.getKey() + "','')"
                + "</script>";
    }

    /**
     * 下载.
     */
    @RequestMapping("cms-article-download")
    @ResponseBody
    public String download(@RequestParam("id") Long id) throws Exception {
        List<CmsAttachment> cmsAttachments = cmsAttachmentManager.findBy(
                "cmsArticle.id", id);

        Map<String, Object> data = new HashMap<String, Object>();
        List<Map<String, Object>> files = new ArrayList<Map<String, Object>>();
        data.put("files", files);

        for (CmsAttachment cmsAttachment : cmsAttachments) {
            Map<String, Object> map = new HashMap<String, Object>();
            files.add(map);
            map.put("name", cmsAttachment.getName());
            map.put("url", "../rs/cms/image?key=" + cmsAttachment.getPath());

            // map.put("thumbnailUrl", "./rs/cms/image?key=" + storeDto.getKey());
        }

        return jsonMapper.toJson(data);
    }

    /**
     * 上传.
     */
    @RequestMapping("cms-article-upload")
    @ResponseBody
    public String upload(@RequestParam("id") Long id,
            @RequestParam("files[]") MultipartFile attachment) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        StoreDTO storeDto = storeConnector.saveStore("cms/html/r/image",
                new MultipartFileDataSource(attachment), tenantId);
        CmsArticle cmsArticle = cmsArticleManager.get(id);
        CmsAttachment cmsAttachment = new CmsAttachment();
        cmsAttachment.setCmsArticle(cmsArticle);
        cmsAttachment.setName(attachment.getOriginalFilename());
        cmsAttachment.setPath(storeDto.getKey());
        cmsAttachmentManager.save(cmsAttachment);

        Map<String, Object> data = new HashMap<String, Object>();
        List<Map<String, Object>> files = new ArrayList<Map<String, Object>>();
        data.put("files", files);

        Map<String, Object> map = new HashMap<String, Object>();
        files.add(map);
        map.put("name", attachment.getOriginalFilename());
        map.put("url", "../rs/cms/image?key=" + storeDto.getKey());

        // map.put("thumbnailUrl", "./rs/cms/image?key=" + storeDto.getKey());
        return jsonMapper.toJson(data);
    }

    /**
     * 图库类文章.
     */
    @RequestMapping("cms-article-image")
    public String imageArticle(
            @RequestParam(value = "id", required = false) Long id, Model model) {
        String tenantId = tenantHolder.getTenantId();

        if (id == null) {
            CmsArticle cmsArticle = new CmsArticle();
            cmsArticle.setUserId(currentUserHolder.getUserId());
            cmsArticle.setCreateTime(new Date());
            cmsArticle.setTenantId(tenantId);
            cmsArticle.setContent("");
            cmsArticle.setType(CmsConstants.TYPE_IMAGE);
            cmsArticleManager.save(cmsArticle);
            model.addAttribute("model", cmsArticle);
        } else {
            CmsArticle cmsArticle = cmsArticleManager.get(id);
            model.addAttribute("model", cmsArticle);
        }

        model.addAttribute("cmsCatalogs",
                cmsCatalogManager.findBy("tenantId", tenantId));

        return "cms/cms-article-image";
    }

    /**
     * 音频类文章.
     */
    @RequestMapping("cms-article-audio")
    public String audioArticle(
            @RequestParam(value = "id", required = false) Long id, Model model) {
        String tenantId = tenantHolder.getTenantId();

        if (id == null) {
            CmsArticle cmsArticle = new CmsArticle();
            cmsArticle.setUserId(currentUserHolder.getUserId());
            cmsArticle.setCreateTime(new Date());
            cmsArticle.setTenantId(tenantId);
            cmsArticle.setContent("");
            cmsArticle.setType(CmsConstants.TYPE_AUDIO);
            cmsArticleManager.save(cmsArticle);
            model.addAttribute("model", cmsArticle);
        } else {
            CmsArticle cmsArticle = cmsArticleManager.get(id);
            model.addAttribute("model", cmsArticle);
        }

        model.addAttribute("cmsCatalogs",
                cmsCatalogManager.findBy("tenantId", tenantId));

        return "cms/cms-article-audio";
    }

    /**
     * 视频文章.
     */
    @RequestMapping("cms-article-video")
    public String videoArticle(
            @RequestParam(value = "id", required = false) Long id, Model model) {
        String tenantId = tenantHolder.getTenantId();

        if (id == null) {
            CmsArticle cmsArticle = new CmsArticle();
            cmsArticle.setUserId(currentUserHolder.getUserId());
            cmsArticle.setCreateTime(new Date());
            cmsArticle.setTenantId(tenantId);
            cmsArticle.setContent("");
            cmsArticle.setType(CmsConstants.TYPE_VIDEO);
            cmsArticleManager.save(cmsArticle);
            model.addAttribute("model", cmsArticle);
        } else {
            CmsArticle cmsArticle = cmsArticleManager.get(id);
            model.addAttribute("model", cmsArticle);
        }

        model.addAttribute("cmsCatalogs",
                cmsCatalogManager.findBy("tenantId", tenantId));

        return "cms/cms-article-video";
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
    public void setCmsAttachmentManager(
            CmsAttachmentManager cmsAttachmentManager) {
        this.cmsAttachmentManager = cmsAttachmentManager;
    }

    @Resource
    public void setCmsCommentManager(CmsCommentManager cmsCommentManager) {
        this.cmsCommentManager = cmsCommentManager;
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
