package com.mossle.party.web.party;

import java.util.ArrayList;
import java.util.List;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.struts2.BaseAction;

import com.mossle.party.domain.PartyDim;
import com.mossle.party.domain.PartyDimRoot;
import com.mossle.party.domain.PartyEntity;
import com.mossle.party.manager.PartyDimManager;
import com.mossle.party.manager.PartyDimRootManager;
import com.mossle.party.manager.PartyEntityManager;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = PartyDimRootAction.RELOAD, location = "party-dim-root-root.do?operationMode=RETRIEVE", type = "redirect") })
public class PartyDimRootAction extends BaseAction implements
        ModelDriven<PartyDimRoot>, Preparable {
    public static final String RELOAD = "reload";
    private PartyDimRootManager partyDimRootManager;
    private PartyDimManager partyDimManager;
    private PartyEntityManager partyEntityManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private PartyDimRoot model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private BeanMapper beanMapper = new BeanMapper();
    private List<PartyEntity> partyEntities;
    private List<PartyDim> partyDims;
    private long partyEntityId;
    private long partyDimId;

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = partyDimRootManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new PartyDimRoot();
    }

    public String save() {
        PartyDimRoot dest = null;

        if (id > 0) {
            dest = partyDimRootManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;
        }

        dest.setPartyEntity(partyEntityManager.get(partyEntityId));
        dest.setPartyDim(partyDimManager.get(partyDimId));
        partyDimRootManager.save(dest);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        partyDimRootManager.removeAll(partyDimRootManager
                .findByIds(selectedItem));
        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = partyDimRootManager.get(id);
        }

        partyEntities = partyEntityManager.getAll();
        partyDims = partyDimManager.getAll();

        return INPUT;
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public PartyDimRoot getModel() {
        return model;
    }

    public void setPartyDimRootManager(PartyDimRootManager partyDimRootManager) {
        this.partyDimRootManager = partyDimRootManager;
    }

    public void setPartyDimManager(PartyDimManager partyDimManager) {
        this.partyDimManager = partyDimManager;
    }

    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
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

    public List<PartyEntity> getPartyEntities() {
        return partyEntities;
    }

    public List<PartyDim> getPartyDims() {
        return partyDims;
    }

    public void setPartyEntityId(long partyEntityId) {
        this.partyEntityId = partyEntityId;
    }

    public void setPartyDimId(long partyDimId) {
        this.partyDimId = partyDimId;
    }
}
