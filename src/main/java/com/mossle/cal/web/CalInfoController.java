package com.mossle.cal.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.api.scope.ScopeHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.cal.domain.CalInfo;
import com.mossle.cal.manager.CalInfoManager;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;

import com.mossle.security.util.SpringSecurityUtils;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("cal")
public class CalInfoController {
    private CalInfoManager calInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;

    @RequestMapping("cal-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);

        String userId = SpringSecurityUtils.getCurrentUserId();
        propertyFilters.add(new PropertyFilter("EQL_userId", userId));
        page = calInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "cal/cal-info-list";
    }

    @RequestMapping("cal-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            CalInfo calInfo = calInfoManager.get(id);
            model.addAttribute("model", calInfo);
        }

        return "cal/cal-info-input";
    }

    @RequestMapping("cal-info-save")
    public String save(@ModelAttribute CalInfo calInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        CalInfo dest = null;

        Long id = calInfo.getId();

        if (id != null) {
            dest = calInfoManager.get(id);
            beanMapper.copy(calInfo, dest);
        } else {
            dest = calInfo;

            String userId = SpringSecurityUtils.getCurrentUserId();
            dest.setUserId(Long.parseLong(userId));
        }

        calInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/cal/cal-info-list.do";
    }

    @RequestMapping("cal-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<CalInfo> calInfos = calInfoManager.findByIds(selectedItem);

        calInfoManager.removeAll(calInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/cal/cal-info-list.do";
    }

    @RequestMapping("cal-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = calInfoManager.pagedQuery(page, propertyFilters);

        List<CalInfo> calInfos = (List<CalInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("cal info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(calInfos);
        exportor.export(response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setCalInfoManager(CalInfoManager calInfoManager) {
        this.calInfoManager = calInfoManager;
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
