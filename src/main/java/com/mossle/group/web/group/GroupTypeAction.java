package com.mossle.group.web.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mossle.api.ScopeConnector;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.scope.ScopeHolder;
import com.mossle.core.struts2.BaseAction;

import com.mossle.group.component.GroupTypeCache;
import com.mossle.group.domain.GroupType;
import com.mossle.group.manager.GroupTypeManager;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = GroupTypeAction.RELOAD, location = "group-type.do?operationMode=RETRIEVE", type = "redirect") })
public class GroupTypeAction extends BaseAction implements
        ModelDriven<GroupType>, Preparable {
    public static final String RELOAD = "reload";
    private GroupTypeManager groupTypeManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private GroupType model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();
    private ScopeConnector scopeConnector;
    private GroupTypeCache groupTypeCache;

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
        page = groupTypeManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new GroupType();
    }

    public String save() {
        Long globalId = scopeConnector
                .findGlobalId(ScopeHolder.getGlobalCode());
        Long localId = scopeConnector.findLocalId(ScopeHolder.getGlobalCode(),
                ScopeHolder.getLocalCode());
        GroupType dest = null;

        if (id > 0) {
            dest = groupTypeManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;
        }

        if (id == 0) {
            dest.setGlobalId(globalId);
            dest.setLocalId(localId);
        }

        groupTypeManager.save(dest);
        groupTypeCache.refreshScope(globalId);
        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        List<GroupType> groupTypes = groupTypeManager.findByIds(selectedItem);

        for (GroupType groupType : groupTypes) {
            groupTypeManager.remove(groupType);
        }

        Long globalId = scopeConnector
                .findGlobalId(ScopeHolder.getGlobalCode());
        groupTypeCache.refreshScope(globalId);
        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = groupTypeManager.get(id);
        }

        return INPUT;
    }

    public String search() {
        StringBuilder buff = new StringBuilder("select ub from GroupType ub");

        Map<String, Object> params = new HashMap<String, Object>();

        if (!params.isEmpty()) {
            page = groupTypeManager.pagedQuery(buff.toString(),
                    page.getPageNo(), page.getPageSize(), params);
        }

        return "search";
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = groupTypeManager.pagedQuery(page, propertyFilters);

        List<GroupType> groupTypes = (List<GroupType>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("group type");
        tableModel.addHeaders("id", "name");
        tableModel.setData(groupTypes);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public GroupType getModel() {
        return model;
    }

    public void setGroupTypeManager(GroupTypeManager groupTypeManager) {
        this.groupTypeManager = groupTypeManager;
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

    public void setGroupTypeCache(GroupTypeCache groupTypeCache) {
        this.groupTypeCache = groupTypeCache;
    }
}
