package com.mossle.cms.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.cms.persistence.domain.CmsComment;
import com.mossle.cms.persistence.manager.CmsCommentManager;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cms")
public class CmsCommentController {
    private CmsCommentManager cmsCommentManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;

    @RequestMapping("cms-comment-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = cmsCommentManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "cms/cms-comment-list";
    }

    @RequestMapping("cms-comment-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            CmsComment cmsComment = cmsCommentManager.get(id);
            model.addAttribute("model", cmsComment);
        }

        return "cms/cms-comment-input";
    }

    @RequestMapping("cms-comment-save")
    public String save(@ModelAttribute CmsComment cmsComment,
            RedirectAttributes redirectAttributes) {
        Long id = cmsComment.getId();
        CmsComment dest = null;

        if (id != null) {
            dest = cmsCommentManager.get(id);
            beanMapper.copy(cmsComment, dest);
        } else {
            dest = cmsComment;
        }

        cmsCommentManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/cms/cms-comment-list.do";
    }

    @RequestMapping("cms-comment-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<CmsComment> cmsComments = cmsCommentManager
                .findByIds(selectedItem);
        cmsCommentManager.removeAll(cmsComments);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/cms/cms-comment-list.do";
    }

    @RequestMapping("cms-comment-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = cmsCommentManager.pagedQuery(page, propertyFilters);

        List<CmsComment> cmsComments = (List<CmsComment>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("cmsComment");
        tableModel.addHeaders("id", "name");
        tableModel.setData(cmsComments);
        exportor.export(request, response, tableModel);
    }

    @RequestMapping("cms-comment-checkName")
    @ResponseBody
    public boolean checkName(@RequestParam("name") String name,
            @RequestParam(value = "id", required = false) Long id)
            throws Exception {
        String hql = "from CmsComment where name=?";
        Object[] params = { name };

        if (id != null) {
            hql = "from CmsComment where name=? and id<>?";
            params = new Object[] { name, id };
        }

        CmsComment cmsComment = cmsCommentManager.findUnique(hql, params);

        boolean result = (cmsComment == null);

        return result;
    }

    // ~ ======================================================================
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
}
