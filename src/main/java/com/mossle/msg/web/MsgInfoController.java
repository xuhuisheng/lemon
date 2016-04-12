package com.mossle.msg.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;

import com.mossle.core.auth.CurrentUserHolder;
import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.msg.persistence.domain.MsgInfo;
import com.mossle.msg.persistence.manager.MsgInfoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("msg")
public class MsgInfoController {
    private static Logger logger = LoggerFactory
            .getLogger(MsgInfoController.class);
    private MsgInfoManager msgInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private CurrentUserHolder currentUserHolder;

    @RequestMapping("msg-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_senderId", userId));
        page = msgInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "msg/msg-info-list";
    }

    @RequestMapping("msg-info-listReceived")
    public String listReceived(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_receiverId", userId));
        page.setOrder("DESC");
        page.setOrderBy("createTime");
        page = msgInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "msg/msg-info-listReceived";
    }

    @RequestMapping("msg-info-listSent")
    public String listSent(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);

        propertyFilters.add(new PropertyFilter("EQS_senderId", userId));
        page.setOrder("DESC");
        page.setOrderBy("createTime");
        page = msgInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "msg/msg-info-listSent";
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
            @RequestParam("username") String username,
            RedirectAttributes redirectAttributes) {
        MsgInfo dest = null;
        Long id = msgInfo.getId();

        if (id != null) {
            dest = msgInfoManager.get(id);
            beanMapper.copy(msgInfo, dest);
            msgInfoManager.save(dest);
        } else {
            dest = msgInfo;

            String userId = currentUserHolder.getUserId();
            dest.setSenderId(userId);

            for (String theUsername : username.split(",")) {
                MsgInfo theMsgInfo = new MsgInfo();
                beanMapper.copy(msgInfo, theMsgInfo);
                theMsgInfo.setSenderId(userId);

                UserDTO userDto = userConnector
                        .findByUsername(theUsername, "1");

                if (userDto == null) {
                    logger.warn("user not exists : {}", theUsername);

                    continue;
                }

                theMsgInfo.setReceiverId(userDto.getId());
                theMsgInfo.setCreateTime(new Date());
                theMsgInfo.setStatus(0);
                msgInfoManager.save(theMsgInfo);
            }
        }

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/msg/msg-info-listSent.do";
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
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = msgInfoManager.pagedQuery(page, propertyFilters);

        List<MsgInfo> msgInfos = (List<MsgInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("msg info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(msgInfos);
        exportor.export(request, response, tableModel);
    }

    @RequestMapping("msg-info-view")
    public String view(@RequestParam("id") Long id, Model model) {
        String userId = currentUserHolder.getUserId();
        MsgInfo msgInfo = msgInfoManager.get(id);

        if ((msgInfo.getStatus() == 0)
                && userId.equals(msgInfo.getReceiverId())) {
            msgInfo.setStatus(1);
            msgInfoManager.save(msgInfo);
        }

        model.addAttribute("model", msgInfo);

        return "msg/msg-info-view";
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

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }
}
