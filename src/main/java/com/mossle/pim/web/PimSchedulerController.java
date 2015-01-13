package com.mossle.pim.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.api.scope.ScopeHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.auth.CurrentUserHolder;
import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;

import com.mossle.pim.domain.PimScheduler;
import com.mossle.pim.domain.PimSchedulerParticipant;
import com.mossle.pim.manager.PimSchedulerManager;
import com.mossle.pim.manager.PimSchedulerParticipantManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("pim")
public class PimSchedulerController {
    private PimSchedulerManager pimSchedulerManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private CurrentUserHolder currentUserHolder;

    @RequestMapping("pim-scheduler-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);

        String userId = currentUserHolder.getUserId();
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page = pimSchedulerManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "pim/pim-scheduler-list";
    }

    @RequestMapping("pim-scheduler-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            PimScheduler pimScheduler = pimSchedulerManager.get(id);
            model.addAttribute("model", pimScheduler);
        }

        return "pim/pim-scheduler-input";
    }

    @RequestMapping("pim-scheduler-save")
    public String save(@ModelAttribute PimScheduler pimScheduler,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        PimScheduler dest = null;

        Long id = pimScheduler.getId();

        if (id != null) {
            dest = pimSchedulerManager.get(id);
            beanMapper.copy(pimScheduler, dest);
        } else {
            dest = pimScheduler;

            String userId = currentUserHolder.getUserId();
            dest.setUserId(userId);
        }

        pimSchedulerManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/pim/pim-scheduler-list.do";
    }

    @RequestMapping("pim-scheduler-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<PimScheduler> pimSchedulers = pimSchedulerManager
                .findByIds(selectedItem);

        pimSchedulerManager.removeAll(pimSchedulers);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/pim/pim-scheduler-list.do";
    }

    @RequestMapping("pim-scheduler-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = pimSchedulerManager.pagedQuery(page, propertyFilters);

        List<PimScheduler> pimSchedulers = (List<PimScheduler>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("pim scheduler");
        tableModel.addHeaders("id", "name");
        tableModel.setData(pimSchedulers);
        exportor.export(response, tableModel);
    }

    @RequestMapping("pim-scheduler-view")
    public String view(Model model) {
        return "pim/pim-scheduler-view";
    }

    // ~ ======================================================================
    @Resource
    public void setPimSchedulerManager(PimSchedulerManager pimSchedulerManager) {
        this.pimSchedulerManager = pimSchedulerManager;
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
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }
}
