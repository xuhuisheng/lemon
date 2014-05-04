package com.mossle.msg.web;

import java.util.ArrayList;
import java.util.Date;
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

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;

import com.mossle.msg.domain.MsgInfo;
import com.mossle.msg.manager.MsgInfoManager;

import com.mossle.security.util.SpringSecurityUtils;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("msg")
public class MsgInfoController {
    private MsgInfoManager msgInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;

    @RequestMapping("msg-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_senderUsername",
                SpringSecurityUtils.getCurrentUsername()));
        page = msgInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "msg/msg-info-list";
    }

    @RequestMapping("msg-info-listReceived")
    public String listReceived(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_receiverUsername",
                SpringSecurityUtils.getCurrentUsername()));
        page = msgInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "msg/msg-info-list";
    }

    @RequestMapping("msg-info-listSent")
    public String listSent(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);

        propertyFilters.add(new PropertyFilter("EQS_senderUsername",
                SpringSecurityUtils.getCurrentUsername()));
        page = msgInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "msg/msg-info-list";
    }

    @RequestMapping("msg-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            MsgInfo msgInfo = msgInfoManager.get(id);

            model.addAttribute("model", msgInfo);
        }

        return "msg/msg-info-input";
    }

    @RequestMapping("msg-info-save")
    public String save(@ModelAttribute MsgInfo msgInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        MsgInfo dest = null;
        Long id = msgInfo.getId();

        if (id != null) {
            dest = msgInfoManager.get(id);
            beanMapper.copy(msgInfo, dest);
        } else {
            dest = msgInfo;

            String username = SpringSecurityUtils.getCurrentUsername();
            dest.setSenderUsername(username);
            dest.setCreateTime(new Date());
            dest.setStatus(0);
        }

        msgInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/msg/msg-info-list.do";
    }

    @RequestMapping("msg-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<MsgInfo> msgInfos = msgInfoManager.findByIds(selectedItem);

        msgInfoManager.removeAll(msgInfos);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/msg/msg-info-list.do";
    }

    @RequestMapping("msg-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = msgInfoManager.pagedQuery(page, propertyFilters);

        List<MsgInfo> msgInfos = (List<MsgInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("msg info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(msgInfos);
        exportor.export(response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setMsgInfoManager(MsgInfoManager msgInfoManager) {
        this.msgInfoManager = msgInfoManager;
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
