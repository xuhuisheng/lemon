package com.mossle.party.web.party;

import java.util.ArrayList;
import java.util.List;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.struts2.BaseAction;

import com.mossle.party.domain.PartyDim;
import com.mossle.party.manager.PartyDimManager;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = PartyDimAction.RELOAD, location = "party-dim-root.do?operationMode=RETRIEVE", type = "redirect") })
public class PartyDimAction extends BaseAction implements
        ModelDriven<PartyDim>, Preparable {
    public static final String RELOAD = "reload";
    private PartyDimManager partyDimManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private PartyDim model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private BeanMapper beanMapper = new BeanMapper();

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = partyDimManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new PartyDim();
    }

    public String save() {
        PartyDim dest = null;

        if (id > 0) {
            dest = partyDimManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;
        }

        partyDimManager.save(dest);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        partyDimManager.removeAll(partyDimManager.findByIds(selectedItem));
        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = partyDimManager.get(id);
        }

        return INPUT;
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public PartyDim getModel() {
        return model;
    }

    public void setPartyDimManager(PartyDimManager partyDimManager) {
        this.partyDimManager = partyDimManager;
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
}
