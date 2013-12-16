package com.mossle.group.web.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mossle.api.UserConnector;
import com.mossle.api.UserDTO;
import com.mossle.api.scope.ScopeHolder;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.struts2.BaseAction;

import com.mossle.group.domain.OrgDepartment;
import com.mossle.group.manager.OrgDepartmentManager;

import com.mossle.party.domain.PartyEntity;
import com.mossle.party.domain.PartyStruct;
import com.mossle.party.domain.PartyStructId;
import com.mossle.party.manager.PartyEntityManager;
import com.mossle.party.manager.PartyStructManager;
import com.mossle.party.manager.PartyTypeManager;
import com.mossle.party.service.PartyService;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results(@Result(name = OrgDepartmentAction.RELOAD, location = "org-department.do?operationMode=RETRIEVE", type = "redirect"))
public class OrgDepartmentAction extends BaseAction implements
        ModelDriven<OrgDepartment>, Preparable {
    public static final String RELOAD = "reload";
    private OrgDepartmentManager orgDepartmentManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private OrgDepartment model;
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
        page = orgDepartmentManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new OrgDepartment();
        model.setStatus(0);
    }

    public String save() {
        OrgDepartment dest = null;

        if (id > 0) {
            dest = orgDepartmentManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;
        }

        if (id == 0) {
            dest.setScopeId(ScopeHolder.getScopeId());

            // TODO: sync party
        } else {
            // TODO: sync party
        }

        orgDepartmentManager.save(dest);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        List<OrgDepartment> orgCompanies = orgDepartmentManager
                .findByIds(selectedItem);

        for (OrgDepartment orgDepartment : orgCompanies) {
            orgDepartmentManager.remove(orgDepartment);
        }

        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = orgDepartmentManager.get(id);
        }

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = orgDepartmentManager.pagedQuery(page, propertyFilters);

        List<OrgDepartment> orgCompanies = (List<OrgDepartment>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("org");
        tableModel.addHeaders("id", "name", "status", "description");
        tableModel.setData(orgCompanies);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public OrgDepartment getModel() {
        return model;
    }

    public void setOrgDepartmentManager(
            OrgDepartmentManager orgDepartmentManager) {
        this.orgDepartmentManager = orgDepartmentManager;
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
}
