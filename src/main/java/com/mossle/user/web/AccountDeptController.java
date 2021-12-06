package com.mossle.user.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.user.persistence.domain.AccountDept;
import com.mossle.user.persistence.manager.AccountDeptManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("user")
public class AccountDeptController {
    private static Logger logger = LoggerFactory
            .getLogger(AccountDeptController.class);
    private AccountDeptManager accountDeptManager;
    private MessageHelper messageHelper;
    private BeanMapper beanMapper = new BeanMapper();
    private TenantHolder tenantHolder;
    private JsonMapper jsonMapper = new JsonMapper();

    @RequestMapping("account-dept-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = accountDeptManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "user/account-dept-list";
    }

    @RequestMapping("account-dept-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        AccountDept accountDept = null;

        if (id != null) {
            accountDept = accountDeptManager.get(id);
        } else {
            accountDept = new AccountDept();
        }

        model.addAttribute("model", accountDept);

        return "user/account-dept-input";
    }

    @RequestMapping("account-dept-save")
    public String save(@ModelAttribute AccountDept accountDept,
            RedirectAttributes redirectAttributes) throws Exception {
        String tenantId = tenantHolder.getTenantId();

        // 再进行数据复制
        AccountDept dest = null;
        Long id = accountDept.getId();

        if (id != null) {
            dest = accountDeptManager.get(id);
            beanMapper.copy(accountDept, dest);
        } else {
            dest = accountDept;

            // dest.setCreateTime(new Date());
            // dest.setTenantId(tenantId);
        }

        accountDeptManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/user/account-dept-list.do";
    }

    @RequestMapping("account-dept-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        List<AccountDept> accountDeptes = accountDeptManager
                .findByIds(selectedItem);

        for (AccountDept accountDept : accountDeptes) {
            accountDeptManager.remove(accountDept);
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/user/account-dept-list.do";
    }

    @RequestMapping("account-dept-tree")
    @ResponseBody
    public String tree() throws Exception {
        List<AccountDept> accountDepts = this.accountDeptManager
                .find("from AccountDept where accountDept=null order by priority");
        List<Map> list = this.generatePartyEntities(accountDepts);
        String json = jsonMapper.toJson(list);

        return json;
    }

    public List<Map> generatePartyEntities(List<AccountDept> accountDepts) {
        if (accountDepts == null) {
            return Collections.emptyList();
        }

        List<Map> list = new ArrayList<Map>();

        try {
            for (AccountDept accountDept : accountDepts) {
                list.add(generatePartyEntity(accountDept));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return list;
    }

    public Map<String, Object> generatePartyEntity(AccountDept accountDept) {
        Map<String, Object> map = new HashMap<String, Object>();

        try {
            map.put("id", accountDept.getId());
            map.put("code", accountDept.getCode());
            map.put("name", accountDept.getName());
            map.put("ref", accountDept.getRef());

            List<AccountDept> accountDepts = accountDeptManager.find(
                    "from AccountDept where accountDept=? order by priority",
                    accountDept);

            if (accountDepts.isEmpty()) {
                map.put("open", false);
            } else {
                map.put("open", true);
                map.put("children", generatePartyEntities(accountDepts));
            }

            return map;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return map;
        }
    }

    // ~ ======================================================================
    @Resource
    public void setAccountDeptManager(AccountDeptManager accountDeptManager) {
        this.accountDeptManager = accountDeptManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
