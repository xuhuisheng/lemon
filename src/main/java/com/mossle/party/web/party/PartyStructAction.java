package com.mossle.party.web.party;

import java.util.ArrayList;
import java.util.List;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.page.Page;
import com.mossle.core.struts2.BaseAction;

import com.mossle.party.domain.PartyEntity;
import com.mossle.party.domain.PartyStruct;
import com.mossle.party.domain.PartyStructId;
import com.mossle.party.domain.PartyStructType;
import com.mossle.party.manager.PartyEntityManager;
import com.mossle.party.manager.PartyStructManager;
import com.mossle.party.manager.PartyStructTypeManager;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = PartyStructAction.RELOAD, location = "party-struct.do?operationMode=RETRIEVE", type = "redirect") })
public class PartyStructAction extends BaseAction implements
        ModelDriven<PartyStruct>, Preparable {
    public static final String RELOAD = "reload";
    private PartyEntityManager partyEntityManager;
    private PartyStructManager partyStructManager;
    private PartyStructTypeManager partyStructTypeManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private PartyStruct model;
    private List<String> selectedItem = new ArrayList<String>();
    private List<PartyStructType> partyStructTypes;
    private List<PartyEntity> partyEntities;
    private long partyStructTypeId;
    private long parentEntityId;
    private long childEntityId;
    private String partyStructId;

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = partyStructManager.pagedQuery(page, propertyFilters);

        partyStructTypes = partyStructTypeManager.getAll();

        return SUCCESS;
    }

    public void prepareSave() {
        if (partyStructId != null) {
            this.model = convertPartyStruct(partyStructId);
        }
    }

    public String save() {
        if (partyStructId != null) {
            partyStructManager.remove(model);
        }

        PartyStructId thePartyStructId = new PartyStructId(partyStructTypeId,
                parentEntityId, childEntityId);
        model = new PartyStruct();
        model.setId(thePartyStructId);
        partyStructManager.save(model);
        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        List<PartyStructId> ids = new ArrayList<PartyStructId>();

        for (String id : selectedItem) {
            ids.add(convertPartyStructId(id));
        }

        partyStructManager.removeAll(partyStructManager.findByIds(ids));
        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        partyStructTypes = partyStructTypeManager.getAll();
        partyEntities = partyEntityManager.getAll();

        if (partyStructId != null) {
            this.model = convertPartyStruct(partyStructId);
        }

        return INPUT;
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public PartyStruct getModel() {
        return model;
    }

    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
    }

    public void setPartyStructManager(PartyStructManager partyStructManager) {
        this.partyStructManager = partyStructManager;
    }

    public void setPartyStructTypeManager(
            PartyStructTypeManager partyStructTypeManager) {
        this.partyStructTypeManager = partyStructTypeManager;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    protected PartyStructId convertPartyStructId(String id) {
        String[] array = id.split(",");
        partyStructTypeId = Long.parseLong(array[0]);
        parentEntityId = Long.parseLong(array[1]);
        childEntityId = Long.parseLong(array[2]);

        PartyStructId thePartyStructId = new PartyStructId(partyStructTypeId,
                parentEntityId, childEntityId);

        return thePartyStructId;
    }

    protected PartyStruct convertPartyStruct(String id) {
        return partyStructManager.get(convertPartyStructId(id));
    }

    // ~ ======================================================================
    public Page getPage() {
        return page;
    }

    public void setSelectedItem(List<String> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public List<PartyStructType> getPartyStructTypes() {
        return partyStructTypes;
    }

    public List<PartyEntity> getPartyEntities() {
        return partyEntities;
    }

    public void setPartyStructTypeId(long partyStructTypeId) {
        this.partyStructTypeId = partyStructTypeId;
    }

    public void setParentEntityId(long parentEntityId) {
        this.parentEntityId = parentEntityId;
    }

    public void setChildEntityId(long childEntityId) {
        this.childEntityId = childEntityId;
    }

    public void setPartyStructId(String partyStructId) {
        this.partyStructId = partyStructId;
    }
}
