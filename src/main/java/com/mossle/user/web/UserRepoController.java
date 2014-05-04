package com.mossle.user.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.api.scope.ScopeHolder;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;

import com.mossle.user.component.UserRepoPublisher;
import com.mossle.user.persistence.domain.UserRepo;
import com.mossle.user.persistence.manager.UserRepoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("user")
public class UserRepoController {
    private UserRepoManager userRepoManager;
    private UserRepoPublisher userRepoPublisher;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("user-repo-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_scopeId", ScopeHolder
                .getScopeId()));
        page = userRepoManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "user/user-repo-list";
    }

    @RequestMapping("user-repo-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            UserRepo userRepo = userRepoManager.get(id);
            model.addAttribute("model", userRepo);
        }

        return "user/user-repo-input";
    }

    @RequestMapping("user-repo-save")
    public String save(@ModelAttribute UserRepo userRepo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        UserRepo dest = null;
        Long id = userRepo.getId();

        if (id != null) {
            dest = userRepoManager.get(id);
            beanMapper.copy(userRepo, dest);
        } else {
            dest = userRepo;
        }

        if (id == null) {
            dest.setScopeId(ScopeHolder.getScopeId());
        }

        userRepoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");
        userRepoPublisher.execute(dest);

        return "redirect:/user/user-repo-list.do";
    }

    @RequestMapping("user-repo-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<UserRepo> userRepos = userRepoManager.findByIds(selectedItem);

        boolean success = true;

        for (UserRepo userRepo : userRepos) {
            if (userRepo.getUserSchemas().isEmpty()) {
                userRepoManager.remove(userRepo);
                userRepo.setName(null);
                userRepoPublisher.execute(userRepo);
            } else {
                success = false;
                messageHelper.addFlashMessage(redirectAttributes, "无法删除"
                        + userRepo.getName());
            }
        }

        if (success) {
            messageHelper.addFlashMessage(redirectAttributes,
                    "core.success.delete", "删除成功");
        }

        return "redirect:/user/user-repo-list.do";
    }

    @RequestMapping("user-repo-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = userRepoManager.pagedQuery(page, propertyFilters);

        List<UserRepo> userRepos = (List<UserRepo>) page.getResult();
        TableModel tableModel = new TableModel();
        tableModel.setName("user-repo");
        tableModel.addHeaders("id", "name", "type", "repoCode", "reference",
                "descn");
        tableModel.setData(userRepos);
        exportor.export(response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setUserRepoManager(UserRepoManager userRepoManager) {
        this.userRepoManager = userRepoManager;
    }

    @Resource
    public void setUserRepoPublisher(UserRepoPublisher userRepoPublisher) {
        this.userRepoPublisher = userRepoPublisher;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }
}
