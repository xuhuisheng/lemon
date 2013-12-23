package com.mossle.auth.web.auth;

import java.util.ArrayList;
import java.util.List;

import com.mossle.api.scope.ScopeHolder;

import com.mossle.auth.component.UserStatusChecker;
import com.mossle.auth.component.UserStatusConverter;
import com.mossle.auth.domain.UserStatus;
import com.mossle.auth.manager.UserStatusManager;
import com.mossle.auth.support.CheckUserStatusException;
import com.mossle.auth.support.UserStatusDTO;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.struts2.BaseAction;

import com.mossle.security.util.SimplePasswordEncoder;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({
        @Result(name = UserStatusAction.RELOAD, location = "user-status.do?operationMode=RETRIEVE", type = "redirect"),
        @Result(name = UserStatusAction.RELOAD_PASSWORD, location = "user-status!password.do?operationMode=RETRIEVE&id=${id}", type = "redirect") })
public class UserStatusAction extends BaseAction implements
        ModelDriven<UserStatus>, Preparable {
    private static Logger logger = LoggerFactory
            .getLogger(RolePermAction.class);
    public static final String RELOAD = "reload";
    public static final String RELOAD_PASSWORD = "reload-password";
    private UserStatusManager userStatusManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private UserStatus model;
    private long id;
    private String username;
    private String confirmPassword;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();
    private UserStatusConverter userStatusConverter;
    private UserStatusChecker userStatusChecker;
    private SimplePasswordEncoder simplePasswordEncoder;
    private String newPassword;

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        propertyFilters.add(new PropertyFilter("EQS_scopeId", ScopeHolder
                .getScopeId()));
        page = userStatusManager.pagedQuery(page, propertyFilters);

        List<UserStatus> userStatuses = (List<UserStatus>) page.getResult();
        List<UserStatusDTO> userStatusDtos = userStatusConverter
                .createUserStatusDtos(userStatuses,
                        ScopeHolder.getUserRepoRef(), ScopeHolder.getScopeId());
        page.setResult(userStatusDtos);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new UserStatus();
        model.setStatus(0);
    }

    public String save() {
        try {
            userStatusChecker.check(model);

            if (model.getPassword() != null) {
                if (!model.getPassword().equals(confirmPassword)) {
                    addActionMessage(messages.getMessage(
                            "user.user.input.passwordnotequals", "两次输入密码不符"));

                    return INPUT;
                }

                if (simplePasswordEncoder != null) {
                    model.setPassword(simplePasswordEncoder.encode(model
                            .getPassword()));
                }
            }

            UserStatus dest = null;

            if (id > 0) {
                dest = userStatusManager.get(id);
                beanMapper.copy(model, dest);
            } else {
                dest = model;
            }

            if (id == 0) {
                dest.setUserRepoRef(ScopeHolder.getUserRepoRef());
                dest.setScopeId(ScopeHolder.getScopeId());
            }

            userStatusManager.save(dest);

            addActionMessage(messages.getMessage("core.success.save", "保存成功"));
        } catch (CheckUserStatusException ex) {
            logger.warn(ex.getMessage(), ex);
            addActionMessage(ex.getMessage());

            return INPUT;
        }

        return RELOAD;
    }

    public String removeAll() {
        try {
            List<UserStatus> userStatuses = userStatusManager
                    .findByIds(selectedItem);

            for (UserStatus userStatus : userStatuses) {
                userStatusChecker.check(userStatus);
            }

            userStatusManager.removeAll(userStatuses);
            addActionMessage(messages.getMessage("core.success.delete", "删除成功"));
        } catch (CheckUserStatusException ex) {
            logger.warn(ex.getMessage(), ex);
            addActionMessage(ex.getMessage());
        }

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = userStatusManager.get(id);
        }

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = userStatusManager.pagedQuery(page, propertyFilters);

        List<UserStatus> userStatuses = (List<UserStatus>) page.getResult();
        List<UserStatusDTO> userStatusDtos = userStatusConverter
                .createUserStatusDtos(userStatuses,
                        ScopeHolder.getUserRepoRef(), ScopeHolder.getScopeId());
        TableModel tableModel = new TableModel();
        tableModel.setName("user status");
        tableModel.addHeaders("id", "username", "enabled", "authorities");
        tableModel.setData(userStatusDtos);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    public String password() {
        return "password";
    }

    public String initPassword() {
        if ((newPassword != null) && newPassword.equals(confirmPassword)) {
            UserStatus userStatus = userStatusManager.get(id);
            userStatus.setPassword(newPassword);
            userStatusManager.save(userStatus);
        }

        addActionMessage(messages.getMessage("core.success.save", "操作成功"));

        return RELOAD_PASSWORD;
    }

    public void checkUsername() throws Exception {
        String hql = "from UserStatus where username=?";
        Object[] params = { username };

        if (id != 0L) {
            hql = "from UserStatus where username=? and id<>?";
            params = new Object[] { username, id };
        }

        UserStatus userStatus = userStatusManager.findUnique(hql, params);

        boolean result = (userStatus == null);
        ServletActionContext.getResponse().getWriter()
                .write(Boolean.toString(result));
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public UserStatus getModel() {
        return model;
    }

    public void setUserStatusManager(UserStatusManager userStatusManager) {
        this.userStatusManager = userStatusManager;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public void setUserStatusConverter(UserStatusConverter userStatusConverter) {
        this.userStatusConverter = userStatusConverter;
    }

    public void setUserStatusChecker(UserStatusChecker userStatusChecker) {
        this.userStatusChecker = userStatusChecker;
    }

    public void setSimplePasswordEncoder(
            SimplePasswordEncoder simplePasswordEncoder) {
        this.simplePasswordEncoder = simplePasswordEncoder;
    }

    // ~ ======================================================================
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Page getPage() {
        return page;
    }

    public void setSelectedItem(List<Long> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
