package com.mossle.user.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.export.Exportor;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.user.persistence.domain.AccountAlias;
import com.mossle.user.persistence.manager.AccountAliasManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("user")
public class AccountAliasController {
    private AccountAliasManager accountAliasManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private TenantHolder tenantHolder;

    @RequestMapping("account-alis-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = accountAliasManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "user/account-alias-list";
    }

    @RequestMapping("account-alias-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        AccountAlias accountAlias = null;

        if (id != null) {
            accountAlias = accountAliasManager.get(id);
        } else {
            accountAlias = new AccountAlias();
        }

        model.addAttribute("model", accountAlias);

        return "user/account-alias-input";
    }

    @RequestMapping("account-alias-save")
    public String save(@ModelAttribute AccountAlias accountAlias,
            RedirectAttributes redirectAttributes) throws Exception {
        String tenantId = tenantHolder.getTenantId();

        // 再进行数据复制
        AccountAlias dest = null;
        Long id = accountAlias.getId();

        if (id != null) {
            dest = accountAliasManager.get(id);
            beanMapper.copy(accountAlias, dest);
        } else {
            dest = accountAlias;
            dest.setCreateTime(new Date());
            dest.setTenantId(tenantId);
        }

        accountAliasManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/user/account-alias-list.do";
    }

    @RequestMapping("account-alias-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        List<AccountAlias> accountAliases = accountAliasManager
                .findByIds(selectedItem);

        for (AccountAlias accountAlias : accountAliases) {
            accountAliasManager.remove(accountAlias);
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/user/account-alias-list.do";
    }

    // ~ ======================================================================
    @Resource
    public void setAccountAliasManager(AccountAliasManager accountAliasManager) {
        this.accountAliasManager = accountAliasManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
