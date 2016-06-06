package com.mossle.internal.sendsms.web;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.util.StringUtils;

import com.mossle.internal.sendsms.persistence.domain.SendsmsConfig;
import com.mossle.internal.sendsms.persistence.domain.SendsmsQueue;
import com.mossle.internal.sendsms.persistence.manager.SendsmsConfigManager;
import com.mossle.internal.sendsms.persistence.manager.SendsmsQueueManager;
import com.mossle.internal.sendsms.support.SmsConnector;
import com.mossle.internal.sendsms.support.SmsDTO;
import com.mossle.internal.sendsms.support.SmsServerInfo;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sendsms")
public class SendsmsQueueController {
    private SendsmsQueueManager sendsmsQueueManager;
    private SendsmsConfigManager sendsmsConfigManager;
    private SmsConnector smsConnector;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("sendsms-queue-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        page.setDefaultOrder("createTime", Page.DESC);

        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = sendsmsQueueManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "sendsms/sendsms-queue-list";
    }

    @RequestMapping("sendsms-queue-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            SendsmsQueue sendsmsQueue = sendsmsQueueManager.get(id);
            model.addAttribute("model", sendsmsQueue);
        }

        model.addAttribute("sendsmsConfigs", sendsmsConfigManager.getAll());

        return "sendsms/sendsms-queue-input";
    }

    @RequestMapping("sendsms-queue-save")
    public String save(@ModelAttribute SendsmsQueue sendsmsQueue,
            @RequestParam("sendsmsConfigId") Long sendsmsConfigId,
            RedirectAttributes redirectAttributes) {
        Long id = sendsmsQueue.getId();
        SendsmsQueue dest = null;

        if (id != null) {
            dest = sendsmsQueueManager.get(id);
            beanMapper.copy(sendsmsQueue, dest);
        } else {
            dest = sendsmsQueue;
            dest.setCreateTime(new Date());
        }

        dest.setSendsmsConfig(sendsmsConfigManager.get(sendsmsConfigId));

        sendsmsQueueManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/sendsms/sendsms-queue-list.do";
    }

    @RequestMapping("sendsms-queue-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<SendsmsQueue> sendsmsQueues = sendsmsQueueManager
                .findByIds(selectedItem);
        sendsmsQueueManager.removeAll(sendsmsQueues);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/sendsms/sendsms-queue-list.do";
    }

    @RequestMapping("sendsms-queue-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = sendsmsQueueManager.pagedQuery(page, propertyFilters);

        List<SendsmsQueue> sendsmsQueues = (List<SendsmsQueue>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("sendsms config");
        tableModel.addHeaders("id", "name");
        tableModel.setData(sendsmsQueues);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setSendsmsQueueManager(SendsmsQueueManager sendsmsQueueManager) {
        this.sendsmsQueueManager = sendsmsQueueManager;
    }

    @Resource
    public void setSendsmsConfigManager(
            SendsmsConfigManager sendsmsConfigManager) {
        this.sendsmsConfigManager = sendsmsConfigManager;
    }

    @Resource
    public void setSmsConnector(SmsConnector smsConnector) {
        this.smsConnector = smsConnector;
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
