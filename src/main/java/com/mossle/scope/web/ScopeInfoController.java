package com.mossle.scope.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.api.scope.ScopeCache;
import com.mossle.api.userrepo.UserRepoConnector;
import com.mossle.api.userrepo.UserRepoDTO;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;

import com.mossle.scope.component.ScopePublisher;
import com.mossle.scope.domain.ScopeInfo;
import com.mossle.scope.manager.ScopeInfoManager;
import com.mossle.scope.support.ScopeInfoDTO;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/scope")
public class ScopeInfoController {
    private ScopeInfoManager scopeInfoManager;
    private ScopePublisher scopePublisher;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserRepoConnector userRepoConnector;
    private ScopeCache scopeCache;
    private MessageHelper messageHelper;

    @RequestMapping("scope-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
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
        model.addAttribute("page", page);

        return "scope/scope-info-list";
    }

    @RequestMapping("scope-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            ScopeInfo scopeInfo = scopeInfoManager.get(id);
            model.addAttribute("model", scopeInfo);
        }

        List<UserRepoDTO> userRepoDtos = userRepoConnector.findAll();
        model.addAttribute("userRepoDtos", userRepoDtos);

        return "scope/scope-info-input";
    }

    @RequestMapping("scope-info-save")
    public String save(@ModelAttribute ScopeInfo scopeInfo,
            RedirectAttributes redirectAttributes) {
        Long id = scopeInfo.getId();
        ScopeInfo dest = null;

        if (id != null) {
            dest = scopeInfoManager.get(id);
            beanMapper.copy(scopeInfo, dest);
        } else {
            dest = scopeInfo;
        }

        scopeInfoManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");
        // TODO: 应该支持事务提交后发送消息
        scopePublisher.execute(dest);

        return "redirect:/scope/scope-info-list.do";
    }

    @RequestMapping("scope-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<ScopeInfo> scopeInfos = scopeInfoManager.findByIds(selectedItem);
        scopeInfoManager.removeAll(scopeInfos);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        // TODO: 应该支持事务提交后发送消息
        for (ScopeInfo scopeInfo : scopeInfos) {
            scopeInfo.setName(null);
            // 支持真删除？
            scopePublisher.execute(scopeInfo);
        }

        return "redirect:/scope/scope-info-list.do";
    }

    @RequestMapping("scope-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = scopeInfoManager.pagedQuery(page, propertyFilters);

        List<ScopeInfo> scopeInfos = (List<ScopeInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("scopeInfo");
        tableModel.addHeaders("id", "name");
        tableModel.setData(scopeInfos);
        exportor.export(response, tableModel);
    }

    @RequestMapping("scope-info-checkName")
    @ResponseBody
    public boolean checkName(@RequestParam("name") String name,
            @RequestParam(value = "id", required = false) Long id)
            throws Exception {
        String hql = "from ScopeInfo where name=?";
        Object[] params = { name };

        if (id != null) {
            hql = "from ScopeInfo where name=? and id<>?";
            params = new Object[] { name, id };
        }

        ScopeInfo scopeInfo = scopeInfoManager.findUnique(hql, params);

        boolean result = (scopeInfo == null);

        return result;
    }

    // ~ ======================================================================
    @Resource
    public void setScopeInfoManager(ScopeInfoManager scopeInfoManager) {
        this.scopeInfoManager = scopeInfoManager;
    }

    @Resource
    public void setUserRepoConnector(UserRepoConnector userRepoConnector) {
        this.userRepoConnector = userRepoConnector;
    }

    @Resource
    public void setScopePublisher(ScopePublisher scopePublisher) {
        this.scopePublisher = scopePublisher;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setScopeCache(ScopeCache scopeCache) {
        this.scopeCache = scopeCache;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }
}
