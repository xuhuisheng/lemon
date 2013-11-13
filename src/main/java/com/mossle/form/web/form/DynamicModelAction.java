package com.mossle.form.web.form;

import java.util.ArrayList;
import java.util.List;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.struts2.BaseAction;

import com.mossle.form.domain.DynamicModel;
import com.mossle.form.manager.DynamicModelManager;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = DynamicModelAction.RELOAD, location = "dynamic-model.do?operationMode=RETRIEVE", type = "redirect") })
public class DynamicModelAction extends BaseAction implements
        ModelDriven<DynamicModel>, Preparable {
    public static final String RELOAD = "reload";
    private DynamicModelManager dynamicModelManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private DynamicModel model;
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
        page = dynamicModelManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new DynamicModel();
        model.setStatus(0);
    }

    public String save() {
        DynamicModel dest = null;

        if (id > 0) {
            dest = dynamicModelManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;
        }

        dynamicModelManager.save(dest);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        List<DynamicModel> dynamicModels = dynamicModelManager
                .findByIds(selectedItem);

        dynamicModelManager.removeAll(dynamicModels);
        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = dynamicModelManager.get(id);
        }

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = dynamicModelManager.pagedQuery(page, propertyFilters);

        List<DynamicModel> dynamicModels = (List<DynamicModel>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("dynamic model");
        tableModel.addHeaders("id", "name");
        tableModel.setData(dynamicModels);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public DynamicModel getModel() {
        return model;
    }

    public void setDynamicModelManager(DynamicModelManager dynamicModelManager) {
        this.dynamicModelManager = dynamicModelManager;
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
}
