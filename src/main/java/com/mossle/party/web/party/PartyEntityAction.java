package com.mossle.party.web.party;

import java.util.ArrayList;
import java.util.List;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.struts2.BaseAction;

import com.mossle.party.domain.PartyEntity;
import com.mossle.party.domain.PartyType;
import com.mossle.party.manager.PartyEntityManager;
import com.mossle.party.manager.PartyTypeManager;
import com.mossle.party.support.PartyEntityConverter;
import com.mossle.party.support.PartyEntityDTO;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = PartyEntityAction.RELOAD, location = "party-entity.do?operationMode=RETRIEVE", type = "redirect") })
public class PartyEntityAction extends BaseAction implements
        ModelDriven<PartyEntity>, Preparable {
    public static final String RELOAD = "reload";
    private PartyEntityManager partyEntityManager;
    private PartyTypeManager partyTypeManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private PartyEntity model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private List<PartyType> partyTypes;
    private long partyTypeId;
    private PartyEntityConverter partyEntityConverter = new PartyEntityConverter();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = partyEntityManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new PartyEntity();

        model.setPartyType(partyTypeManager.get(partyTypeId));
    }

    public String save() {
        PartyEntity dest = null;

        if (id > 0) {
            dest = partyEntityManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;
        }

        partyEntityManager.save(dest);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        partyEntityManager
                .removeAll(partyEntityManager.findByIds(selectedItem));
        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = partyEntityManager.get(id);
        }

        partyTypes = partyTypeManager.getAll();

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = partyEntityManager.pagedQuery(page, propertyFilters);

        List<PartyEntity> partyEntities = (List<PartyEntity>) page.getResult();
        List<PartyEntityDTO> partyDtos = partyEntityConverter
                .createPartyEntityDtos(partyEntities);
        TableModel tableModel = new TableModel();
        tableModel.setName("party entity");
        tableModel.addHeaders("id", "type", "code", "name");
        tableModel.setData(partyDtos);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public PartyEntity getModel() {
        return model;
    }

    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
    }

    public void setPartyTypeManager(PartyTypeManager partyTypeManager) {
        this.partyTypeManager = partyTypeManager;
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

    public List<PartyType> getPartyTypes() {
        return partyTypes;
    }

    public void setPartyTypeId(long partyTypeId) {
        this.partyTypeId = partyTypeId;
    }
}
