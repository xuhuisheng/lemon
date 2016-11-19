package com.mossle.meeting.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantConnector;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.meeting.persistence.domain.MeetingAttendee;
import com.mossle.meeting.persistence.domain.MeetingInfo;
import com.mossle.meeting.persistence.domain.MeetingItem;
import com.mossle.meeting.persistence.domain.MeetingRoom;
import com.mossle.meeting.persistence.manager.MeetingAttendeeManager;
import com.mossle.meeting.persistence.manager.MeetingInfoManager;
import com.mossle.meeting.persistence.manager.MeetingItemManager;
import com.mossle.meeting.persistence.manager.MeetingRoomManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("meeting")
public class MeetingInfoController {
    private static Logger logger = LoggerFactory
            .getLogger(MeetingInfoController.class);
    private MeetingInfoManager meetingInfoManager;
    private MeetingRoomManager meetingRoomManager;
    private MeetingItemManager meetingItemManager;
    private MeetingAttendeeManager meetingAttendeeManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private TenantConnector tenantConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("meeting-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
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

            // organizerName
            UserDTO userDto = userConnector
                    .findById(meetingInfo.getOrganizer());
            model.addAttribute("organizerName", userDto.getDisplayName());

            // attendees
            List<String> attendees = new ArrayList<String>();

            for (MeetingAttendee meetingAttendee : meetingInfo
                    .getMeetingAttendees()) {
                UserDTO user = userConnector.findById(meetingAttendee
                        .getUserId());
                attendees.add(user.getUsername());
            }

            model.addAttribute("attendeeNames",
                    StringUtils.join(attendees, ","));

            // items
            List<String> items = new ArrayList<String>();

            for (MeetingItem meetingItem : meetingInfo.getMeetingItems()) {
                items.add(meetingItem.getName());
            }

            model.addAttribute("items", items);
        }

        List<MeetingRoom> meetingRooms = meetingRoomManager.getAll();
        model.addAttribute("meetingRooms", meetingRooms);

        return "meeting/meeting-info-input";
    }

    @RequestMapping("meeting-info-save")
    public String save(
            @ModelAttribute MeetingInfo meetingInfo,
            @RequestParam("meetingRoomId") Long meetingRoomId,
            @RequestParam("attendees") String attendees,
            @RequestParam(value = "items", required = false) List<String> items,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        MeetingInfo dest = null;

        Long id = meetingInfo.getId();

        if (id != null) {
            dest = meetingInfoManager.get(id);
            beanMapper.copy(meetingInfo, dest);
        } else {
            dest = meetingInfo;
            dest.setMeetingRoom(meetingRoomManager.get(meetingRoomId));
            dest.setTenantId(tenantId);
        }

        meetingInfoManager.save(dest);

        meetingInfoManager.removeAll(dest.getMeetingAttendees());

        for (String attendee : attendees.split(",")) {
            MeetingAttendee meetingAttendee = new MeetingAttendee();
            UserDTO userDto = userConnector
                    .findByUsername(attendee.trim(), "1");

            if (userDto == null) {
                logger.info("cannot find attendee : {}", attendee.trim());

                continue;
            }

            meetingAttendee.setUserId(userDto.getId());
            meetingAttendee.setMeetingInfo(dest);
            meetingInfoManager.save(meetingAttendee);
        }

        meetingInfoManager.removeAll(dest.getMeetingItems());

        if (items != null) {
            for (String item : items) {
                MeetingItem meetingItem = new MeetingItem();
                meetingItem.setName(item);
                meetingItem.setMeetingInfo(dest);
                meetingInfoManager.save(meetingItem);
            }
        }

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/meeting/meeting-info-list.do";
    }

    @RequestMapping("meeting-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<MeetingInfo> meetingInfos = meetingInfoManager
                .findByIds(selectedItem);

        for (MeetingInfo meetingInfo : meetingInfos) {
            meetingAttendeeManager.removeAll(meetingInfo.getMeetingAttendees());
            meetingItemManager.removeAll(meetingInfo.getMeetingItems());
            meetingInfoManager.remove(meetingInfo);
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/meeting/meeting-info-list.do";
    }

    @RequestMapping("meeting-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = meetingInfoManager.pagedQuery(page, propertyFilters);

        List<MeetingInfo> meetingInfos = (List<MeetingInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("doc info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(meetingInfos);
        exportor.export(request, response, tableModel);
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
    public void setMeetingItemManager(MeetingItemManager meetingItemManager) {
        this.meetingItemManager = meetingItemManager;
    }

    @Resource
    public void setMeetingAttendeeManager(
            MeetingAttendeeManager meetingAttendeeManager) {
        this.meetingAttendeeManager = meetingAttendeeManager;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
