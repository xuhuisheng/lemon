package com.mossle.auth.web.auth;

import java.util.ArrayList;
import java.util.List;

import com.mossle.api.scope.ScopeConnector;
import com.mossle.api.scope.ScopeHolder;
import com.mossle.api.scope.ScopeInfo;

import com.mossle.auth.component.RoleChecker;
import com.mossle.auth.domain.Role;
import com.mossle.auth.domain.RoleDef;
import com.mossle.auth.manager.RoleDefManager;
import com.mossle.auth.manager.RoleManager;
import com.mossle.auth.support.CheckRoleException;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.struts2.BaseAction;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = RoleAction.RELOAD, location = "role.do?operationMode=RETRIEVE", type = "redirect") })
public class RoleAction extends BaseAction implements ModelDriven<Role>,
        Preparable {
    private static Logger logger = LoggerFactory.getLogger(RoleAction.class);
    public static final String RELOAD = "reload";
    private RoleManager roleManager;
    private RoleDefManager roleDefManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private Role model;
    private long id;
    private String name;
    private List<Long> selectedItem = new ArrayList<Long>();
    private RoleChecker roleChecker;
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();
    private ScopeConnector scopeConnector;
    private long roleDefId;
    private List<RoleDef> roleDefs;

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        propertyFilters.add(new PropertyFilter("EQS_scopeId", ScopeHolder
                .getScopeId()));
        page = roleManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new Role();
    }

    public String save() {
        try {
            // before check
            roleChecker.check(model);

            // after invoke
            Role dest = null;

            if (id > 0) {
                dest = roleManager.get(id);
                beanMapper.copy(model, dest);
            } else {
                dest = model;
            }

            if (id == 0) {
                dest.setScopeId(ScopeHolder.getScopeId());
            }

            dest.setName(roleDefManager.get(roleDefId).getName());
            dest.setRoleDef(roleDefManager.get(roleDefId));

            roleManager.save(dest);

            addActionMessage(messages.getMessage("core.success.save", "保存成功"));
        } catch (CheckRoleException ex) {
            logger.warn(ex.getMessage(), ex);
            addActionMessage(ex.getMessage());

            return INPUT;
        }

        return RELOAD;
    }

    public String removeAll() {
        try {
            List<Role> roles = roleManager.findByIds(selectedItem);

            for (Role role : roles) {
                roleChecker.check(role);
            }

            roleManager.removeAll(roles);
            addActionMessage(messages.getMessage("core.success.delete", "删除成功"));
        } catch (CheckRoleException ex) {
            logger.warn(ex.getMessage(), ex);
            addActionMessage(ex.getMessage());
        }

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = roleManager.get(id);
        }

        roleDefs = roleDefManager.find("from RoleDef where scopeId=?",
                ScopeHolder.getScopeId());

        List<ScopeInfo> scopeInfos = scopeConnector.findSharedScopes();

        for (ScopeInfo scopeInfo : scopeInfos) {
            roleDefs.addAll(roleDefManager.find(
                    "from RoleDef where scopeInfo=?", scopeInfo.getId()));
        }

        List<Role> roles = roleManager.findBy("scopeId",
                ScopeHolder.getScopeId());
        List<RoleDef> removedRoleDefs = new ArrayList<RoleDef>();

        for (Role role : roles) {
            for (RoleDef roleDef : roleDefs) {
                if (roleDef.getId().equals(role.getRoleDef().getId())) {
                    removedRoleDefs.add(roleDef);

                    break;
                }
            }
        }

        roleDefs.removeAll(removedRoleDefs);

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = roleManager.pagedQuery(page, propertyFilters);

        List<Role> roles = (List<Role>) page.getResult();
        TableModel tableModel = new TableModel();
        tableModel.setName("role");
        tableModel.addHeaders("id", "name", "descn");
        tableModel.setData(roles);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    public void checkName() throws Exception {
        String hql = "from Role where scopeId=" + ScopeHolder.getScopeId()
                + " and name=?";
        Object[] params = { name };

        if (id != 0L) {
            hql = "from Role where scopeId=" + ScopeHolder.getScopeId()
                    + " and name=? and id<>?";
            params = new Object[] { name, id };
        }

        boolean result = roleManager.findUnique(hql, params) == null;
        ServletActionContext.getResponse().getWriter()
                .write(Boolean.toString(result));
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public Role getModel() {
        return model;
    }

    public void setRoleManager(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    public void setRoleDefManager(RoleDefManager roleDefManager) {
        this.roleDefManager = roleDefManager;
    }

    public void setRoleChecker(RoleChecker roleChecker) {
        this.roleChecker = roleChecker;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setScopeConnector(ScopeConnector scopeConnector) {
        this.scopeConnector = scopeConnector;
    }

    public void setRoleDefId(long roleDefId) {
        this.roleDefId = roleDefId;
    }

    public List<RoleDef> getRoleDefs() {
        return roleDefs;
    }
}
