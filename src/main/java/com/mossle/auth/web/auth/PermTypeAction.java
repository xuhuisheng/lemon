package com.mossle.auth.web.auth;

import java.util.ArrayList;
import java.util.List;

import com.mossle.api.scope.ScopeHolder;

import com.mossle.auth.domain.Perm;
import com.mossle.auth.domain.PermType;
import com.mossle.auth.manager.PermManager;
import com.mossle.auth.manager.PermTypeManager;

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

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = PermTypeAction.RELOAD, location = "perm-type.do?operationMode=RETRIEVE&appId=${appId}", type = "redirect") })
public class PermTypeAction extends BaseAction implements
        ModelDriven<PermType>, Preparable {
    public static final String RELOAD = "reload";
    private PermTypeManager permTypeManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private PermType model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();
    private List<Perm> perms;
    private PermManager permManager;
    private Long permId;

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        propertyFilters.add(new PropertyFilter("EQS_scopeId", ScopeHolder
                .getScopeId()));
        page = permTypeManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    // ~ ======================================================================
    public void prepareSave() {
        model = new PermType();
    }

    public String save() {
        // copy
        PermType dest = null;

        if (id > 0) {
            dest = permTypeManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;
        }

        if (id == 0) {
            dest.setScopeId(ScopeHolder.getScopeId());
        }

        // save
        permTypeManager.save(dest);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        List<PermType> permTypes = permTypeManager.findByIds(selectedItem);
        permTypeManager.removeAll(permTypes);
        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = permTypeManager.get(id);
        }

        perms = permManager.findBy("scopeId", ScopeHolder.getScopeId());

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = permTypeManager.pagedQuery(page, propertyFilters);

        List<PermType> permTypees = (List<PermType>) page.getResult();
        TableModel tableModel = new TableModel();
        tableModel.setName("permType");
        tableModel.addHeaders("id", "type", "value", "perm.name", "priority",
                "app.name");
        tableModel.setData(permTypees);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public PermType getModel() {
        return model;
    }

    public void setPermTypeManager(PermTypeManager permTypeManager) {
        this.permTypeManager = permTypeManager;
    }

    public void setPermManager(PermManager permManager) {
        this.permManager = permManager;
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

    public List<Perm> getPerms() {
        return perms;
    }

    public void setPermId(Long permId) {
        this.permId = permId;
    }
}
