package com.mossle.user.web.user;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mossle.api.UserProcessor;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.struts2.BaseAction;
import com.mossle.core.util.ServletUtils;

import com.mossle.security.util.SimplePasswordEncoder;
import com.mossle.security.util.SpringSecurityUtils;

import com.mossle.user.persistence.domain.UserAttr;
import com.mossle.user.persistence.domain.UserBase;
import com.mossle.user.persistence.domain.UserRepo;
import com.mossle.user.persistence.domain.UserSchema;
import com.mossle.user.persistence.manager.UserAttrManager;
import com.mossle.user.persistence.manager.UserBaseManager;
import com.mossle.user.persistence.manager.UserRepoManager;
import com.mossle.user.persistence.manager.UserSchemaManager;
import com.mossle.user.service.UserService;
import com.mossle.user.support.EmptyUserProcessor;
import com.mossle.user.support.UserBaseWrapper;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = ProfileAction.RELOAD, location = "profile.do?operationMode=RETRIEVE", type = "redirect") })
public class ProfileAction extends BaseAction implements ModelDriven<UserBase>,
        Preparable {
    public static final String RELOAD = "reload";
    private UserBaseManager userBaseManager;
    private UserSchemaManager userSchemaManager;
    private UserAttrManager userAttrManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private UserBase model;
    private long id;
    private BeanMapper beanMapper = new BeanMapper();
    private UserBaseWrapper userBaseWrapper;
    private UserService userService;
    private long userRepoId;

    public String execute() {
        this.model = userBaseManager.findUniqueBy("username",
                SpringSecurityUtils.getCurrentUsername());
        this.userBaseWrapper = new UserBaseWrapper(model);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new UserBase();
    }

    public String save() throws Exception {
        Map<String, Object> parameters = ServletUtils
                .getParametersStartingWith(ServletActionContext.getRequest(),
                        "_user_attr_");

        // 再进行数据复制
        UserBase dest = null;

        if (id > 0) {
            dest = userBaseManager.get(id);
            beanMapper.copy(model, dest);
            userService.updateUser(dest, userRepoId, parameters);
        } else {
            dest = model;
            userService.insertUser(dest, userRepoId, parameters);
        }

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public UserBase getModel() {
        return model;
    }

    public void setUserBaseManager(UserBaseManager userBaseManager) {
        this.userBaseManager = userBaseManager;
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

    public UserBaseWrapper getUserBaseWrapper() {
        return userBaseWrapper;
    }

    // ~ ======================================================================
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setUserRepoId(long userRepoId) {
        this.userRepoId = userRepoId;
    }
}
