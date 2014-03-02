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

import com.mossle.user.persistence.domain.UserRepo;
import com.mossle.user.persistence.domain.UserSchema;
import com.mossle.user.persistence.manager.UserRepoManager;
import com.mossle.user.persistence.manager.UserSchemaManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("user")
public class UserSchemaController {
    private UserSchemaManager userSchemaManager;
    private UserRepoManager userRepoManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("user-schema-list")
    public String list(@ModelAttribute Page page,
            @RequestParam("userRepoId") Long userRepoId,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);

        propertyFilters.add(new PropertyFilter("EQL_userRepo.id", Long
                .toString(userRepoId)));
        page = userSchemaManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "user/user-schema-list";
    }

    @RequestMapping("user-schema-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            UserSchema userSchema = userSchemaManager.get(id);
            model.addAttribute("model", userSchema);
        }

        return "user/user-schema-input";
    }

    @RequestMapping("user-schema-save")
    public String save(@ModelAttribute UserSchema userSchema,
            @RequestParam("userRepoId") Long userRepoId,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        UserSchema dest = null;
        Long id = userSchema.getId();

        if (id != null) {
            dest = userSchemaManager.get(id);
            beanMapper.copy(userSchema, dest);
        } else {
            dest = userSchema;
        }

        if (id == null) {
            dest.setScopeId(ScopeHolder.getScopeId());
        }

        UserRepo userRepo = userRepoManager.get(userRepoId);
        dest.setUserRepo(userRepo);
        userSchemaManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/user/user-schema-list.do?userRepoId=" + userRepoId;
    }

    @RequestMapping("user-schema-remove")
    public String remove(@RequestParam("userRepoId") Long userRepoId,
            @RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<UserSchema> userSchemas = userSchemaManager
                .findByIds(selectedItem);
        boolean success = true;

        for (UserSchema userSchema : userSchemas) {
            if (userSchema.getUserAttrs().isEmpty()) {
                userSchemaManager.remove(userSchema);
            } else {
                success = false;
                messageHelper.addFlashMessage(redirectAttributes, "无法删除"
                        + userSchema.getName());
            }
        }

        if (success) {
            messageHelper.addFlashMessage(redirectAttributes,
                    "core.success.delete", "删除成功");
        }

        return "redirect:/user/user-schema-list.do?userRepoId=" + userRepoId;
    }

    @RequestMapping("user-schema-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = userSchemaManager.pagedQuery(page, propertyFilters);

        List<UserSchema> userRepos = (List<UserSchema>) page.getResult();
        TableModel tableModel = new TableModel();
        tableModel.setName("user-repo");
        tableModel.addHeaders("id", "name", "type", "repoCode", "reference",
                "descn");
        tableModel.setData(userRepos);
        exportor.export(response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setUserSchemaManager(UserSchemaManager userSchemaManager) {
        this.userSchemaManager = userSchemaManager;
    }

    @Resource
    public void setUserRepoManager(UserRepoManager userRepoManager) {
        this.userRepoManager = userRepoManager;
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
