package com.mossle.scope.web.scope;

import java.util.ArrayList;
import java.util.List;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.struts2.BaseAction;

import com.mossle.scope.domain.ScopeGlobal;
import com.mossle.scope.domain.ScopeLocal;
import com.mossle.scope.manager.ScopeGlobalManager;
import com.mossle.scope.manager.ScopeLocalManager;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = ScopeLocalAction.RELOAD, location = "scope-local.do?operationMode=RETRIEVE", type = "redirect") })
public class ScopeLocalAction extends BaseAction implements
        ModelDriven<ScopeLocal>, Preparable {
    public static final String RELOAD = "reload";
    private ScopeLocalManager scopeLocalManager;
    private ScopeGlobalManager scopeGlobalManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private ScopeLocal model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();
    private List<ScopeGlobal> scopeGlobals;
    private Long scopeGlobalId;
    private String name;

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = scopeLocalManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new ScopeLocal();
    }

    public String save() {
        ScopeLocal dest = null;

        if (id > 0) {
            dest = scopeLocalManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;
        }

        dest.setScopeGlobal(scopeGlobalManager.get(scopeGlobalId));
        scopeLocalManager.save(dest);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        List<ScopeLocal> scopeLocals = scopeLocalManager
                .findByIds(selectedItem);

        scopeLocalManager.removeAll(scopeLocals);
        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = scopeLocalManager.get(id);
        }

        scopeGlobals = scopeGlobalManager.getAll();

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = scopeLocalManager.pagedQuery(page, propertyFilters);

        List<ScopeLocal> scopeLocals = (List<ScopeLocal>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("scopeLocal");
        tableModel.addHeaders("id", "name");
        tableModel.setData(scopeLocals);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    public void checkName() throws Exception {
        String hql = "from ScopeLocal where name=? and scopeGlobal.id=?";
        Object[] params = { name, scopeGlobalId };

        if (id != 0L) {
            hql = "from ScopeLocal where name=? and scopeGlobal.id=? and id<>?";
            params = new Object[] { name, scopeGlobalId, id };
        }

        ScopeLocal scopeLocal = scopeLocalManager.findUnique(hql, params);

        boolean result = (scopeLocal == null);
        ServletActionContext.getResponse().getWriter()
                .write(Boolean.toString(result));
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public ScopeLocal getModel() {
        return model;
    }

    public void setScopeLocalManager(ScopeLocalManager scopeLocalManager) {
        this.scopeLocalManager = scopeLocalManager;
    }

    public void setScopeGlobalManager(ScopeGlobalManager scopeGlobalManager) {
        this.scopeGlobalManager = scopeGlobalManager;
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

    // ~ ======================================================================
    public List<ScopeGlobal> getScopeGlobals() {
        return scopeGlobals;
    }

    public void setScopeGlobalId(Long scopeGlobalId) {
        this.scopeGlobalId = scopeGlobalId;
    }

    public void setName(String name) {
        this.name = name;
    }
}
