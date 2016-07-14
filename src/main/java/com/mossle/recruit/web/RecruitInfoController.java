package com.mossle.recruit.web;

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

import com.mossle.recruit.persistence.domain.RecruitInfo;
import com.mossle.recruit.persistence.manager.RecruitInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("recruit")
public class RecruitInfoController {
    private RecruitInfoManager recruitInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("recruit-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = recruitInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "recruit/recruit-info-list";
    }

    @RequestMapping("recruit-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            RecruitInfo recruitInfo = recruitInfoManager.get(id);
            model.addAttribute("model", recruitInfo);
        }

        return "recruit/recruit-info-input";
    }

    @RequestMapping("recruit-info-save")
    public String save(@ModelAttribute RecruitInfo recruitInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        RecruitInfo dest = null;

        Long id = recruitInfo.getId();

        if (id != null) {
            dest = recruitInfoManager.get(id);
            beanMapper.copy(recruitInfo, dest);
        } else {
            dest = recruitInfo;
            dest.setTenantId(tenantId);
        }

        recruitInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/recruit/recruit-info-list.do";
    }

    @RequestMapping("recruit-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<RecruitInfo> recruitInfos = recruitInfoManager
                .findByIds(selectedItem);

        recruitInfoManager.removeAll(recruitInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/recruit/recruit-info-list.do";
    }

    @RequestMapping("recruit-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = recruitInfoManager.pagedQuery(page, propertyFilters);

        List<RecruitInfo> recruitInfos = (List<RecruitInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("recruit info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(recruitInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setRecruitInfoManager(RecruitInfoManager recruitInfoManager) {
        this.recruitInfoManager = recruitInfoManager;
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
