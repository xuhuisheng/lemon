package com.mossle.meeting.web;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.api.scope.ScopeConnector;
import com.mossle.api.scope.ScopeHolder;
import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.util.IoUtils;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;

import com.mossle.meeting.domain.MeetingInfo;
import com.mossle.meeting.domain.MeetingRoom;
import com.mossle.meeting.manager.MeetingInfoManager;
import com.mossle.meeting.manager.MeetingRoomManager;

import com.mossle.security.util.SpringSecurityUtils;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("meeting")
public class MeetingInfoController {
    private MeetingInfoManager meetingInfoManager;
    private MeetingRoomManager meetingRoomManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private ScopeConnector scopeConnector;
    private MessageHelper messageHelper;

    @RequestMapping("meeting-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = meetingInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "meeting/meeting-info-list";
    }

    @RequestMapping("meeting-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            MeetingInfo meetingInfo = meetingInfoManager.get(id);
            model.addAttribute("model", meetingInfo);
        }

        List<MeetingRoom> meetingRooms = meetingRoomManager.getAll();
        model.addAttribute("meetingRooms", meetingRooms);

        return "meeting/meeting-info-input";
    }

    @RequestMapping("meeting-info-save")
    public String save(@ModelAttribute MeetingInfo meetingInfo,
            @RequestParam("meetingRoomId") Long meetingRoomId,
            RedirectAttributes redirectAttributes) {
        MeetingInfo dest = null;

        Long id = meetingInfo.getId();

        if (id != null) {
            dest = meetingInfoManager.get(id);
            beanMapper.copy(meetingInfo, dest);
        } else {
            dest = meetingInfo;
            dest.setMeetingRoom(meetingRoomManager.get(meetingRoomId));
        }

        meetingInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/meeting/meeting-info-list.do";
    }

    @RequestMapping("meeting-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<MeetingInfo> meetingInfos = meetingInfoManager
                .findByIds(selectedItem);

        meetingInfoManager.removeAll(meetingInfos);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/meeting/meeting-info-list.do";
    }

    @RequestMapping("meeting-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = meetingInfoManager.pagedQuery(page, propertyFilters);

        List<MeetingInfo> meetingInfos = (List<MeetingInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("doc info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(meetingInfos);
        exportor.export(response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setMeetingInfoManager(MeetingInfoManager meetingInfoManager) {
        this.meetingInfoManager = meetingInfoManager;
    }

    @Resource
    public void setMeetingRoomManager(MeetingRoomManager meetingRoomManager) {
        this.meetingRoomManager = meetingRoomManager;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }
}
