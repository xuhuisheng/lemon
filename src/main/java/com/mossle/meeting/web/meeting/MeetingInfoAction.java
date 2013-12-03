package com.mossle.meeting.web.meeting;

import java.io.*;
import java.io.File;

import java.util.ArrayList;
import java.util.List;

import com.mossle.api.UserConnector;
import com.mossle.api.UserDTO;
import com.mossle.api.scope.ScopeConnector;
import com.mossle.api.scope.ScopeHolder;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.struts2.BaseAction;
import com.mossle.core.util.IoUtils;

import com.mossle.meeting.domain.MeetingInfo;
import com.mossle.meeting.domain.MeetingRoom;
import com.mossle.meeting.manager.MeetingInfoManager;
import com.mossle.meeting.manager.MeetingRoomManager;

import com.mossle.security.util.SpringSecurityUtils;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = MeetingInfoAction.RELOAD, location = "meeting-info.do?operationMode=RETRIEVE", type = "redirect") })
public class MeetingInfoAction extends BaseAction implements
        ModelDriven<MeetingInfo>, Preparable {
    public static final String RELOAD = "reload";
    private MeetingInfoManager meetingInfoManager;
    private MeetingRoomManager meetingRoomManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private MeetingInfo model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private ScopeConnector scopeConnector;
    private Long meetingRoomId;
    private List<MeetingRoom> meetingRooms;

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = meetingInfoManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new MeetingInfo();
    }

    public String save() throws Exception {
        MeetingInfo dest = null;

        if (id > 0) {
            dest = meetingInfoManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;
            dest.setMeetingRoom(meetingRoomManager.get(meetingRoomId));
        }

        meetingInfoManager.save(dest);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        List<MeetingInfo> meetingInfos = meetingInfoManager
                .findByIds(selectedItem);

        meetingInfoManager.removeAll(meetingInfos);
        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = meetingInfoManager.get(id);
        }

        meetingRooms = meetingRoomManager.getAll();

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = meetingInfoManager.pagedQuery(page, propertyFilters);

        List<MeetingInfo> meetingInfos = (List<MeetingInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("doc info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(meetingInfos);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public MeetingInfo getModel() {
        return model;
    }

    public void setMeetingInfoManager(MeetingInfoManager meetingInfoManager) {
        this.meetingInfoManager = meetingInfoManager;
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

    public Long getMeetingRoomId() {
        return meetingRoomId;
    }

    public void setMeetingRoomId(Long meetingRoomId) {
        this.meetingRoomId = meetingRoomId;
    }

    public List<MeetingRoom> getMeetingRooms() {
        return meetingRooms;
    }
}
