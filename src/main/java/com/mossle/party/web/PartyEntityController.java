package com.mossle.party.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;

import com.mossle.party.domain.PartyEntity;
import com.mossle.party.domain.PartyType;
import com.mossle.party.manager.PartyEntityManager;
import com.mossle.party.manager.PartyTypeManager;
import com.mossle.party.support.PartyEntityConverter;
import com.mossle.party.support.PartyEntityDTO;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("party")
public class PartyEntityController {
    private PartyEntityManager partyEntityManager;
    private PartyTypeManager partyTypeManager;
    private MessageHelper messageHelper;
    private PartyEntityConverter partyEntityConverter = new PartyEntityConverter();
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("party-entity-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = partyEntityManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "party/party-entity-list";
    }

    @RequestMapping("party-entity-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            PartyEntity partyEntity = partyEntityManager.get(id);
            model.addAttribute("model", partyEntity);
        }

        List<PartyType> partyTypes = partyTypeManager.getAll();
        model.addAttribute("partyTypes", partyTypes);

        return "party/party-entity-input";
    }

    @RequestMapping("party-entity-save")
    public String save(@ModelAttribute PartyEntity partyEntity,
            @RequestParam("partyTypeId") Long partyTypeId,
            RedirectAttributes redirectAttributes) {
        PartyEntity dest = null;
        Long id = partyEntity.getId();

        if (id != null) {
            dest = partyEntityManager.get(id);
            beanMapper.copy(partyEntity, dest);
        } else {
            dest = partyEntity;
        }

        dest.setPartyType(partyTypeManager.get(partyTypeId));
        partyEntityManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/party/party-entity-list.do";
    }

    @RequestMapping("party-entity-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        partyEntityManager
                .removeAll(partyEntityManager.findByIds(selectedItem));
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/party/party-entity-list.do";
    }

    @RequestMapping("party-entity-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = partyEntityManager.pagedQuery(page, propertyFilters);

        List<PartyEntity> partyEntities = (List<PartyEntity>) page.getResult();
        List<PartyEntityDTO> partyDtos = partyEntityConverter
                .createPartyEntityDtos(partyEntities);
        TableModel tableModel = new TableModel();
        tableModel.setName("party entity");
        tableModel.addHeaders("id", "type", "code", "name");
        tableModel.setData(partyDtos);
        exportor.export(response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
    }

    @Resource
    public void setPartyTypeManager(PartyTypeManager partyTypeManager) {
        this.partyTypeManager = partyTypeManager;
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
