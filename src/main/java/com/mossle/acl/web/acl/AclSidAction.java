package com.mossle.acl.web.acl;

import java.util.ArrayList;
import java.util.List;

import com.mossle.acl.domain.AclSid;
import com.mossle.acl.manager.AclSidManager;

import com.mossle.api.scope.ScopeHolder;

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

@Results({ @Result(name = AclSidAction.RELOAD, location = "acl-sid.do?operationMode=RETRIEVE", type = "redirect") })
public class AclSidAction extends BaseAction implements ModelDriven<AclSid>,
        Preparable {
    public static final String RELOAD = "reload";
    private AclSidManager aclSidManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private AclSid model;
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
        page = aclSidManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new AclSid();
    }

    public String save() {
        AclSid dest = null;

        if (id > 0) {
            dest = aclSidManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;
        }

        if (id == 0) {
            dest.setScopeId(ScopeHolder.getScopeId());
        }

        aclSidManager.save(dest);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        List<AclSid> aclSids = aclSidManager.findByIds(selectedItem);

        aclSidManager.removeAll(aclSids);
        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = aclSidManager.get(id);
        }

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = aclSidManager.pagedQuery(page, propertyFilters);

        List<AclSid> aclSids = (List<AclSid>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("AclSid");
        tableModel.addHeaders("id");
        tableModel.setData(aclSids);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public AclSid getModel() {
        return model;
    }

    public void setAclSidManager(AclSidManager aclSidManager) {
        this.aclSidManager = aclSidManager;
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
