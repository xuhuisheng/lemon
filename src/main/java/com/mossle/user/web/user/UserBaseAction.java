package com.mossle.user.web.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mossle.api.scope.ScopeHolder;
import com.mossle.api.user.UserCache;
import com.mossle.api.user.UserDTO;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.struts2.BaseAction;
import com.mossle.core.util.ServletUtils;

import com.mossle.security.util.SimplePasswordEncoder;

import com.mossle.user.persistence.domain.UserBase;
import com.mossle.user.persistence.domain.UserRepo;
import com.mossle.user.persistence.manager.UserBaseManager;
import com.mossle.user.persistence.manager.UserRepoManager;
import com.mossle.user.service.UserService;
import com.mossle.user.support.UserBaseWrapper;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = UserBaseAction.RELOAD, location = "user-base.do?operationMode=RETRIEVE", type = "redirect") })
public class UserBaseAction extends BaseAction implements
        ModelDriven<UserBase>, Preparable {
    public static final String RELOAD = "reload";
    private UserBaseManager userBaseManager;
    private UserRepoManager userRepoManager;
    private UserCache userCache;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private UserBase model;
    private long id;
    private String username;
    private String confirmPassword;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();
    private SimplePasswordEncoder simplePasswordEncoder;
    private UserBaseWrapper userBaseWrapper;
    private UserService userService;
    private long userRepoId;

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        UserRepo userRepo = userRepoManager.findUniqueBy("code",
                ScopeHolder.getScopeCode());
        propertyFilters.add(new PropertyFilter("EQL_userRepo.id", Long
                .toString(userRepo.getId())));
        page = userBaseManager.pagedQuery(page, propertyFilters);

        List<UserBase> userBases = (List<UserBase>) page.getResult();
        List<UserBaseWrapper> userBaseWrappers = new ArrayList<UserBaseWrapper>();

        for (UserBase userBase : userBases) {
            userBaseWrappers.add(new UserBaseWrapper(userBase));
        }

        page.setResult(userBaseWrappers);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new UserBase();
        model.setStatus(0);
    }

    public String save() throws Exception {
        // 先进行校验
        if (model.getPassword() != null) {
            if (!model.getPassword().equals(confirmPassword)) {
                addActionMessage(messages.getMessage(
                        "user.user.input.passwordnotequals", "两次输入密码不符"));

                // TODO: 还要填充schema
                return INPUT;
            }

            if (simplePasswordEncoder != null) {
                model.setPassword(simplePasswordEncoder.encode(model
                        .getPassword()));
            }
        }

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

        UserDTO userDto = new UserDTO();
        userDto.setId(Long.toString(dest.getId()));
        userDto.setUsername(dest.getUsername());
        userDto.setRef(dest.getRef());
        userDto.setUserRepoRef(Long.toString(userRepoId));
        userCache.removeUser(userDto);

        return RELOAD;
    }

    public String removeAll() {
        List<UserBase> userBases = userBaseManager.findByIds(selectedItem);

        for (UserBase userBase : userBases) {
            userService.removeUser(userBase);

            UserDTO userDto = new UserDTO();
            userDto.setId(Long.toString(userBase.getId()));
            userDto.setUsername(userBase.getUsername());
            userDto.setRef(userBase.getRef());
            userDto.setUserRepoRef(Long
                    .toString(userBase.getUserRepo().getId()));
            userCache.removeUser(userDto);
        }

        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = userBaseManager.get(id);
            userBaseWrapper = new UserBaseWrapper(model);
        } else {
            model = new UserBase();

            UserRepo userRepo = userRepoManager.findUniqueBy("code",
                    ScopeHolder.getScopeCode());
            model.setUserRepo(userRepo);
            userBaseWrapper = new UserBaseWrapper(model);
        }

        return INPUT;
    }

    public String search() {
        StringBuilder buff = new StringBuilder("select ub from UserBase ub");

        Map<String, Object> params = new HashMap<String, Object>();

        if (!params.isEmpty()) {
            page = userBaseManager.pagedQuery(buff.toString(),
                    page.getPageNo(), page.getPageSize(), params);

            List<UserBase> userBases = (List<UserBase>) page.getResult();
            List<UserBaseWrapper> userBaseWrappers = new ArrayList<UserBaseWrapper>();

            for (UserBase userBase : userBases) {
                userBaseWrappers.add(new UserBaseWrapper(userBase));
            }

            page.setResult(userBaseWrappers);
        }

        return "search";
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = userBaseManager.pagedQuery(page, propertyFilters);

        List<UserBase> userBases = (List<UserBase>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("user");
        tableModel.addHeaders("id", "username", "enabled", "description");
        tableModel.setData(userBases);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    public void checkUsername() throws Exception {
        String hql = "from UserBase where username=?";
        Object[] params = { username };

        if (id != 0L) {
            hql = "from UserBase where username=? and id<>?";
            params = new Object[] { username, id };
        }

        boolean result = userBaseManager.findUnique(hql, params) == null;
        ServletActionContext.getResponse().getWriter()
                .write(Boolean.toString(result));
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

    public void setUserRepoManager(UserRepoManager userRepoManager) {
        this.userRepoManager = userRepoManager;
    }

    public void setUserCache(UserCache userCache) {
        this.userCache = userCache;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public void setSimplePasswordEncoder(
            SimplePasswordEncoder simplePasswordEncoder) {
        this.simplePasswordEncoder = simplePasswordEncoder;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
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
