package com.mossle.meeting.web;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;

import com.mossle.meeting.domain.MeetingRoom;
import com.mossle.meeting.manager.MeetingRoomManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("meeting")
public class MeetingRoomController {
    private MeetingRoomManager meetingRoomManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;

    @RequestMapping("meeting-room-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
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
        MeetingRoom dest = null;
        Long id = meetingRoom.getId();

        if (id != null) {
            dest = meetingRoomManager.get(id);
            beanMapper.copy(meetingRoom, dest);
        } else {
            dest = meetingRoom;
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

        meetingRoomManager.removeAll(meetingRooms);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/meeting/meeting-room-list.do";
    }

    @RequestMapping("meeting-room-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = meetingRoomManager.pagedQuery(page, propertyFilters);

        List<MeetingRoom> meetingRooms = (List<MeetingRoom>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("doc info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(meetingRooms);
        exportor.export(response, tableModel);
    }

    // ~ ======================================================================
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
