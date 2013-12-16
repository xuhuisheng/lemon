package com.mossle.group.web.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mossle.api.UserConnector;
import com.mossle.api.UserDTO;
import com.mossle.api.scope.ScopeHolder;

import com.mossle.core.page.Page;
import com.mossle.core.struts2.BaseAction;

import com.mossle.party.domain.PartyDim;
import com.mossle.party.domain.PartyEntity;
import com.mossle.party.domain.PartyStruct;
import com.mossle.party.domain.PartyStructId;
import com.mossle.party.domain.PartyStructType;
import com.mossle.party.domain.PartyType;
import com.mossle.party.manager.PartyDimManager;
import com.mossle.party.manager.PartyEntityManager;
import com.mossle.party.manager.PartyStructManager;
import com.mossle.party.manager.PartyStructTypeManager;
import com.mossle.party.manager.PartyTypeManager;
import com.mossle.party.manager.PartyTypeManager;

import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

@Results({
        @Result(name = OrgAction.RELOAD, location = "org.do?operationMode=RETRIEVE", type = "redirect"),
        @Result(name = OrgAction.RELOAD_CHILD, location = "org!children.do?operationMode=RETRIEVE&partyDimId=${partyDimId}&partyEntityId=${partyEntityId}", type = "redirect"),
        @Result(name = OrgAction.RELOAD_USER, location = "org!users.do?operationMode=RETRIEVE&partyDimId=${partyDimId}&partyEntityId=${partyEntityId}", type = "redirect") })
public class OrgAction extends BaseAction {
    public static final String RELOAD = "reload";
    public static final String RELOAD_CHILD = "reload-child";
    public static final String RELOAD_USER = "reload-user";
    private PartyDimManager partyDimManager;
    private PartyEntityManager partyEntityManager;
    private PartyTypeManager partyTypeManager;
    private PartyStructManager partyStructManager;
    private PartyStructTypeManager partyStructTypeManager;
    private UserConnector userConnector;
    private List<PartyDim> partyDims;
    private long partyDimId;
    private PartyDim partyDim;
    private Page page = new Page();
    private long partyEntityId;
    private String name;
    private List<Long> selectedItem = new ArrayList<Long>();
    private List<PartyType> partyTypes;
    private long partyTypeId;
    private int status;
    private long partyStructTypeId;
    private List<PartyStructType> partyStructTypes;

    public void init() {
        partyDims = partyDimManager.getAll("priority", true);

        if (partyDimId == 0L) {
            partyDim = partyDims.get(0);
            partyDimId = partyDim.getId();
        } else {
            partyDim = partyDimManager.get(partyDimId);
        }

        if (partyEntityId == 0L) {
            partyEntityId = partyDim.getPartyDimRoots().iterator().next()
                    .getPartyEntity().getId();
        }
    }

    public String execute() {
        return users();
    }

    public String users() {
        init();

        String hql = "from PartyStruct where childEntity.partyType.person=1 and parentEntity.id=?";
        page = partyDimManager.pagedQuery(hql, page.getPageNo(),
                page.getPageSize(), partyEntityId);

        return "users";
    }

    public String inputUser() throws Exception {
        init();
        partyStructTypes = partyStructTypeManager.getAll();

        return "inputUser";
    }

    public String saveUser() throws Exception {
        UserDTO userDto = userConnector.findByUsername(name,
                ScopeHolder.getUserRepoRef());
        PartyEntity child = partyEntityManager.findUnique(
                "from PartyEntity where partyType.person=1 and ref=?",
                userDto.getId());
        PartyEntity parent = partyEntityManager.findUnique(
                "from PartyEntity where partyType.person<>1 and ref=?",
                Long.toString(partyEntityId));

        PartyStruct partyStruct = new PartyStruct();
        PartyStructId partyStructId = new PartyStructId(partyStructTypeId,
                parent.getId(), child.getId());
        partyStruct.setId(partyStructId);
        partyStruct.setPartyDim(partyDimManager.get(partyDimId));
        partyStruct.setStatus(status);
        partyStructManager.save(partyStruct);

        return RELOAD_USER;
    }

    public String removeUser() {
        for (Long childId : selectedItem) {
            PartyEntity parent = partyEntityManager.findUnique(
                    "from PartyEntity where partyType.person<>1 and id=?",
                    partyEntityId);
            PartyEntity child = partyEntityManager.findUnique(
                    "from PartyEntity where partyType.person=1 and id=?",
                    childId);

            PartyStructId partyStructId = new PartyStructId(partyStructTypeId,
                    parent.getId(), child.getId());

            PartyStruct partyStruct = partyStructManager.get(partyStructId);
            partyStructManager.remove(partyStruct);
        }

        // addActionMessage(messages.getMessage("core.success.delete", "删除成功"));
        return RELOAD_USER;
    }

    // ~ ==================================================
    public String children() throws Exception {
        init();

        String hql = "select child from PartyEntity child join child.parentStructs ps join ps.parentEntity parent"
                + " where child.partyType.person<>1 and parent.id=?";
        page = partyEntityManager.pagedQuery(hql, page.getPageNo(),
                page.getPageSize(), partyEntityId);

        return "children";
    }

    public String inputChild() throws Exception {
        init();
        partyTypes = partyTypeManager.find("from PartyType where person<>1");

        return "inputChild";
    }

    public String saveChild() {
        PartyEntity child = partyEntityManager.findUnique(
                "from PartyEntity where name=? and partyType.id=?", name,
                partyTypeId);
        PartyEntity parent = partyEntityManager.findUnique(
                "from PartyEntity where partyType.person<>1 and ref=?",
                Long.toString(partyEntityId));
        PartyStruct partyStruct = new PartyStruct();
        PartyStructId partyStructId = new PartyStructId(1L, parent.getId(),
                child.getId());
        partyStruct.setId(partyStructId);
        partyStruct.setPartyDim(partyDimManager.get(partyDimId));
        partyStructManager.save(partyStruct);

        return RELOAD_CHILD;
    }

    public String removeChild() {
        for (Long childId : selectedItem) {
            PartyEntity parent = partyEntityManager.findUnique(
                    "from PartyEntity where partyType.person<>1 and id=?",
                    partyEntityId);
            PartyEntity child = partyEntityManager.findUnique(
                    "from PartyEntity where partyType.person<>1 and id=?",
                    childId);

            PartyStructId partyStructId = new PartyStructId(1L, parent.getId(),
                    child.getId());
            PartyStruct partyStruct = partyStructManager.get(partyStructId);
            partyStructManager.remove(partyStruct);
        }

        return RELOAD_CHILD;
    }

    // ~ ==================================================
    public void setPartyDimManager(PartyDimManager partyDimManager) {
        this.partyDimManager = partyDimManager;
    }

    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
    }

    public void setPartyStructManager(PartyStructManager partyStructManager) {
        this.partyStructManager = partyStructManager;
    }

    public void setPartyTypeManager(PartyTypeManager partyTypeManager) {
        this.partyTypeManager = partyTypeManager;
    }

    public void setPartyStructTypeManager(
            PartyStructTypeManager partyStructTypeManager) {
        this.partyStructTypeManager = partyStructTypeManager;
    }

    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    // ~ ==================================================
    public List<PartyDim> getPartyDims() {
        return partyDims;
    }

    public PartyDim getPartyDim() {
        return partyDim;
    }

    public Page getPage() {
        return page;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setStatus(int status) {
        this.status = status;
    }

    public void setPartyStructTypeId(long partyStructTypeId) {
        this.partyStructTypeId = partyStructTypeId;
    }

    public List<PartyStructType> getPartyStructTypes() {
        return partyStructTypes;
    }

    // ~ ==================================================
    public long getPartyDimId() {
        return partyDimId;
    }

    public void setPartyDimId(long partyDimId) {
        this.partyDimId = partyDimId;
    }

    public long getPartyEntityId() {
        return partyEntityId;
    }

    public void setPartyEntityId(long partyEntityId) {
        this.partyEntityId = partyEntityId;
    }
}
