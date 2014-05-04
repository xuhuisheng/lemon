package com.mossle.cms.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.api.scope.ScopeHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.cms.domain.CmsArticle;
import com.mossle.cms.manager.CmsArticleManager;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;

import com.mossle.security.util.SpringSecurityUtils;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("cms")
public class CmsArticleController {
    private CmsArticleManager cmsArticleManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;

    @RequestMapping("cms-article-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);

        String userId = userConnector.findByUsername(
                SpringSecurityUtils.getCurrentUsername(),
                ScopeHolder.getUserRepoRef()).getId();
        propertyFilters.add(new PropertyFilter("EQL_userId", userId));
        page = cmsArticleManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "cms/cms-article-list";
    }

    @RequestMapping("cms-article-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            CmsArticle cmsArticle = cmsArticleManager.get(id);
            model.addAttribute("model", cmsArticle);
        }

        return "cms/cms-article-input";
    }

    @RequestMapping("cms-article-save")
    public String save(@ModelAttribute CmsArticle cmsArticle,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        CmsArticle dest = null;
        Long id = cmsArticle.getId();

        if (id != null) {
            dest = cmsArticleManager.get(id);
            beanMapper.copy(cmsArticle, dest);
        } else {
            dest = cmsArticle;

            String userId = SpringSecurityUtils.getCurrentUserId();
            dest.setUserId(Long.parseLong(userId));
            dest.setCreateTime(new Date());
        }

        cmsArticleManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/cms/cms-article-list.do";
    }

    @RequestMapping("cms-article-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<CmsArticle> cmsArticles = cmsArticleManager
                .findByIds(selectedItem);

        cmsArticleManager.removeAll(cmsArticles);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/cms/cms-article-list.do";
    }

    @RequestMapping("cms-article-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = cmsArticleManager.pagedQuery(page, propertyFilters);

        List<CmsArticle> cmsArticles = (List<CmsArticle>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("cal info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(cmsArticles);
        exportor.export(response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setCmsArticleManager(CmsArticleManager cmsArticleManager) {
        this.cmsArticleManager = cmsArticleManager;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }
}
