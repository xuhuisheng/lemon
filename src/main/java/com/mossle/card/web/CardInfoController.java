package com.mossle.card.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.card.persistence.domain.CardInfo;
import com.mossle.card.persistence.domain.DoorInfo;
import com.mossle.card.persistence.manager.CardInfoManager;
import com.mossle.card.persistence.manager.DoorInfoManager;

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
public class CardInfoController {
    private CardInfoManager cardInfoManager;
    private DoorInfoManager doorInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("card-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = cardInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "card/card-info-list";
    }

    @RequestMapping("card-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            CardInfo cardInfo = cardInfoManager.get(id);
            model.addAttribute("model", cardInfo);
        }

        return "card/card-info-input";
    }

    @RequestMapping("card-info-save")
    public String save(@ModelAttribute CardInfo cardInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        CardInfo dest = null;

        Long id = cardInfo.getId();

        if (id != null) {
            dest = cardInfoManager.get(id);
            beanMapper.copy(cardInfo, dest);
        } else {
            dest = cardInfo;
            dest.setTenantId(tenantId);
        }

        cardInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/card/card-info-list.do";
    }

    @RequestMapping("card-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<CardInfo> cardInfos = cardInfoManager.findByIds(selectedItem);

        cardInfoManager.removeAll(cardInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/card/card-info-list.do";
    }

    @RequestMapping("card-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = cardInfoManager.pagedQuery(page, propertyFilters);

        List<CardInfo> cardInfos = (List<CardInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("card info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(cardInfos);
        exportor.export(request, response, tableModel);
    }

    @RequestMapping("card-info-door-input")
    public String cardInfoDoorInput(@RequestParam("id") Long id, Model model) {
        CardInfo cardInfo = cardInfoManager.get(id);
        model.addAttribute("cardInfo", cardInfo);

        List<DoorInfo> doorInfos = doorInfoManager.getAll();
        model.addAttribute("doorInfos", doorInfos);

        List<Long> ids = new ArrayList<Long>();

        for (DoorInfo doorInfo : cardInfo.getDoorInfos()) {
            ids.add(doorInfo.getId());
        }

        model.addAttribute("ids", ids);

        return "card/card-info-door-input";
    }

    @RequestMapping("card-info-door-save")
    public String cardInfoDoorSave(
            @RequestParam("cardId") Long cardId,
            @RequestParam(value = "doorIds", required = false) List<Long> doorIds) {
        CardInfo cardInfo = cardInfoManager.get(cardId);
        cardInfo.getDoorInfos().clear();
        cardInfoManager.save(cardInfo);

        if (doorIds != null) {
            for (Long doorId : doorIds) {
                DoorInfo doorInfo = doorInfoManager.get(doorId);
                cardInfo.getDoorInfos().add(doorInfo);
                cardInfoManager.save(cardInfo);
            }
        }

        return "redirect:/card/card-info-door-input.do?id=" + cardId;
    }

    // ~ ======================================================================
    @Resource
    public void setCardInfoManager(CardInfoManager cardInfoManager) {
        this.cardInfoManager = cardInfoManager;
    }

    @Resource
    public void setDoorInfoManager(DoorInfoManager doorInfoManager) {
        this.doorInfoManager = doorInfoManager;
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
