package com.mossle.forum.web.forum;

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

import com.mossle.forum.domain.ForumTopic;
import com.mossle.forum.manager.ForumTopicManager;

import com.mossle.security.util.SpringSecurityUtils;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({
        @Result(name = ForumTopicAction.RELOAD, location = "forum-topic.do?operationMode=RETRIEVE", type = "redirect"),
        @Result(name = ForumTopicAction.RELOAD_VIEW, location = "forum-topic!view.do?operationMode=RETRIEVE", type = "redirect") })
public class ForumTopicAction extends BaseAction implements
        ModelDriven<ForumTopic>, Preparable {
    public static final String RELOAD = "reload";
    public static final String RELOAD_VIEW = "reload-view";
    private ForumTopicManager forumTopicManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private ForumTopic model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private List<ForumTopic> forumTopics;

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());

        propertyFilters.add(new PropertyFilter("EQS_senderUsername",
                SpringSecurityUtils.getCurrentUsername()));
        page = forumTopicManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public String listReceived() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());

        propertyFilters.add(new PropertyFilter("EQS_receiverUsername",
                SpringSecurityUtils.getCurrentUsername()));
        page = forumTopicManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public String listSent() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());

        propertyFilters.add(new PropertyFilter("EQS_receiverUsername",
                SpringSecurityUtils.getCurrentUsername()));
        page = forumTopicManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new ForumTopic();
    }

    public String save() {
        ForumTopic dest = null;

        if (id > 0) {
            dest = forumTopicManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;

            String userId = userConnector.findByUsername(
                    SpringSecurityUtils.getCurrentUsername(),
                    ScopeHolder.getUserRepoRef()).getId();
            dest.setUserId(Long.parseLong(userId));
            dest.setCreateTime(new Date());
        }

        forumTopicManager.save(dest);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        forumTopics = forumTopicManager.findByIds(selectedItem);

        forumTopicManager.removeAll(forumTopics);
        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = forumTopicManager.get(id);
        }

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = forumTopicManager.pagedQuery(page, propertyFilters);

        forumTopics = (List<ForumTopic>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("msg info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(forumTopics);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    public String view() throws Exception {
        forumTopics = forumTopicManager.getAll();

        return "view";
    }

    public String create() throws Exception {
        return "create";
    }

    public void prepareCreateTopic() {
        model = new ForumTopic();
    }

    public String createTopic() throws Exception {
        String userId = userConnector.findByUsername(
                SpringSecurityUtils.getCurrentUsername(),
                ScopeHolder.getUserRepoRef()).getId();
        model.setUserId(Long.parseLong(userId));
        model.setCreateTime(new Date());
        forumTopicManager.save(model);

        return RELOAD_VIEW;
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public ForumTopic getModel() {
        return model;
    }

    public void setForumTopicManager(ForumTopicManager forumTopicManager) {
        this.forumTopicManager = forumTopicManager;
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

    public List<ForumTopic> getForumTopics() {
        return forumTopics;
    }
}
