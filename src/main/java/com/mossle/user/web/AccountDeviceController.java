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

import com.mossle.user.persistence.domain.AccountDevice;
import com.mossle.user.persistence.manager.AccountDeviceManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("user")
public class AccountDeviceController {
    private AccountDeviceManager accountDeviceManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private TenantHolder tenantHolder;

    @RequestMapping("account-device-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = accountDeviceManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "user/account-device-list";
    }

    @RequestMapping("account-device-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        AccountDevice accountDevice = null;

        if (id != null) {
            accountDevice = accountDeviceManager.get(id);
        } else {
            accountDevice = new AccountDevice();
        }

        model.addAttribute("model", accountDevice);

        return "user/account-device-input";
    }

    @RequestMapping("account-device-save")
    public String save(
            @ModelAttribute AccountDevice accountDevice,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
            RedirectAttributes redirectAttributes) throws Exception {
        String tenantId = tenantHolder.getTenantId();

        // 再进行数据复制
        AccountDevice dest = null;
        Long id = accountDevice.getId();

        if (id != null) {
            dest = accountDeviceManager.get(id);
            dest.setStatus("new");
            beanMapper.copy(accountDevice, dest);
        } else {
            dest = accountDevice;
            dest.setCreateTime(new Date());
            dest.setTenantId(tenantId);
        }

        accountDeviceManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/user/account-device-list.do";
    }

    @RequestMapping("account-device-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        List<AccountDevice> accountDevices = accountDeviceManager
                .findByIds(selectedItem);

        for (AccountDevice accountDevice : accountDevices) {
            accountDeviceManager.remove(accountDevice);
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/user/account-device-list.do";
    }

    @RequestMapping("account-device-active")
    public String active(@RequestParam("id") Long id,
            RedirectAttributes redirectAttributes) {
        AccountDevice accountDevice = accountDeviceManager.get(id);
        accountDevice.setStatus("active");
        accountDeviceManager.save(accountDevice);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.update", "操作成功");

        return "redirect:/user/account-device-list.do";
    }

    @RequestMapping("account-device-disable")
    public String disable(@RequestParam("id") Long id,
            RedirectAttributes redirectAttributes) {
        AccountDevice accountDevice = accountDeviceManager.get(id);
        accountDevice.setStatus("disabled");
        accountDeviceManager.save(accountDevice);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.update", "操作成功");

        return "redirect:/user/account-device-list.do";
    }

    // ~ ======================================================================
    @Resource
    public void setAccountDeviceManager(
            AccountDeviceManager accountDeviceManager) {
        this.accountDeviceManager = accountDeviceManager;
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
