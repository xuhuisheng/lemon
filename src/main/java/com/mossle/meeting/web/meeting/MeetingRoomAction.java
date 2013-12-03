package com.mossle.meeting.web.meeting;

import java.io.*;
import java.io.File;

import java.util.ArrayList;
import java.util.List;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.struts2.BaseAction;

import com.mossle.meeting.domain.MeetingRoom;
import com.mossle.meeting.manager.MeetingRoomManager;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = MeetingRoomAction.RELOAD, location = "meeting-room.do?operationMode=RETRIEVE", type = "redirect") })
public class MeetingRoomAction extends BaseAction implements
        ModelDriven<MeetingRoom>, Preparable {
    public static final String RELOAD = "reload";
    private MeetingRoomManager meetingRoomManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private MeetingRoom model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = meetingRoomManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new MeetingRoom();
    }

    public String save() throws Exception {
        MeetingRoom dest = null;

        if (id > 0) {
            dest = meetingRoomManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;
        }

        meetingRoomManager.save(dest);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        List<MeetingRoom> meetingRooms = meetingRoomManager
                .findByIds(selectedItem);

        meetingRoomManager.removeAll(meetingRooms);
        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = meetingRoomManager.get(id);
        }

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = meetingRoomManager.pagedQuery(page, propertyFilters);

        List<MeetingRoom> meetingRooms = (List<MeetingRoom>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("doc info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(meetingRooms);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public MeetingRoom getModel() {
        return model;
    }

    public void setMeetingRoomManager(MeetingRoomManager meetingRoomManager) {
        this.meetingRoomManager = meetingRoomManager;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    // ~ ======================================================================
    public void setId(int id) {
        this.id = id;
    }

    public Page getPage() {
        return page;
    }

    public void setSelectedItem(List<Long> selectedItem) {
        this.selectedItem = selectedItem;
    }
}
