package com.mossle.meeting.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.meeting.persistence.domain.MeetingRoom;
import com.mossle.meeting.persistence.manager.MeetingInfoManager;
import com.mossle.meeting.persistence.manager.MeetingRoomManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("meeting")
public class MeetingRoomController {
    private MeetingRoomManager meetingRoomManager;
    private MeetingInfoManager meetingInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("meeting-room-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = meetingRoomManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "meeting/meeting-room-list";
    }

    @RequestMapping("meeting-room-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            MeetingRoom meetingRoom = meetingRoomManager.get(id);
            model.addAttribute("model", meetingRoom);
        }

        return "meeting/meeting-room-input";
    }

    @RequestMapping("meeting-room-save")
    public String save(@ModelAttribute MeetingRoom meetingRoom,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        MeetingRoom dest = null;
        Long id = meetingRoom.getId();

        if (id != null) {
            dest = meetingRoomManager.get(id);
            beanMapper.copy(meetingRoom, dest);
        } else {
            dest = meetingRoom;
            dest.setTenantId(tenantId);
        }

        meetingRoomManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/meeting/meeting-room-list.do";
    }

    @RequestMapping("meeting-room-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<MeetingRoom> meetingRooms = meetingRoomManager
                .findByIds(selectedItem);

        for (MeetingRoom meetingRoom : meetingRooms) {
            meetingInfoManager.removeAll(meetingRoom.getMeetingInfos());
            meetingRoomManager.remove(meetingRoom);
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/meeting/meeting-room-list.do";
    }

    @RequestMapping("meeting-room-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = meetingRoomManager.pagedQuery(page, propertyFilters);

        List<MeetingRoom> meetingRooms = (List<MeetingRoom>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("doc info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(meetingRooms);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setMeetingRoomManager(MeetingRoomManager meetingRoomManager) {
        this.meetingRoomManager = meetingRoomManager;
    }

    @Resource
    public void setMeetingInfoManager(MeetingInfoManager meetingInfoManager) {
        this.meetingInfoManager = meetingInfoManager;
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
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
