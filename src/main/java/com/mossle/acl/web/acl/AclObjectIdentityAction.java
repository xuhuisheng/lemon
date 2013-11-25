package com.mossle.acl.web.acl;

import java.util.ArrayList;
import java.util.List;

import com.mossle.acl.domain.AclObjectIdentity;
import com.mossle.acl.domain.AclObjectType;
import com.mossle.acl.domain.AclSid;
import com.mossle.acl.manager.AclObjectIdentityManager;
import com.mossle.acl.manager.AclObjectTypeManager;
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

@Results({ @Result(name = AclObjectIdentityAction.RELOAD, location = "acl-object-identity.do?operationMode=RETRIEVE", type = "redirect") })
public class AclObjectIdentityAction extends BaseAction implements
        ModelDriven<AclObjectIdentity>, Preparable {
    public static final String RELOAD = "reload";
    private AclObjectIdentityManager aclObjectIdentityManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private AclObjectIdentity model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();

    // ~ ======================================================================
    private AclObjectTypeManager aclObjectTypeManager;
    private AclSidManager aclSidManager;
    private List<AclObjectType> aclObjectTypes;
    private List<AclObjectIdentity> aclObjectIdentities;
    private List<AclSid> aclSids;
    private Long typeId;
    private Long parentId;
    private Long ownerId;

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        propertyFilters.add(new PropertyFilter("EQS_scopeId", ScopeHolder
                .getScopeId()));
        page = aclObjectIdentityManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new AclObjectIdentity();
    }

    public String save() {
        AclObjectIdentity dest = null;

        if (id > 0) {
            dest = aclObjectIdentityManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;
        }

        dest.setAclObjectType(aclObjectTypeManager.get(typeId));

        if (parentId != null) {
            dest.setAclObjectIdentity(aclObjectIdentityManager.get(parentId));
        }

        if (ownerId != null) {
            dest.setOwnerId(ownerId);
        }

        if (id == 0) {
            dest.setScopeId(ScopeHolder.getScopeId());
        }

        aclObjectIdentityManager.save(dest);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        List<AclObjectIdentity> aclObjectIdentitys = aclObjectIdentityManager
                .findByIds(selectedItem);

        aclObjectIdentityManager.removeAll(aclObjectIdentitys);
        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = aclObjectIdentityManager.get(id);
        }

        String scopeId = ScopeHolder.getScopeId();
        aclObjectTypes = aclObjectTypeManager.findBy("scopeId", scopeId);
        aclObjectIdentities = aclObjectIdentityManager.findBy("scopeId",
                scopeId);
        aclSids = aclSidManager.findBy("scopeId", scopeId);

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = aclObjectIdentityManager.pagedQuery(page, propertyFilters);

        List<AclObjectIdentity> aclObjectIdentitys = (List<AclObjectIdentity>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("AclObjectIdentity");
        tableModel.addHeaders("id");
        tableModel.setData(aclObjectIdentitys);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public AclObjectIdentity getModel() {
        return model;
    }

    public void setAclObjectIdentityManager(
            AclObjectIdentityManager aclObjectIdentityManager) {
        this.aclObjectIdentityManager = aclObjectIdentityManager;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public void setAclObjectTypeManager(
            AclObjectTypeManager aclObjectTypeManager) {
        this.aclObjectTypeManager = aclObjectTypeManager;
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

    public List<AclObjectType> getAclObjectTypes() {
        return aclObjectTypes;
    }

    public List<AclObjectIdentity> getAclObjectIdentities() {
        return aclObjectIdentities;
    }

    public List<AclSid> getAclSids() {
        return aclSids;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
