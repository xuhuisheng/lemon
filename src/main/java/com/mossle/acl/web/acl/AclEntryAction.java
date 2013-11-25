package com.mossle.acl.web.acl;

import java.util.ArrayList;
import java.util.List;

import com.mossle.acl.domain.AclEntry;
import com.mossle.acl.domain.AclObjectIdentity;
import com.mossle.acl.domain.AclSid;
import com.mossle.acl.manager.AclEntryManager;
import com.mossle.acl.manager.AclObjectIdentityManager;
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

@Results({ @Result(name = AclEntryAction.RELOAD, location = "acl-entry.do?operationMode=RETRIEVE", type = "redirect") })
public class AclEntryAction extends BaseAction implements
        ModelDriven<AclEntry>, Preparable {
    public static final String RELOAD = "reload";
    private AclEntryManager aclEntryManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private AclEntry model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();

    // ~ ======================================================================
    private AclObjectIdentityManager aclObjectIdentityManager;
    private AclSidManager aclSidManager;
    private List<AclObjectIdentity> aclObjectIdentities;
    private List<AclSid> aclSids;
    private Long identityId;
    private Long sidId;

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        propertyFilters.add(new PropertyFilter("EQS_scopeId", ScopeHolder
                .getScopeId()));
        page = aclEntryManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new AclEntry();
    }

    public String save() {
        AclEntry dest = null;

        if (id > 0) {
            dest = aclEntryManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;
        }

        dest.setAclObjectIdentity(aclObjectIdentityManager.get(identityId));
        dest.setSidId(sidId);

        if (id == 0) {
            dest.setScopeId(ScopeHolder.getScopeId());
        }

        aclEntryManager.save(dest);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        List<AclEntry> aclEntrys = aclEntryManager.findByIds(selectedItem);

        aclEntryManager.removeAll(aclEntrys);
        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = aclEntryManager.get(id);
        }

        aclObjectIdentities = aclObjectIdentityManager.findBy("scopeId",
                ScopeHolder.getScopeId());
        aclSids = aclSidManager.findBy("scopeId", ScopeHolder.getScopeId());

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = aclEntryManager.pagedQuery(page, propertyFilters);

        List<AclEntry> aclEntrys = (List<AclEntry>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("AclEntry");
        tableModel.addHeaders("id");
        tableModel.setData(aclEntrys);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public AclEntry getModel() {
        return model;
    }

    public void setAclEntryManager(AclEntryManager aclEntryManager) {
        this.aclEntryManager = aclEntryManager;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public void setAclObjectIdentityManager(
            AclObjectIdentityManager aclObjectIdentityManager) {
        this.aclObjectIdentityManager = aclObjectIdentityManager;
    }

    public void setAclSidManager(AclSidManager aclSidManager) {
        this.aclSidManager = aclSidManager;
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

    public List<AclObjectIdentity> getAclObjectIdentities() {
        return aclObjectIdentities;
    }

    public List<AclSid> getAclSids() {
        return aclSids;
    }

    public void setIdentityId(Long identityId) {
        this.identityId = identityId;
    }

    public void setSidId(Long sidId) {
        this.sidId = sidId;
    }
}
