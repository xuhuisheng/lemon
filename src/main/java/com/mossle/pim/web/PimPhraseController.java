package com.mossle.pim.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.auth.CurrentUserHolder;
import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.pim.persistence.domain.PimPhrase;
import com.mossle.pim.persistence.manager.PimPhraseManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("pim")
public class PimPhraseController {
    private PimPhraseManager pimPhraseManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private CurrentUserHolder currentUserHolder;
    private TenantHolder tenantHolder;

    @RequestMapping("pim-phrase-my-list")
    public String myList(Model model) {
        String userId = currentUserHolder.getUserId();
        List<PimPhrase> pimPhrases = pimPhraseManager.findBy("userId", userId);
        model.addAttribute("pimPhrases", pimPhrases);

        return "pim/pim-phrase-my-list";
    }

    @RequestMapping("pim-phrase-my-input")
    public String myInput(
            @RequestParam(value = "id", required = false) Long id, Model model) {
        if (id != null) {
            PimPhrase pimPhrase = pimPhraseManager.get(id);
            model.addAttribute("pimPhrase", pimPhrase);
        }

        return "pim/pim-phrase-my-input";
    }

    @RequestMapping("pim-phrase-my-save")
    public String mySave(@ModelAttribute PimPhrase pimPhrase,
            RedirectAttributes redirectAttributes) {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();
        Long id = pimPhrase.getId();
        PimPhrase dest = null;

        if (id != null) {
            dest = pimPhraseManager.get(id);
            beanMapper.copy(pimPhrase, dest);
        } else {
            dest = pimPhrase;
            dest.setUserId(userId);
        }

        pimPhraseManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/pim/pim-phrase-my-list.do";
    }

    @RequestMapping("pim-phrase-my-remove")
    public String myRemove(
            @RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<PimPhrase> pimPhrases = pimPhraseManager.findByIds(selectedItem);
        pimPhraseManager.removeAll(pimPhrases);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/pim/pim-phrase-my-list.do";
    }

    @RequestMapping("pim-phrase-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page = pimPhraseManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "pim/pim-phrase-list";
    }

    @RequestMapping("pim-phrase-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            PimPhrase pimPhrase = pimPhraseManager.get(id);
            model.addAttribute("model", pimPhrase);
        }

        return "pim/pim-phrase-input";
    }

    @RequestMapping("pim-phrase-save")
    public String save(@ModelAttribute PimPhrase pimPhrase,
            RedirectAttributes redirectAttributes) {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();
        Long id = pimPhrase.getId();
        PimPhrase dest = null;

        if (id != null) {
            dest = pimPhraseManager.get(id);
            beanMapper.copy(pimPhrase, dest);
        } else {
            dest = pimPhrase;
            dest.setUserId(userId);
        }

        pimPhraseManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/pim/pim-phrase-list.do";
    }

    @RequestMapping("pim-phrase-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<PimPhrase> pimPhrases = pimPhraseManager.findByIds(selectedItem);
        pimPhraseManager.removeAll(pimPhrases);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/pim/pim-phrase-list.do";
    }

    @RequestMapping("pim-phrase-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page = pimPhraseManager.pagedQuery(page, propertyFilters);

        List<PimPhrase> pimPhrases = (List<PimPhrase>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("pim info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(pimPhrases);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setPimPhraseManager(PimPhraseManager pimPhraseManager) {
        this.pimPhraseManager = pimPhraseManager;
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
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
