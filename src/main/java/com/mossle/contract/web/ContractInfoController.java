package com.mossle.contract.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.contract.persistence.domain.ContractInfo;
import com.mossle.contract.persistence.manager.ContractInfoManager;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("contract")
public class ContractInfoController {
    private ContractInfoManager contractInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("contract-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = contractInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "contract/contract-info-list";
    }

    @RequestMapping("contract-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            ContractInfo contractInfo = contractInfoManager.get(id);
            model.addAttribute("model", contractInfo);
        }

        return "contract/contract-info-input";
    }

    @RequestMapping("contract-info-save")
    public String save(@ModelAttribute ContractInfo contractInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        ContractInfo dest = null;

        Long id = contractInfo.getId();

        if (id != null) {
            dest = contractInfoManager.get(id);
            beanMapper.copy(contractInfo, dest);
        } else {
            dest = contractInfo;
            dest.setTenantId(tenantId);
        }

        contractInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/contract/contract-info-list.do";
    }

    @RequestMapping("contract-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<ContractInfo> contractInfos = contractInfoManager
                .findByIds(selectedItem);

        contractInfoManager.removeAll(contractInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/contract/contract-info-list.do";
    }

    @RequestMapping("contract-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = contractInfoManager.pagedQuery(page, propertyFilters);

        List<ContractInfo> contractInfos = (List<ContractInfo>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("contract info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(contractInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setContractInfoManager(ContractInfoManager contractInfoManager) {
        this.contractInfoManager = contractInfoManager;
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
