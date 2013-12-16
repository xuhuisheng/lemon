package com.mossle.doc.web.doc;

import java.io.*;
import java.io.File;

import java.util.ArrayList;
import java.util.List;

import com.mossle.api.UserConnector;
import com.mossle.api.scope.ScopeHolder;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.struts2.BaseAction;
import com.mossle.core.util.IoUtils;

import com.mossle.doc.domain.DocInfo;
import com.mossle.doc.manager.DocInfoManager;

import com.mossle.security.util.SpringSecurityUtils;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = DocInfoAction.RELOAD, location = "doc-info.do?operationMode=RETRIEVE", type = "redirect") })
public class DocInfoAction extends BaseAction implements ModelDriven<DocInfo>,
        Preparable {
    public static final String RELOAD = "reload";
    private DocInfoManager docInfoManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private DocInfo model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private File attachment;

    public void setAttachment(File attachment) {
        this.attachment = attachment;
    }

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
        page = docInfoManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new DocInfo();
    }

    public String save() throws Exception {
        DocInfo dest = null;

        if (id > 0) {
            dest = docInfoManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;

            String userId = userConnector.findByUsername(
                    SpringSecurityUtils.getCurrentUsername(),
                    ScopeHolder.getUserRepoRef()).getId();
            dest.setUserId(Long.parseLong(userId));
        }

        new File("target/uploaded").mkdirs();

        File targetFile = new File("target/uploaded", attachment.getName());
        InputStream is = null;
        OutputStream os = null;

        try {
            is = new FileInputStream(attachment);
            os = new FileOutputStream(targetFile);
            IoUtils.copyStream(is, os);
        } finally {
            if (is != null) {
                is.close();
            }

            if (os != null) {
                os.close();
            }
        }

        dest.setPath(targetFile.getName());
        docInfoManager.save(dest);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public void download() throws Exception {
        DocInfo docInfo = docInfoManager.get(id);
        File file = new File("target/uploaded", docInfo.getPath());
        InputStream is = null;

        try {
            is = new FileInputStream(file);
            IoUtils.copyStream(is, ServletActionContext.getResponse()
                    .getOutputStream());
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String removeAll() {
        List<DocInfo> docInfos = docInfoManager.findByIds(selectedItem);

        docInfoManager.removeAll(docInfos);
        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = docInfoManager.get(id);
        }

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = docInfoManager.pagedQuery(page, propertyFilters);

        List<DocInfo> docInfos = (List<DocInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("doc info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(docInfos);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public DocInfo getModel() {
        return model;
    }

    public void setDocInfoManager(DocInfoManager docInfoManager) {
        this.docInfoManager = docInfoManager;
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
