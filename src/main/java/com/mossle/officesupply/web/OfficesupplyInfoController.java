package com.mossle.officesupply.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.user.UserConnector;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.officesupply.persistence.domain.OfficesupplyInfo;
import com.mossle.officesupply.persistence.manager.OfficesupplyInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("officesupply")
public class OfficesupplyInfoController {
    private OfficesupplyInfoManager officesupplyInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;

    @RequestMapping("officesupply-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = officesupplyInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "officesupply/officesupply-info-list";
    }

    @RequestMapping("officesupply-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            OfficesupplyInfo officesupplyInfo = officesupplyInfoManager.get(id);
            model.addAttribute("model", officesupplyInfo);
        }

        return "officesupply/officesupply-info-input";
    }

    @RequestMapping("officesupply-info-save")
    public String save(@ModelAttribute OfficesupplyInfo officesupplyInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        OfficesupplyInfo dest = null;

        Long id = officesupplyInfo.getId();

        if (id != null) {
            dest = officesupplyInfoManager.get(id);
            beanMapper.copy(officesupplyInfo, dest);
        } else {
            dest = officesupplyInfo;
        }

        officesupplyInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/officesupply/officesupply-info-list.do";
    }

    @RequestMapping("officesupply-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<OfficesupplyInfo> officesupplyInfos = officesupplyInfoManager
                .findByIds(selectedItem);

        officesupplyInfoManager.removeAll(officesupplyInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/officesupply/officesupply-info-list.do";
    }

    @RequestMapping("officesupply-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = officesupplyInfoManager.pagedQuery(page, propertyFilters);

        List<OfficesupplyInfo> officesupplyInfos = (List<OfficesupplyInfo>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("officesupply info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(officesupplyInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setOfficesupplyInfoManager(
            OfficesupplyInfoManager officesupplyInfoManager) {
        this.officesupplyInfoManager = officesupplyInfoManager;
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
}
