package com.mossle.doc.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.doc.persistence.domain.DocDispatch;
import com.mossle.doc.persistence.domain.DocIncoming;
import com.mossle.doc.persistence.manager.DocDispatchManager;
import com.mossle.doc.persistence.manager.DocIncomingManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("doc")
public class DocController {
    private DocIncomingManager docIncomingManager;
    private DocDispatchManager docDispatchManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;
    private CurrentUserHolder currentUserHolder;

    @RequestMapping("index")
    public String index(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        return "redirect:/doc/incoming.do";
    }

    @RequestMapping("incoming")
    public String incoming(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();

        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page.setDefaultOrder("id", Page.DESC);

        page = docIncomingManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "doc/incoming";
    }

    @RequestMapping("dispatch")
    public String dispatch(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();

        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page.setDefaultOrder("id", Page.DESC);

        page = docDispatchManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "doc/dispatch";
    }

    // ~ ======================================================================
    @Resource
    public void setDocIncomingManager(DocIncomingManager docIncomingManager) {
        this.docIncomingManager = docIncomingManager;
    }

    @Resource
    public void setDocDispatchManager(DocDispatchManager docDispatchManager) {
        this.docDispatchManager = docDispatchManager;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }
}
