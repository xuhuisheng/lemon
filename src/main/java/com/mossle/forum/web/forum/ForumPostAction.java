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

import com.mossle.forum.domain.ForumPost;
import com.mossle.forum.domain.ForumTopic;
import com.mossle.forum.manager.ForumPostManager;
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
        @Result(name = ForumPostAction.RELOAD, location = "forum-topic.do?operationMode=RETRIEVE", type = "redirect"),
        @Result(name = ForumPostAction.RELOAD_VIEW, location = "forum-post!view.do?id=${forumTopicId}&operationMode=RETRIEVE", type = "redirect") })
public class ForumPostAction extends BaseAction implements
        ModelDriven<ForumPost>, Preparable {
    public static final String RELOAD = "reload";
    public static final String RELOAD_VIEW = "reload-view";
    private ForumPostManager forumPostManager;
    private ForumTopicManager forumTopicManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private ForumPost model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private ForumTopic forumTopic;
    private Long forumTopicId;
    private List<ForumPost> forumPosts;

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());

        propertyFilters.add(new PropertyFilter("EQS_senderUsername",
                SpringSecurityUtils.getCurrentUsername()));
        page = forumPostManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public String listReceived() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());

        propertyFilters.add(new PropertyFilter("EQS_receiverUsername",
                SpringSecurityUtils.getCurrentUsername()));
        page = forumPostManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public String listSent() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());

        propertyFilters.add(new PropertyFilter("EQS_receiverUsername",
                SpringSecurityUtils.getCurrentUsername()));
        page = forumPostManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new ForumPost();
    }

    public String save() {
        ForumPost dest = null;

        if (id > 0) {
            dest = forumPostManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;

            String userId = userConnector.findByUsername(
                    SpringSecurityUtils.getCurrentUsername(),
                    ScopeHolder.getUserRepoRef()).getId();
            dest.setUserId(Long.parseLong(userId));
            dest.setCreateTime(new Date());
        }

        forumPostManager.save(dest);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        List<ForumPost> forumPosts = forumPostManager.findByIds(selectedItem);

        forumPostManager.removeAll(forumPosts);
        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = forumPostManager.get(id);
        }

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = forumPostManager.pagedQuery(page, propertyFilters);

        List<ForumPost> forumPosts = (List<ForumPost>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("msg info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(forumPosts);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    public String view() throws Exception {
        forumTopic = forumTopicManager.get(id);

        return "view";
    }

    public void prepareCreatePost() {
        model = new ForumPost();
    }

    public String createPost() throws Exception {
        String userId = userConnector.findByUsername(
                SpringSecurityUtils.getCurrentUsername(),
                ScopeHolder.getUserRepoRef()).getId();
        model.setId(null);
        model.setUserId(Long.parseLong(userId));
        model.setCreateTime(new Date());
        model.setForumTopic(forumTopicManager.get(forumTopicId));
        forumPostManager.save(model);

        return RELOAD_VIEW;
    }

    public ForumTopic getForumTopic() {
        return forumTopic;
    }

    public Long getForumTopicId() {
        return forumTopicId;
    }

    public void setForumTopicId(Long forumTopicId) {
        this.forumTopicId = forumTopicId;
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public ForumPost getModel() {
        return model;
    }

    public void setForumPostManager(ForumPostManager forumPostManager) {
        this.forumPostManager = forumPostManager;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public void setForumTopicManager(ForumTopicManager forumTopicManager) {
        this.forumTopicManager = forumTopicManager;
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
