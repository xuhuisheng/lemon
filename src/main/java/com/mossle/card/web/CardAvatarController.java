package com.mossle.card.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.card.persistence.domain.CardAvatar;
import com.mossle.card.persistence.manager.CardAvatarManager;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("card")
public class CardAvatarController {
    private CardAvatarManager cardAvatarManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("card-avatar-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = cardAvatarManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "card/card-avatar-list";
    }

    @RequestMapping("card-avatar-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            CardAvatar cardAvatar = cardAvatarManager.get(id);
            model.addAttribute("model", cardAvatar);
        }

        return "card/card-avatar-input";
    }

    @RequestMapping("card-avatar-save")
    public String save(@ModelAttribute CardAvatar cardAvatar,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        CardAvatar dest = null;

        Long id = cardAvatar.getId();

        if (id != null) {
            dest = cardAvatarManager.get(id);
            beanMapper.copy(cardAvatar, dest);
        } else {
            dest = cardAvatar;
            dest.setTenantId(tenantId);
        }

        cardAvatarManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/card/card-avatar-list.do";
    }

    @RequestMapping("card-avatar-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<CardAvatar> cardAvatars = cardAvatarManager
                .findByIds(selectedItem);

        cardAvatarManager.removeAll(cardAvatars);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/card/card-avatar-list.do";
    }

    @RequestMapping("card-avatar-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = cardAvatarManager.pagedQuery(page, propertyFilters);

        List<CardAvatar> cardAvatars = (List<CardAvatar>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("card info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(cardAvatars);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setCardAvatarManager(CardAvatarManager cardAvatarManager) {
        this.cardAvatarManager = cardAvatarManager;
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
}
