package com.mossle.user.web.user;

import java.util.ArrayList;
import java.util.List;

import com.mossle.api.scope.ScopeHolder;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.struts2.BaseAction;

import com.mossle.user.component.UserRepoPublisher;
import com.mossle.user.persistence.domain.UserRepo;
import com.mossle.user.persistence.manager.UserRepoManager;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = UserRepoAction.RELOAD, location = "user-repo.do?operationMode=RETRIEVE", type = "redirect") })
public class UserRepoAction extends BaseAction implements
        ModelDriven<UserRepo>, Preparable {
    public static final String RELOAD = "reload";
    private UserRepoManager userRepoManager;
    private UserRepoPublisher userRepoPublisher;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private UserRepo model;
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
        propertyFilters.add(new PropertyFilter("EQS_scopeId", ScopeHolder
                .getScopeId()));
        page = userRepoManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new UserRepo();
    }

    public String save() {
        UserRepo dest = null;

        if (id > 0) {
            dest = userRepoManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;
        }

        if (id == 0) {
            dest.setScopeId(ScopeHolder.getScopeId());
        }

        userRepoManager.save(dest);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));
        userRepoPublisher.execute(dest);

        return RELOAD;
    }

    public String removeAll() {
        List<UserRepo> userRepos = userRepoManager.findByIds(selectedItem);

        boolean success = true;

        for (UserRepo userRepo : userRepos) {
            if (userRepo.getUserSchemas().isEmpty()) {
                userRepoManager.remove(userRepo);
                userRepo.setName(null);
                userRepoPublisher.execute(userRepo);
            } else {
                success = false;
                addActionMessage("无法删除" + userRepo.getName());
            }
        }

        if (success) {
            addActionMessage(messages.getMessage("core.success.delete", "删除成功"));
        }

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = userRepoManager.get(id);
        }

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = userRepoManager.pagedQuery(page, propertyFilters);

        List<UserRepo> userRepos = (List<UserRepo>) page.getResult();
        TableModel tableModel = new TableModel();
        tableModel.setName("user-repo");
        tableModel.addHeaders("id", "name", "type", "repoCode", "reference",
                "descn");
        tableModel.setData(userRepos);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public UserRepo getModel() {
        return model;
    }

    public void setUserRepoManager(UserRepoManager userRepoManager) {
        this.userRepoManager = userRepoManager;
    }

    public void setUserRepoPublisher(UserRepoPublisher userRepoPublisher) {
        this.userRepoPublisher = userRepoPublisher;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    // ~ ======================================================================
    public void setId(long id) {
        this.id = id;
    }

    public Page getPage() {
        return page;
    }

    public void setSelectedItem(List<Long> selectedItem) {
        this.selectedItem = selectedItem;
    }
}
