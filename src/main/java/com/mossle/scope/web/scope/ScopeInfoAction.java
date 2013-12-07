package com.mossle.scope.web.scope;

import java.util.ArrayList;
import java.util.List;

import com.mossle.api.UserRepoConnector;
import com.mossle.api.UserRepoDTO;
import com.mossle.api.scope.ScopeCache;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.struts2.BaseAction;

import com.mossle.scope.domain.ScopeInfo;
import com.mossle.scope.manager.ScopeInfoManager;
import com.mossle.scope.support.ScopeInfoDTO;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = ScopeInfoAction.RELOAD, location = "scope-info.do?operationMode=RETRIEVE", type = "redirect") })
public class ScopeInfoAction extends BaseAction implements
        ModelDriven<ScopeInfo>, Preparable {
    public static final String RELOAD = "reload";
    private ScopeInfoManager scopeInfoManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private ScopeInfo model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();
    private String name;
    private List<UserRepoDTO> userRepoDtos;
    private UserRepoConnector userRepoConnector;
    private ScopeCache scopeCache;

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = scopeInfoManager.pagedQuery(page, propertyFilters);

        List<ScopeInfo> scopeInfos = (List<ScopeInfo>) page.getResult();
        List<ScopeInfoDTO> scopeInfoDtos = new ArrayList<ScopeInfoDTO>();

        for (ScopeInfo scopeInfo : scopeInfos) {
            ScopeInfoDTO scopeInfoDto = new ScopeInfoDTO();
            beanMapper.copy(scopeInfo, scopeInfoDto);

            String userRepoRef = scopeInfoDto.getUserRepoRef();
            UserRepoDTO userRepoDto = userRepoConnector.findById(userRepoRef);

            if (userRepoDto != null) {
                scopeInfoDto.setUserRepoCode(userRepoDto.getCode());
            }

            scopeInfoDtos.add(scopeInfoDto);
        }

        page.setResult(scopeInfoDtos);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new ScopeInfo();
    }

    public String save() {
        ScopeInfo dest = null;

        if (id > 0) {
            dest = scopeInfoManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;
        }

        scopeInfoManager.save(dest);
        scopeCache.refresh();

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        List<ScopeInfo> scopeInfos = scopeInfoManager.findByIds(selectedItem);

        scopeInfoManager.removeAll(scopeInfos);
        scopeCache.refresh();
        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = scopeInfoManager.get(id);
        }

        userRepoDtos = userRepoConnector.findAll();

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = scopeInfoManager.pagedQuery(page, propertyFilters);

        List<ScopeInfo> scopeInfos = (List<ScopeInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("scopeInfo");
        tableModel.addHeaders("id", "name");
        tableModel.setData(scopeInfos);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    public void checkName() throws Exception {
        String hql = "from ScopeInfo where name=?";
        Object[] params = { name };

        if (id != 0L) {
            hql = "from ScopeInfo where name=? and id<>?";
            params = new Object[] { name, id };
        }

        ScopeInfo scopeInfo = scopeInfoManager.findUnique(hql, params);

        boolean result = (scopeInfo == null);
        ServletActionContext.getResponse().getWriter()
                .write(Boolean.toString(result));
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public ScopeInfo getModel() {
        return model;
    }

    public void setScopeInfoManager(ScopeInfoManager scopeInfoManager) {
        this.scopeInfoManager = scopeInfoManager;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public void setUserRepoConnector(UserRepoConnector userRepoConnector) {
        this.userRepoConnector = userRepoConnector;
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
    public List<UserRepoDTO> getUserRepoDtos() {
        return userRepoDtos;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScopeCache(ScopeCache scopeCache) {
        this.scopeCache = scopeCache;
    }
}
