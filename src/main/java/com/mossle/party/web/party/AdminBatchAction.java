package com.mossle.party.web.party;

import java.util.ArrayList;
import java.util.List;

import com.mossle.api.scope.ScopeHolder;
import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;

import com.mossle.core.page.Page;
import com.mossle.core.struts2.BaseAction;

import com.mossle.party.domain.PartyEntity;
import com.mossle.party.domain.PartyStruct;
import com.mossle.party.domain.PartyStructId;
import com.mossle.party.manager.PartyEntityManager;
import com.mossle.party.manager.PartyStructManager;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = AdminBatchAction.RELOAD, location = "admin-batch.do?operationMode=RETRIEVE&id=${id}&groupTypeId=${groupTypeId}", type = "redirect") })
public class AdminBatchAction extends BaseAction implements
        ModelDriven<PartyEntity>, Preparable {
    public static final String RELOAD = "reload";
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private long id;
    private Long groupTypeId;
    private String text;
    private List<UserDTO> userDtos = new ArrayList<UserDTO>();
    private List<Long> userIds;
    private UserConnector userConnector;
    private PartyEntityManager partyEntityManager;
    private PartyStructManager partyStructManager;
    private List<PartyEntity> partyEntities;
    private List<Long> selectedItem = new ArrayList<Long>();

    public String execute() {
        String hql = "select c from PartyEntity c join c.parentStructs p"
                + " where p.parentEntity.reference=? and p.parentEntity.partyType=2 and c.partyType.id=1";
        partyEntities = partyEntityManager.find(hql, Long.toString(id));

        return SUCCESS;
    }

    public String input() {
        if (text != null) {
            for (String str : text.split("\n")) {
                str = str.trim();

                if (str.length() == 0) {
                    continue;
                }

                UserDTO userDto = userConnector.findByUsername(str,
                        ScopeHolder.getUserRepoRef());

                if (userDto.getStatus() != 1) {
                    continue;
                }

                userDtos.add(userDto);
            }
        }

        return INPUT;
    }

    public String save() {
        PartyEntity group = partyEntityManager.findUnique(
                "from PartyEntity where partyType.id=2 and reference=?",
                Long.toString(id));

        for (Long userId : userIds) {
            PartyEntity user = partyEntityManager.findUnique(
                    "from PartyEntity where partyType.id=1 and reference=?",
                    Long.toString(userId));

            PartyStruct partyStruct = partyStructManager
                    .findUnique(
                            "from PartyStruct where partyStructType.id=2 and parentEntity=? and childEntity=?",
                            group, user);

            if (partyStruct == null) {
                PartyStructId partyStructId = new PartyStructId(2L,
                        group.getId(), user.getId());
                partyStruct = new PartyStruct();
                partyStruct.setId(partyStructId);
                partyStructManager.save(partyStruct);
            }
        }

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        PartyEntity group = partyEntityManager.findUnique(
                "from PartyEntity where partyType.id=2 and reference=?",
                Long.toString(id));

        for (Long userId : selectedItem) {
            PartyEntity user = partyEntityManager.findUnique(
                    "from PartyEntity where partyType.id=1 and reference=?",
                    Long.toString(userId));

            PartyStruct partyStruct = partyStructManager
                    .findUnique(
                            "from PartyStruct where partyStructType.id=2 and parentEntity=? and childEntity=?",
                            group, user);

            if (partyStruct != null) {
                partyStructManager.remove(partyStruct);
            }
        }

        return RELOAD;
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public PartyEntity getModel() {
        return null;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    // ~ ======================================================================
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Page getPage() {
        return page;
    }

    public void setSelectedItem(List<Long> selectedItem) {
        this.selectedItem = selectedItem;
    }

    // ~ ======================================================================
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    public Long getGroupTypeId() {
        return groupTypeId;
    }

    public void setGroupTypeId(Long groupTypeId) {
        this.groupTypeId = groupTypeId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<UserDTO> getUserDtos() {
        return userDtos;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
    }

    public void setPartyStructManager(PartyStructManager partyStructManager) {
        this.partyStructManager = partyStructManager;
    }

    public List<PartyEntity> getPartyEntities() {
        return partyEntities;
    }
}
