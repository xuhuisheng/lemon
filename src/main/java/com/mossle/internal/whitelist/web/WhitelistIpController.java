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

import com.mossle.internal.whitelist.persistence.domain.WhitelistIp;
import com.mossle.internal.whitelist.persistence.manager.WhitelistIpManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("whitelist")
public class WhitelistIpController {
    private WhitelistIpManager whitelistIpManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private TenantHolder tenantHolder;

    @RequestMapping("whitelist-ip-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = whitelistIpManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "whitelist/whitelist-ip-list";
    }

    @RequestMapping("whitelist-ip-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            WhitelistIp whitelistIp = whitelistIpManager.get(id);
            model.addAttribute("model", whitelistIp);
        }

        return "whitelist/whitelist-ip-input";
    }

    @RequestMapping("whitelist-ip-save")
    public String save(@ModelAttribute WhitelistIp whitelistIp,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        Long id = whitelistIp.getId();
        WhitelistIp dest = null;

        if (id != null) {
            dest = whitelistIpManager.get(id);
            beanMapper.copy(whitelistIp, dest);
        } else {
            dest = whitelistIp;
            dest.setTenantId(tenantId);
        }

        whitelistIpManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/whitelist/whitelist-ip-list.do";
    }

    @RequestMapping("whitelist-ip-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<WhitelistIp> whitelistIps = whitelistIpManager
                .findByIds(selectedItem);
        whitelistIpManager.removeAll(whitelistIps);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/whitelist/whitelist-ip-list.do";
    }

    @RequestMapping("whitelist-ip-export")
    public void export(@ModelAttribute Page page, HttpServletRequest request,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = whitelistIpManager.pagedQuery(page, propertyFilters);

        List<WhitelistIp> whitelistIps = (List<WhitelistIp>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("whitelistIp");
        tableModel.addHeaders("id", "name");
        tableModel.setData(whitelistIps);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setWhitelistIpManager(WhitelistIpManager whitelistIpManager) {
        this.whitelistIpManager = whitelistIpManager;
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
