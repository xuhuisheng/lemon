package com.mossle.cms.web.cms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mossle.api.UserConnector;
import com.mossle.api.scope.ScopeHolder;

import com.mossle.cms.domain.CmsArticle;
import com.mossle.cms.manager.CmsArticleManager;

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

@Results({ @Result(name = CmsArticleAction.RELOAD, location = "cms-article.do?operationMode=RETRIEVE", type = "redirect") })
public class CmsArticleAction extends BaseAction implements
        ModelDriven<CmsArticle>, Preparable {
    public static final String RELOAD = "reload";
    private CmsArticleManager cmsArticleManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private CmsArticle model;
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

        String userId = userConnector.findByUsername(
                SpringSecurityUtils.getCurrentUsername(),
                ScopeHolder.getUserRepoRef()).getId();
        propertyFilters.add(new PropertyFilter("EQL_userId", userId));
        page = cmsArticleManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new CmsArticle();
    }

    public String save() {
        CmsArticle dest = null;

        if (id > 0) {
            dest = cmsArticleManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;

            String userId = userConnector.findByUsername(
                    SpringSecurityUtils.getCurrentUsername(),
                    ScopeHolder.getUserRepoRef()).getId();
            dest.setUserId(Long.parseLong(userId));
            dest.setCreateTime(new Date());
        }

        cmsArticleManager.save(dest);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        List<CmsArticle> cmsArticles = cmsArticleManager
                .findByIds(selectedItem);

        cmsArticleManager.removeAll(cmsArticles);
        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = cmsArticleManager.get(id);
        }

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = cmsArticleManager.pagedQuery(page, propertyFilters);

        List<CmsArticle> cmsArticles = (List<CmsArticle>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("cal info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(cmsArticles);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public CmsArticle getModel() {
        return model;
    }

    public void setCmsArticleManager(CmsArticleManager cmsArticleManager) {
        this.cmsArticleManager = cmsArticleManager;
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
