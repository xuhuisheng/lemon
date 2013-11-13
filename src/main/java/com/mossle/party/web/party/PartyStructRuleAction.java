package com.mossle.party.web.party;

import java.util.ArrayList;
import java.util.List;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.page.Page;
import com.mossle.core.struts2.BaseAction;

import com.mossle.party.domain.PartyStructRule;
import com.mossle.party.domain.PartyStructRuleId;
import com.mossle.party.domain.PartyStructType;
import com.mossle.party.domain.PartyType;
import com.mossle.party.manager.PartyStructRuleManager;
import com.mossle.party.manager.PartyStructTypeManager;
import com.mossle.party.manager.PartyTypeManager;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = PartyStructRuleAction.RELOAD, location = "party-struct-rule.do?operationMode=RETRIEVE", type = "redirect") })
public class PartyStructRuleAction extends BaseAction implements
        ModelDriven<PartyStructRule>, Preparable {
    public static final String RELOAD = "reload";
    private PartyStructRuleManager partyStructRuleManager;
    private PartyStructTypeManager partyStructTypeManager;
    private PartyTypeManager partyTypeManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private PartyStructRule model;
    private List<String> selectedItem = new ArrayList<String>();
    private List<PartyStructType> partyStructTypes;
    private List<PartyType> partyTypes;
    private long partyStructTypeId;
    private long parentTypeId;
    private long childTypeId;
    private String partyStructRuleId;

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = partyStructRuleManager.pagedQuery(page, propertyFilters);

        partyStructTypes = partyStructTypeManager.getAll();

        return SUCCESS;
    }

    public void prepareSave() {
        if (partyStructRuleId != null) {
            model = convertPartyStructRule(partyStructRuleId);
        }
    }

    public String save() {
        if (partyStructRuleId != null) {
            partyStructRuleManager.remove(model);
        }

        model = new PartyStructRule();

        PartyStructRuleId thePartyStructRuleId = new PartyStructRuleId(
                partyStructTypeId, parentTypeId, childTypeId);
        model.setId(thePartyStructRuleId);
        partyStructRuleManager.save(model);
        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        List<PartyStructRuleId> ids = new ArrayList<PartyStructRuleId>();

        for (String id : selectedItem) {
            ids.add(convertPartyStructRuleId(id));
        }

        partyStructRuleManager.removeAll(partyStructRuleManager.findByIds(ids));
        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        partyStructTypes = partyStructTypeManager.getAll();
        partyTypes = partyTypeManager.getAll();

        if (partyStructRuleId != null) {
            model = convertPartyStructRule(partyStructRuleId);
        }

        return INPUT;
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public PartyStructRule getModel() {
        return model;
    }

    public void setPartyStructRuleManager(
            PartyStructRuleManager partyStructRuleManager) {
        this.partyStructRuleManager = partyStructRuleManager;
    }

    public void setPartyStructTypeManager(
            PartyStructTypeManager partyStructTypeManager) {
        this.partyStructTypeManager = partyStructTypeManager;
    }

    public void setPartyTypeManager(PartyTypeManager partyTypeManager) {
        this.partyTypeManager = partyTypeManager;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    protected PartyStructRuleId convertPartyStructRuleId(String id) {
        String[] array = id.split(",");
        partyStructTypeId = Long.parseLong(array[0]);
        parentTypeId = Long.parseLong(array[1]);
        childTypeId = Long.parseLong(array[2]);

        PartyStructRuleId thePartyStructRuleId = new PartyStructRuleId(
                partyStructTypeId, parentTypeId, childTypeId);

        return thePartyStructRuleId;
    }

    protected PartyStructRule convertPartyStructRule(String id) {
        return partyStructRuleManager.get(convertPartyStructRuleId(id));
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

    public List<PartyType> getPartyTypes() {
        return partyTypes;
    }

    public void setPartyStructTypeId(long partyStructTypeId) {
        this.partyStructTypeId = partyStructTypeId;
    }

    public void setParentTypeId(long parentTypeId) {
        this.parentTypeId = parentTypeId;
    }

    public void setChildTypeId(long childTypeId) {
        this.childTypeId = childTypeId;
    }

    public void setPartyStructRuleId(String partyStructRuleId) {
        this.partyStructRuleId = partyStructRuleId;
    }
}
