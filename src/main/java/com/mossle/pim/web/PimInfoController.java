package com.mossle.pim.web;

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
import com.mossle.core.util.ServletUtils;

import com.mossle.pim.persistence.domain.PimInfo;
import com.mossle.pim.persistence.manager.PimInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("pim")
public class PimInfoController {
    private PimInfoManager pimInfoManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private CurrentUserHolder currentUserHolder;
    private TenantHolder tenantHolder;

    @RequestMapping("pim-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page = pimInfoManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "pim/pim-info-list";
    }

    @RequestMapping("pim-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            PimInfo pimInfo = pimInfoManager.get(id);
            model.addAttribute("model", pimInfo);
        }

        return "pim/pim-info-input";
    }

    @RequestMapping("pim-info-save")
    public String save(@ModelAttribute PimInfo pimInfo,
            RedirectAttributes redirectAttributes) {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();
        Long id = pimInfo.getId();
        PimInfo dest = null;

        if (id != null) {
            dest = pimInfoManager.get(id);
            beanMapper.copy(pimInfo, dest);
        } else {
            dest = pimInfo;
            dest.setUserId(userId);
            dest.setTenantId(tenantId);
        }

        pimInfoManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/pim/pim-info-list.do";
    }

    @RequestMapping("pim-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<PimInfo> pimInfos = pimInfoManager.findByIds(selectedItem);
        pimInfoManager.removeAll(pimInfos);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/pim/pim-info-list.do";
    }

    @RequestMapping("pim-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page = pimInfoManager.pagedQuery(page, propertyFilters);

        List<PimInfo> pimInfos = (List<PimInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("pim info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(pimInfos);
        exportor.export(request, response, tableModel);
    }

    @RequestMapping("pim-info-vcard")
    public void vcard(@RequestParam("id") Long id, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        PimInfo pimInfo = pimInfoManager.get(id);
        response.setContentType("text/vcard");
        ServletUtils.setFileDownloadHeader(request, response, "vcard.vcf");

        StringBuilder buff = new StringBuilder();
        buff.append("BEGIN:VCARD\n");
        buff.append("VERSION:2.1\n");
        buff.append("FN;CHARSET=UTF8:" + pimInfo.getName() + "\n");
        buff.append("ORG;CHARSET=UTF8:" + pimInfo.getOrg() + ";"
                + pimInfo.getDepartment() + "\n");
        buff.append("TITLE;CHARSET=UTF8:" + pimInfo.getTitle() + "\n");
        buff.append("TEL;WORK;VOICE:" + pimInfo.getTel() + "\n");
        buff.append("EMAIL;PREF;INTERNET:" + pimInfo.getEmail() + "\n");
        buff.append("IMPP:" + pimInfo.getImpp() + "\n");
        buff.append("END:VCARD");

        response.getWriter().write(buff.toString());
    }

    // ~ ======================================================================
    @Resource
    public void setPimInfoManager(PimInfoManager pimInfoManager) {
        this.pimInfoManager = pimInfoManager;
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
