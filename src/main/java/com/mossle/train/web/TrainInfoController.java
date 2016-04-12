package com.mossle.train.web;

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

import com.mossle.train.persistence.domain.TrainInfo;
import com.mossle.train.persistence.manager.TrainInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("train")
public class TrainInfoController {
    private TrainInfoManager trainInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("train-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = trainInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "train/train-info-list";
    }

    @RequestMapping("train-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            TrainInfo trainInfo = trainInfoManager.get(id);
            model.addAttribute("model", trainInfo);
        }

        return "train/train-info-input";
    }

    @RequestMapping("train-info-save")
    public String save(@ModelAttribute TrainInfo trainInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        TrainInfo dest = null;

        Long id = trainInfo.getId();

        if (id != null) {
            dest = trainInfoManager.get(id);
            beanMapper.copy(trainInfo, dest);
        } else {
            dest = trainInfo;
            dest.setTenantId(tenantId);
        }

        trainInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/train/train-info-list.do";
    }

    @RequestMapping("train-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<TrainInfo> trainInfos = trainInfoManager.findByIds(selectedItem);

        trainInfoManager.removeAll(trainInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/train/train-info-list.do";
    }

    @RequestMapping("train-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = trainInfoManager.pagedQuery(page, propertyFilters);

        List<TrainInfo> trainInfos = (List<TrainInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("train info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(trainInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setTrainInfoManager(TrainInfoManager trainInfoManager) {
        this.trainInfoManager = trainInfoManager;
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
