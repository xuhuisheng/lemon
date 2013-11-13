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
import com.mossle.scope.manager.ScopeGlobalManager;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = ScopeGlobalAction.RELOAD, location = "scope-global.do?operationMode=RETRIEVE", type = "redirect") })
public class ScopeGlobalAction extends BaseAction implements
        ModelDriven<ScopeGlobal>, Preparable {
    public static final String RELOAD = "reload";
    private ScopeGlobalManager scopeGlobalManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private ScopeGlobal model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();
    private String name;

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = scopeGlobalManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new ScopeGlobal();
    }

    public String save() {
        ScopeGlobal dest = null;

        if (id > 0) {
            dest = scopeGlobalManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;
        }

        scopeGlobalManager.save(dest);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        List<ScopeGlobal> scopeGlobals = scopeGlobalManager
                .findByIds(selectedItem);

        scopeGlobalManager.removeAll(scopeGlobals);
        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = scopeGlobalManager.get(id);
        }

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = scopeGlobalManager.pagedQuery(page, propertyFilters);

        List<ScopeGlobal> scopeGlobals = (List<ScopeGlobal>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("scopeGlobal");
        tableModel.addHeaders("id", "name");
        tableModel.setData(scopeGlobals);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    public void checkName() throws Exception {
        String hql = "from ScopeGlobal where name=?";
        Object[] params = { name };

        if (id != 0L) {
            hql = "from ScopeGlobal where name=? and id<>?";
            params = new Object[] { name, id };
        }

        ScopeGlobal scopeGlobal = scopeGlobalManager.findUnique(hql, params);

        boolean result = (scopeGlobal == null);
        ServletActionContext.getResponse().getWriter()
                .write(Boolean.toString(result));
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public ScopeGlobal getModel() {
        return model;
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

    public void setName(String name) {
        this.name = name;
    }
}
