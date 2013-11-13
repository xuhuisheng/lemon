package com.mossle.group.web.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mossle.api.ScopeConnector;
import com.mossle.api.UserConnector;
import com.mossle.api.UserDTO;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.scope.ScopeHolder;
import com.mossle.core.struts2.BaseAction;

import com.mossle.group.domain.GroupBase;
import com.mossle.group.domain.GroupType;
import com.mossle.group.manager.GroupBaseManager;
import com.mossle.group.manager.GroupTypeManager;
import com.mossle.group.service.GroupService;

import com.mossle.party.domain.*;
import com.mossle.party.manager.*;
import com.mossle.party.service.PartyService;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({
        @Result(name = GroupBaseAction.RELOAD, location = "group-base.do?operationMode=RETRIEVE", type = "redirect"),
        @Result(name = GroupBaseAction.RELOAD_CHILD, location = "group-base!pagedQueryChildren.do?operationMode=RETRIEVE&groupBaseId=${groupBaseId}", type = "redirect"),
        @Result(name = GroupBaseAction.RELOAD_USER, location = "group-base!pagedQueryUser.do?operationMode=RETRIEVE&groupBaseId=${groupBaseId}", type = "redirect") })
public class GroupBaseAction extends BaseAction implements
        ModelDriven<GroupBase>, Preparable {
    public static final String RELOAD = "reload";
    public static final String RELOAD_CHILD = "reload-child";
    public static final String RELOAD_USER = "reload-user";
    private GroupBaseManager groupBaseManager;
    private GroupTypeManager groupTypeManager;
    private PartyEntityManager partyEntityManager;
    private PartyTypeManager partyTypeManager;
    private PartyStructManager partyStructManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private GroupBase model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();
    private ScopeConnector scopeConnector;
    private List<GroupType> groupTypes;
    private Long groupTypeId;
    private GroupService groupService;
    private PartyService partyService;
    private Long groupBaseId;
    private UserConnector userConnector;
    private String username;

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        Long globalId = scopeConnector
                .findGlobalId(ScopeHolder.getGlobalCode());
        propertyFilters.add(new PropertyFilter("EQL_globalId", Long
                .toString(globalId)));
        page = groupBaseManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new GroupBase();
        model.setStatus(0);
    }

    public void prepareSaveChild() {
        model = new GroupBase();
        model.setStatus(0);
    }

    public void prepareSaveGroup() {
        model = new GroupBase();
        model.setStatus(0);
    }

    public void prepareSaveRoot() {
        model = new GroupBase();
        model.setStatus(0);
    }

    public String save() {
        GroupBase dest = null;

        if (id > 0) {
            dest = groupBaseManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;
        }

        dest.setGroupType(groupTypeManager.get(groupTypeId));

        if (id == 0) {
            dest.setGlobalId(scopeConnector.findGlobalId(ScopeHolder
                    .getGlobalCode()));
            dest.setLocalId(scopeConnector.findLocalId(
                    ScopeHolder.getGlobalCode(), ScopeHolder.getLocalCode()));
            groupService.insert(dest);
        } else {
            groupService.update(dest);
        }

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        List<GroupBase> groupBases = groupBaseManager.findByIds(selectedItem);

        for (GroupBase groupBase : groupBases) {
            groupService.remove(groupBase);
        }

        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = groupBaseManager.get(id);
        }

        Long globalId = scopeConnector
                .findGlobalId(ScopeHolder.getGlobalCode());
        groupTypes = groupTypeManager.findBy("globalId", globalId);

        return INPUT;
    }

    public String search() {
        StringBuilder buff = new StringBuilder("select ub from GroupBase ub");

        Map<String, Object> params = new HashMap<String, Object>();

        if (!params.isEmpty()) {
            page = groupBaseManager.pagedQuery(buff.toString(),
                    page.getPageNo(), page.getPageSize(), params);
        }

        return "search";
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = groupBaseManager.pagedQuery(page, propertyFilters);

        List<GroupBase> groupBases = (List<GroupBase>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("org");
        tableModel.addHeaders("id", "orgname", "enabled", "description");
        tableModel.setData(groupBases);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    // ~ ======================================================================
    public String pagedQueryChildren() throws Exception {
        if (groupBaseId == null) {
            String defaultRootPartyEntityReference = partyService
                    .getDefaultRootPartyEntityReference();
            groupBaseId = Long.parseLong(defaultRootPartyEntityReference);
        }

        String hql = "select gb from GroupBase gb,PartyEntity child join child.parentStructs ps join ps.parentEntity parent"
                + " where gb.id=child.reference and child.partyType.id<>1 and parent.reference=?";
        page = groupBaseManager.pagedQuery(hql, page.getPageNo(),
                page.getPageSize(), Long.toString(groupBaseId));

        return "pagedQueryChildren";
    }

    public String inputChild() throws Exception {
        if (id > 0) {
            model = groupBaseManager.get(id);
        }

        Long globalId = scopeConnector
                .findGlobalId(ScopeHolder.getGlobalCode());
        groupTypes = groupTypeManager.findBy("globalId", globalId);

        return "inputChild";
    }

    public String saveChild() {
        GroupBase dest = null;

        if (id > 0) {
            dest = groupBaseManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;
        }

        dest.setGroupType(groupTypeManager.get(groupTypeId));

        if (id == 0) {
            dest.setGlobalId(scopeConnector.findGlobalId(ScopeHolder
                    .getGlobalCode()));
            dest.setLocalId(scopeConnector.findLocalId(
                    ScopeHolder.getGlobalCode(), ScopeHolder.getLocalCode()));
            groupService.insert(dest);
        } else {
            groupService.update(dest);
        }

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        if (id == 0) {
            PartyEntity parent = partyEntityManager.findUnique(
                    "from PartyEntity where partyType<>1 and reference=?",
                    Long.toString(groupBaseId));
            PartyEntity child = new PartyEntity();
            child.setName(dest.getName());
            child.setReference(Long.toString(dest.getId()));
            child.setPartyType(partyTypeManager.get(2L));
            partyEntityManager.save(child);

            PartyStruct partyStruct = new PartyStruct();
            PartyStructId partyStructId = new PartyStructId(1L, parent.getId(),
                    child.getId());
            partyStruct.setId(partyStructId);
            partyStructManager.save(partyStruct);
        }

        return RELOAD_CHILD;
    }

    public String removeChild() {
        List<GroupBase> groupBases = groupBaseManager.findByIds(selectedItem);

        for (GroupBase groupBase : groupBases) {
            long childId = groupBase.getId();
            PartyEntity parent = groupService.findGroup(Long
                    .toString(groupBaseId));
            PartyEntity child = groupService.findGroup(Long.toString(childId));

            PartyStructId partyStructId = new PartyStructId(1L, parent.getId(),
                    child.getId());
            PartyStruct partyStruct = partyStructManager.get(partyStructId);
            partyStructManager.remove(partyStruct);

            if (child.getParentStructs().isEmpty()
                    && child.getChildStructs().isEmpty()) {
                groupService.remove(groupBase);
                partyEntityManager.remove(child);
            }
        }

        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD_CHILD;
    }

    // ~ ======================================================================
    public String pagedQueryUser() throws Exception {
        if (groupBaseId == null) {
            String defaultRootPartyEntityReference = partyService
                    .getDefaultRootPartyEntityReference();
            groupBaseId = Long.parseLong(defaultRootPartyEntityReference);
        }

        String hql = "select ub from UserBase ub,PartyEntity child join child.parentStructs ps join ps.parentEntity parent"
                + " where ub.id=child.reference and child.partyType.id=1 and parent.reference=?";
        page = groupBaseManager.pagedQuery(hql, page.getPageNo(),
                page.getPageSize(), Long.toString(groupBaseId));

        return "pagedQueryUser";
    }

    public String inputUser() throws Exception {
        return "inputUser";
    }

    public String saveUser() throws Exception {
        Long globalId = scopeConnector
                .findGlobalId(ScopeHolder.getGlobalCode());
        UserDTO userDto = userConnector.findByUsername(username, globalId);
        PartyEntity child = partyEntityManager.findUnique(
                "from PartyEntity where partyType.id=1 and reference=?",
                userDto.getId());
        PartyEntity parent = partyEntityManager.findUnique(
                "from PartyEntity where partyType.id<>1 and reference=?",
                Long.toString(groupBaseId));

        PartyStruct partyStruct = new PartyStruct();
        PartyStructId partyStructId = new PartyStructId(1L, parent.getId(),
                child.getId());
        partyStruct.setId(partyStructId);
        partyStructManager.save(partyStruct);

        return RELOAD_USER;
    }

    public String removeUser() {
        for (Long childId : selectedItem) {
            PartyEntity parent = partyEntityManager.findUnique(
                    "from PartyEntity where partyType.id<>1 and reference=?",
                    Long.toString(groupBaseId));
            PartyEntity child = partyEntityManager.findUnique(
                    "from PartyEntity where partyType.id=1 and reference=?",
                    Long.toString(childId));

            PartyStructId partyStructId = new PartyStructId(1L, parent.getId(),
                    child.getId());
            PartyStruct partyStruct = partyStructManager.get(partyStructId);
            partyStructManager.remove(partyStruct);
        }

        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD_USER;
    }

    // ~ ======================================================================
    public String inputGroup() {
        if (groupBaseId > 0) {
            model = groupBaseManager.get(groupBaseId);
        }

        Long globalId = scopeConnector
                .findGlobalId(ScopeHolder.getGlobalCode());
        groupTypes = groupTypeManager.findBy("globalId", globalId);

        PartyEntity child = partyEntityManager.findUnique(
                "from PartyEntity where partyType.id<>1 and reference=?",
                Long.toString(groupBaseId));
        List<PartyStruct> partyStructs = partyStructManager
                .find("from PartyStruct where partyStructType.id=2 and childEntity=?",
                        child);

        for (PartyStruct partyStruct : partyStructs) {
            PartyEntity parent = partyStruct.getParentEntity();
            username = parent.getName();
        }

        return "inputGroup";
    }

    public String saveGroup() {
        GroupBase dest = null;

        dest = groupBaseManager.get(id);
        beanMapper.copy(model, dest);

        dest.setGroupType(groupTypeManager.get(groupTypeId));

        if (id == 0) {
            dest.setGlobalId(scopeConnector.findGlobalId(ScopeHolder
                    .getGlobalCode()));
            dest.setLocalId(scopeConnector.findLocalId(
                    ScopeHolder.getGlobalCode(), ScopeHolder.getLocalCode()));
            groupService.insert(dest);
        } else {
            groupService.update(dest);
        }

        PartyEntity child = partyEntityManager.findUnique(
                "from PartyEntity where partyType.id<>1 and reference=?",
                Long.toString(groupBaseId));
        List<PartyStruct> partyStructs = partyStructManager
                .find("from PartyStruct where partyStructType.id=2 and childEntity=?",
                        child);
        partyStructManager.removeAll(partyStructs);

        if (!"".equals(username)) {
            Long globalId = scopeConnector.findGlobalId(ScopeHolder
                    .getGlobalCode());
            UserDTO userDto = userConnector.findByUsername(username, globalId);
            PartyEntity parent = partyEntityManager.findUnique(
                    "from PartyEntity where partyType.id=1 and reference=?",
                    userDto.getId());

            PartyStructId partyStructId = new PartyStructId(2, parent.getId(),
                    child.getId());
            PartyStruct partyStruct = new PartyStruct();
            partyStruct.setId(partyStructId);
            partyStructManager.save(partyStruct);
        }

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD_CHILD;
    }

    // ~ ======================================================================
    public String inputRoot() {
        Long globalId = scopeConnector
                .findGlobalId(ScopeHolder.getGlobalCode());
        groupTypes = groupTypeManager.findBy("globalId", globalId);

        return "inputRoot";
    }

    public String saveRoot() {
        GroupBase dest = null;

        dest = model;

        dest.setGroupType(groupTypeManager.get(groupTypeId));

        dest.setGlobalId(scopeConnector.findGlobalId(ScopeHolder
                .getGlobalCode()));
        dest.setLocalId(scopeConnector.findLocalId(ScopeHolder.getGlobalCode(),
                ScopeHolder.getLocalCode()));
        groupService.insert(dest);

        PartyEntity child = new PartyEntity();
        child.setName(dest.getName());
        child.setReference(Long.toString(dest.getId()));
        child.setPartyType(partyTypeManager.get(2L));
        partyEntityManager.save(child);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD_USER;
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public GroupBase getModel() {
        return model;
    }

    public void setGroupBaseManager(GroupBaseManager groupBaseManager) {
        this.groupBaseManager = groupBaseManager;
    }

    public void setGroupTypeManager(GroupTypeManager groupTypeManager) {
        this.groupTypeManager = groupTypeManager;
    }

    public void setPartyService(PartyService partyService) {
        this.partyService = partyService;
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

    // ~ ======================================================================
    public void setScopeConnector(ScopeConnector scopeConnector) {
        this.scopeConnector = scopeConnector;
    }

    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    public List<GroupType> getGroupTypes() {
        return groupTypes;
    }

    public void setGroupTypeId(Long groupTypeId) {
        this.groupTypeId = groupTypeId;
    }

    public Long getGroupBaseId() {
        return groupBaseId;
    }

    public void setGroupBaseId(Long groupBaseId) {
        this.groupBaseId = groupBaseId;
    }

    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
    }

    public void setPartyTypeManager(PartyTypeManager partyTypeManager) {
        this.partyTypeManager = partyTypeManager;
    }

    public void setPartyStructManager(PartyStructManager partyStructManager) {
        this.partyStructManager = partyStructManager;
    }

    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
