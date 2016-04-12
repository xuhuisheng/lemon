package com.mossle.group.web;

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

import com.mossle.group.persistence.domain.GroupInfo;
import com.mossle.group.persistence.manager.GroupInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("group")
public class GroupInfoController {
    private GroupInfoManager groupInfoManager;
    private MessageHelper messageHelper;
    private BeanMapper beanMapper = new BeanMapper();
    private TenantHolder tenantHolder;
    private Exportor exportor;

    @RequestMapping("group-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = groupInfoManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "group/group-info-list";
    }

    @RequestMapping("group-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            GroupInfo groupInfo = groupInfoManager.get(id);
            model.addAttribute("model", groupInfo);
        }

        return "group/group-info-input";
    }

    @RequestMapping("group-info-save")
    public String save(@ModelAttribute GroupInfo groupInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        GroupInfo dest = null;
        Long id = groupInfo.getId();

        if (id != null) {
            dest = groupInfoManager.get(id);
            beanMapper.copy(groupInfo, dest);
        } else {
            dest = groupInfo;
            dest.setTenantId(tenantId);
        }

        groupInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/group/group-info-list.do";
    }

    @RequestMapping("group-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<GroupInfo> groupInfos = groupInfoManager.findByIds(selectedItem);

        for (GroupInfo groupInfo : groupInfos) {
            groupInfoManager.remove(groupInfo);
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/group/group-info-list.do";
    }

    @RequestMapping("group-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = groupInfoManager.pagedQuery(page, propertyFilters);

        List<GroupInfo> groupInfos = (List<GroupInfo>) page.getResult();
        TableModel tableModel = new TableModel();
        tableModel.setName("group info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(groupInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setGroupInfoManager(GroupInfoManager groupInfoManager) {
        this.groupInfoManager = groupInfoManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }
}
