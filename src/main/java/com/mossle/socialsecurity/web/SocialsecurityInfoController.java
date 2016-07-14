package com.mossle.socialsecurity.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.socialsecurity.persistence.domain.SocialsecurityInfo;
import com.mossle.socialsecurity.persistence.manager.SocialsecurityInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("socialsecurity")
public class SocialsecurityInfoController {
    private SocialsecurityInfoManager socialsecurityInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("socialsecurity-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = socialsecurityInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "socialsecurity/socialsecurity-info-list";
    }

    @RequestMapping("socialsecurity-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            SocialsecurityInfo socialsecurityInfo = socialsecurityInfoManager
                    .get(id);
            model.addAttribute("model", socialsecurityInfo);
        }

        return "socialsecurity/socialsecurity-info-input";
    }

    @RequestMapping("socialsecurity-info-save")
    public String save(@ModelAttribute SocialsecurityInfo socialsecurityInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        SocialsecurityInfo dest = null;

        Long id = socialsecurityInfo.getId();

        if (id != null) {
            dest = socialsecurityInfoManager.get(id);
            beanMapper.copy(socialsecurityInfo, dest);
        } else {
            dest = socialsecurityInfo;
            dest.setTenantId(tenantId);
        }

        socialsecurityInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/socialsecurity/socialsecurity-info-list.do";
    }

    @RequestMapping("socialsecurity-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<SocialsecurityInfo> socialsecurityInfos = socialsecurityInfoManager
                .findByIds(selectedItem);

        socialsecurityInfoManager.removeAll(socialsecurityInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/socialsecurity/socialsecurity-info-list.do";
    }

    @RequestMapping("socialsecurity-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = socialsecurityInfoManager.pagedQuery(page, propertyFilters);

        List<SocialsecurityInfo> socialsecurityInfos = (List<SocialsecurityInfo>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("socialsecurity info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(socialsecurityInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setSocialsecurityInfoManager(
            SocialsecurityInfoManager socialsecurityInfoManager) {
        this.socialsecurityInfoManager = socialsecurityInfoManager;
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

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
