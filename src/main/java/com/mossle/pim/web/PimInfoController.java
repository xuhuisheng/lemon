package com.mossle.pim.web;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.util.ServletUtils;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;

import com.mossle.pim.domain.PimInfo;
import com.mossle.pim.manager.PimInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pim")
public class PimInfoController {
    private PimInfoManager pimInfoManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("pim-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
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
        Long id = pimInfo.getId();
        PimInfo dest = null;

        if (id != null) {
            dest = pimInfoManager.get(id);
            beanMapper.copy(pimInfo, dest);
        } else {
            dest = pimInfo;
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
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = pimInfoManager.pagedQuery(page, propertyFilters);

        List<PimInfo> pimInfos = (List<PimInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("pim info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(pimInfos);
        exportor.export(response, tableModel);
    }

    @RequestMapping("pim-info-vcard")
    public void vcard(@RequestParam("id") Long id, HttpServletResponse response)
            throws Exception {
        PimInfo pimInfo = pimInfoManager.get(id);
        response.setContentType("text/vcard");
        ServletUtils.setFileDownloadHeader(response, "vcard.vcf");

        String text = "BEGIN:VCARD\n" + "VERSION:2.1\n" + "FN:"
                + pimInfo.getName() + "\n" + "TEL;WORK;VOICE:"
                + pimInfo.getTel() + "\n" + "EMAIL;PREF;INTERNET:"
                + pimInfo.getEmail() + "\n" + "END:VCARD";
        response.getWriter().write(text);
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
}
