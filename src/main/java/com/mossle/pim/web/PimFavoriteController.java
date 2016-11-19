package com.mossle.pim.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.auth.CurrentUserHolder;
import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.pim.persistence.domain.PimFavorite;
import com.mossle.pim.persistence.manager.PimFavoriteManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("pim")
public class PimFavoriteController {
    private PimFavoriteManager pimFavoriteManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private CurrentUserHolder currentUserHolder;
    private TenantHolder tenantHolder;

    @RequestMapping("pim-favorite-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page = pimFavoriteManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "pim/pim-favorite-list";
    }

    @RequestMapping("pim-favorite-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            PimFavorite pimFavorite = pimFavoriteManager.get(id);
            model.addAttribute("model", pimFavorite);
        }

        return "pim/pim-favorite-input";
    }

    @RequestMapping("pim-favorite-save")
    public String save(@ModelAttribute PimFavorite pimFavorite,
            RedirectAttributes redirectAttributes) {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();
        Long id = pimFavorite.getId();
        PimFavorite dest = null;

        if (id != null) {
            dest = pimFavoriteManager.get(id);
            beanMapper.copy(pimFavorite, dest);
        } else {
            dest = pimFavorite;
            dest.setUserId(userId);
            dest.setCreateTime(new Date());
        }

        pimFavoriteManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/pim/pim-favorite-list.do";
    }

    @RequestMapping("pim-favorite-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<PimFavorite> pimFavorites = pimFavoriteManager
                .findByIds(selectedItem);
        pimFavoriteManager.removeAll(pimFavorites);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/pim/pim-favorite-list.do";
    }

    @RequestMapping("pim-favorite-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page = pimFavoriteManager.pagedQuery(page, propertyFilters);

        List<PimFavorite> pimFavorites = (List<PimFavorite>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("pim info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(pimFavorites);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setPimFavoriteManager(PimFavoriteManager pimFavoriteManager) {
        this.pimFavoriteManager = pimFavoriteManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
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
