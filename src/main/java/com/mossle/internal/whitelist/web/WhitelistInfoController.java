package com.mossle.internal.whitelist.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.internal.whitelist.persistence.domain.WhitelistInfo;
import com.mossle.internal.whitelist.persistence.manager.WhitelistInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("whitelist")
public class WhitelistInfoController {
    private WhitelistInfoManager whitelistInfoManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private TenantHolder tenantHolder;

    @RequestMapping("whitelist-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        // propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = whitelistInfoManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "whitelist/whitelist-info-list";
    }

    @RequestMapping("whitelist-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            WhitelistInfo whitelistInfo = whitelistInfoManager.get(id);
            model.addAttribute("model", whitelistInfo);
        }

        return "whitelist/whitelist-info-input";
    }

    @RequestMapping("whitelist-info-save")
    public String save(@ModelAttribute WhitelistInfo whitelistInfo,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        Long id = whitelistInfo.getId();
        WhitelistInfo dest = null;

        if (id != null) {
            dest = whitelistInfoManager.get(id);
            beanMapper.copy(whitelistInfo, dest);
        } else {
            dest = whitelistInfo;

            // dest.setTenantId(tenantId);
        }

        whitelistInfoManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/whitelist/whitelist-info-list.do";
    }

    @RequestMapping("whitelist-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<WhitelistInfo> whitelistInfos = whitelistInfoManager
                .findByIds(selectedItem);
        whitelistInfoManager.removeAll(whitelistInfos);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/whitelist/whitelist-info-list.do";
    }

    @RequestMapping("whitelist-info-export")
    public void export(@ModelAttribute Page page, HttpServletRequest request,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = whitelistInfoManager.pagedQuery(page, propertyFilters);

        List<WhitelistInfo> whitelistInfos = (List<WhitelistInfo>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("whitelistInfo");
        tableModel.addHeaders("id", "name");
        tableModel.setData(whitelistInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setWhitelistInfoManager(
            WhitelistInfoManager whitelistInfoManager) {
        this.whitelistInfoManager = whitelistInfoManager;
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
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
