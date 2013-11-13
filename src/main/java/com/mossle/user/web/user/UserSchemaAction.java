package com.mossle.user.web.user;

import java.util.ArrayList;
import java.util.List;

import com.mossle.api.ScopeConnector;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.scope.ScopeHolder;
import com.mossle.core.struts2.BaseAction;

import com.mossle.user.persistence.domain.UserRepo;
import com.mossle.user.persistence.domain.UserSchema;
import com.mossle.user.persistence.manager.UserRepoManager;
import com.mossle.user.persistence.manager.UserSchemaManager;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = UserSchemaAction.RELOAD, location = "user-schema.do?operationMode=RETRIEVE&userRepoId=${userRepoId}", type = "redirect") })
public class UserSchemaAction extends BaseAction implements
        ModelDriven<UserSchema>, Preparable {
    public static final String RELOAD = "reload";
    private UserSchemaManager userSchemaManager;
    private UserRepoManager userRepoManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private UserSchema model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();
    private ScopeConnector scopeConnector;
    private long userRepoId;

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());

        propertyFilters.add(new PropertyFilter("EQL_userRepo.id", Long
                .toString(userRepoId)));
        page = userSchemaManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new UserSchema();
    }

    public String save() {
        UserSchema dest = null;

        if (id > 0) {
            dest = userSchemaManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;
        }

        if (id == 0) {
            dest.setGlobalId(scopeConnector.findGlobalId(ScopeHolder
                    .getGlobalCode()));
            dest.setLocalId(scopeConnector.findLocalId(
                    ScopeHolder.getGlobalCode(), ScopeHolder.getLocalCode()));
        }

        UserRepo userRepo = userRepoManager.get(userRepoId);
        dest.setUserRepo(userRepo);
        userSchemaManager.save(dest);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        List<UserSchema> userSchemas = userSchemaManager
                .findByIds(selectedItem);
        boolean success = true;

        for (UserSchema userSchema : userSchemas) {
            if (userSchema.getUserAttrs().isEmpty()) {
                userSchemaManager.remove(userSchema);
            } else {
                success = false;
                addActionMessage("无法删除" + userSchema.getName());
            }
        }

        if (success) {
            addActionMessage(messages.getMessage("core.success.delete", "删除成功"));
        }

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = userSchemaManager.get(id);
        }

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = userSchemaManager.pagedQuery(page, propertyFilters);

        List<UserSchema> userRepos = (List<UserSchema>) page.getResult();
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

    public UserSchema getModel() {
        return model;
    }

    public void setUserSchemaManager(UserSchemaManager userSchemaManager) {
        this.userSchemaManager = userSchemaManager;
    }

    public void setUserRepoManager(UserRepoManager userRepoManager) {
        this.userRepoManager = userRepoManager;
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

    // ~ ======================================================================
    public void setScopeConnector(ScopeConnector scopeConnector) {
        this.scopeConnector = scopeConnector;
    }

    public long getUserRepoId() {
        return userRepoId;
    }

    public void setUserRepoId(long userRepoId) {
        this.userRepoId = userRepoId;
    }
}
