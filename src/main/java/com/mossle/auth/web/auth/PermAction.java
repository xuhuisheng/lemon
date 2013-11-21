package com.mossle.auth.web.auth;

import java.util.ArrayList;
import java.util.List;

import com.mossle.api.scope.ScopeConnector;
import com.mossle.api.scope.ScopeHolder;

import com.mossle.auth.domain.Oper;
import com.mossle.auth.domain.Perm;
import com.mossle.auth.domain.Resc;
import com.mossle.auth.manager.OperManager;
import com.mossle.auth.manager.PermManager;
import com.mossle.auth.manager.RescManager;

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

@Results({ @Result(name = PermAction.RELOAD, location = "perm.do?operationMode=RETRIEVE", type = "redirect") })
public class PermAction extends BaseAction implements ModelDriven<Perm>,
        Preparable {
    public static final String RELOAD = "reload";
    private PermManager permManager;
    private RescManager rescManager;
    private OperManager operManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private Perm model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();
    private List<Resc> rescs;
    private List<Oper> opers;
    private long rescId;
    private long operId;
    private ScopeConnector scopeConnector;

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        propertyFilters.add(new PropertyFilter("EQS_scopeId", ScopeHolder
                .getScopeId()));
        page = permManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new Perm();
    }

    public String save() {
        Perm dest = null;

        if (id > 0) {
            dest = permManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;
        }

        Resc resc = rescManager.get(rescId);
        dest.setResc(resc);

        if (operId == 0L) {
            dest.setOper(null);
            dest.setName(dest.getResc().getName());
        } else {
            dest.setOper(operManager.get(operId));
            dest.setName(dest.getResc().getName() + ":"
                    + dest.getOper().getName());
        }

        if (id == 0) {
            dest.setScopeId(ScopeHolder.getScopeId());
        }

        permManager.save(dest);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        List<Perm> perms = permManager.findByIds(selectedItem);
        permManager.removeAll(perms);
        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = permManager.get(id);
        }

        rescs = rescManager.findBy("scopeId", ScopeHolder.getScopeId());
        opers = operManager.findBy("scopeId", ScopeHolder.getScopeId());

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = permManager.pagedQuery(page, propertyFilters);

        List<Perm> perms = (List<Perm>) page.getResult();
        TableModel tableModel = new TableModel();
        tableModel.setName("perm");
        tableModel.addHeaders("id", "name");
        tableModel.setData(perms);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public Perm getModel() {
        return model;
    }

    public void setPermManager(PermManager permManager) {
        this.permManager = permManager;
    }

    public void setRescManager(RescManager rescManager) {
        this.rescManager = rescManager;
    }

    public void setOperManager(OperManager operManager) {
        this.operManager = operManager;
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

    public List<Resc> getRescs() {
        return rescs;
    }

    public List<Oper> getOpers() {
        return opers;
    }

    public void setRescId(long rescId) {
        this.rescId = rescId;
    }

    public void setOperId(long operId) {
        this.operId = operId;
    }

    public void setScopeConnector(ScopeConnector scopeConnector) {
        this.scopeConnector = scopeConnector;
    }
}
