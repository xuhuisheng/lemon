package com.mossle.msg.web.msg;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mossle.api.UserConnector;
import com.mossle.api.scope.ScopeHolder;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.struts2.BaseAction;

import com.mossle.msg.domain.MsgInfo;
import com.mossle.msg.manager.MsgInfoManager;

import com.mossle.security.util.SpringSecurityUtils;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = MsgInfoAction.RELOAD, location = "msg-info.do?operationMode=RETRIEVE", type = "redirect") })
public class MsgInfoAction extends BaseAction implements ModelDriven<MsgInfo>,
        Preparable {
    public static final String RELOAD = "reload";
    private MsgInfoManager msgInfoManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private MsgInfo model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());

        propertyFilters.add(new PropertyFilter("EQS_senderUsername",
                SpringSecurityUtils.getCurrentUsername()));
        page = msgInfoManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public String listReceived() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        propertyFilters.add(new PropertyFilter("EQS_receiverUsername",
                SpringSecurityUtils.getCurrentUsername()));
        page = msgInfoManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public String listSent() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());

        propertyFilters.add(new PropertyFilter("EQS_receiverUsername",
                SpringSecurityUtils.getCurrentUsername()));
        page = msgInfoManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new MsgInfo();
    }

    public String save() {
        MsgInfo dest = null;

        if (id > 0) {
            dest = msgInfoManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;

            String username = userConnector.findByUsername(
                    SpringSecurityUtils.getCurrentUsername(),
                    ScopeHolder.getUserRepoRef()).getUsername();
            dest.setSenderUsername(username);
            dest.setCreateTime(new Date());
            dest.setStatus(0);
        }

        msgInfoManager.save(dest);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        List<MsgInfo> msgInfos = msgInfoManager.findByIds(selectedItem);

        msgInfoManager.removeAll(msgInfos);
        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = msgInfoManager.get(id);
        }

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = msgInfoManager.pagedQuery(page, propertyFilters);

        List<MsgInfo> msgInfos = (List<MsgInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("msg info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(msgInfos);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public MsgInfo getModel() {
        return model;
    }

    public void setMsgInfoManager(MsgInfoManager msgInfoManager) {
        this.msgInfoManager = msgInfoManager;
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

    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }
}
