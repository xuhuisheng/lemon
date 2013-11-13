package com.mossle.bpm.web.bpm;

import java.util.ArrayList;
import java.util.List;

import com.mossle.api.ScopeConnector;

import com.mossle.bpm.persistence.domain.BpmCategory;
import com.mossle.bpm.persistence.manager.BpmCategoryManager;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.scope.ScopeHolder;
import com.mossle.core.struts2.BaseAction;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = BpmCategoryAction.RELOAD, location = "bpm-category.do?operationMode=RETRIEVE", type = "redirect") })
public class BpmCategoryAction extends BaseAction implements
        ModelDriven<BpmCategory>, Preparable {
    public static final String RELOAD = "reload";
    private BpmCategoryManager bpmCategoryManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private BpmCategory model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = bpmCategoryManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new BpmCategory();
    }

    public String save() {
        BpmCategory dest = null;

        if (id > 0) {
            dest = bpmCategoryManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;
        }

        bpmCategoryManager.save(dest);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        List<BpmCategory> bpmCategories = bpmCategoryManager
                .findByIds(selectedItem);
        bpmCategoryManager.removeAll(bpmCategories);
        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = bpmCategoryManager.get(id);
        }

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = bpmCategoryManager.pagedQuery(page, propertyFilters);

        List<BpmCategory> bpmCategories = (List<BpmCategory>) page.getResult();
        TableModel tableModel = new TableModel();
        tableModel.setName("bpm-category");
        tableModel.addHeaders("id", "name");
        tableModel.setData(bpmCategories);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public BpmCategory getModel() {
        return model;
    }

    public void setBpmCategoryManager(BpmCategoryManager bpmCategoryManager) {
        this.bpmCategoryManager = bpmCategoryManager;
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
