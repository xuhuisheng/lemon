package com.mossle.car.web.car;

import java.util.ArrayList;
import java.util.List;

import com.mossle.api.UserConnector;

import com.mossle.car.domain.CarInfo;
import com.mossle.car.manager.CarInfoManager;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.struts2.BaseAction;

import com.mossle.security.util.SpringSecurityUtils;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = CarInfoAction.RELOAD, location = "car-info.do?operationMode=RETRIEVE", type = "redirect") })
public class CarInfoAction extends BaseAction implements ModelDriven<CarInfo>,
        Preparable {
    public static final String RELOAD = "reload";
    private CarInfoManager carInfoManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private CarInfo model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = carInfoManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new CarInfo();
    }

    public String save() {
        CarInfo dest = null;

        if (id > 0) {
            dest = carInfoManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;
        }

        carInfoManager.save(dest);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        List<CarInfo> carInfos = carInfoManager.findByIds(selectedItem);

        carInfoManager.removeAll(carInfos);
        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = carInfoManager.get(id);
        }

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = carInfoManager.pagedQuery(page, propertyFilters);

        List<CarInfo> carInfos = (List<CarInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("car info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(carInfos);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public CarInfo getModel() {
        return model;
    }

    public void setCarInfoManager(CarInfoManager carInfoManager) {
        this.carInfoManager = carInfoManager;
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

    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }
}
