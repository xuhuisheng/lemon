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

import com.mossle.pim.persistence.domain.PimNote;
import com.mossle.pim.persistence.manager.PimNoteManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("pim")
public class PimNoteController {
    private PimNoteManager pimNoteManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private CurrentUserHolder currentUserHolder;
    private TenantHolder tenantHolder;

    @RequestMapping("pim-note-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page = pimNoteManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "pim/pim-note-list";
    }

    @RequestMapping("pim-note-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            PimNote pimNote = pimNoteManager.get(id);
            model.addAttribute("model", pimNote);
        }

        return "pim/pim-note-input";
    }

    @RequestMapping("pim-note-save")
    public String save(@ModelAttribute PimNote pimNote,
            RedirectAttributes redirectAttributes) {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();
        Long id = pimNote.getId();
        PimNote dest = null;

        if (id != null) {
            dest = pimNoteManager.get(id);
            beanMapper.copy(pimNote, dest);
        } else {
            dest = pimNote;
            dest.setUserId(userId);
            dest.setCreateTime(new Date());
            dest.setStatus("active");
        }

        pimNoteManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/pim/pim-note-list.do";
    }

    @RequestMapping("pim-note-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<PimNote> pimNotes = pimNoteManager.findByIds(selectedItem);
        pimNoteManager.removeAll(pimNotes);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/pim/pim-note-list.do";
    }

    @RequestMapping("pim-note-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page = pimNoteManager.pagedQuery(page, propertyFilters);

        List<PimNote> pimNotes = (List<PimNote>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("pim info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(pimNotes);
        exportor.export(request, response, tableModel);
    }

    @RequestMapping("pim-note-view")
    public String view(Model model) {
        String userId = currentUserHolder.getUserId();
        List<PimNote> pimNotes = pimNoteManager.find(
                "from PimNote where userId=? and status='active'", userId);
        model.addAttribute("pimNotes", pimNotes);

        return "pim/pim-note-view";
    }

    @RequestMapping("pim-note-create")
    @ResponseBody
    public String create() {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();

        PimNote pimNote = new PimNote();
        pimNote.setUserId(userId);
        pimNote.setCreateTime(new Date());
        pimNote.setStatus("active");

        pimNoteManager.save(pimNote);

        return Long.toString(pimNote.getId());
    }

    @RequestMapping("pim-note-update-position")
    @ResponseBody
    public String updatePosition(@RequestParam("id") Long id,
            @RequestParam("clientX") int clientX,
            @RequestParam("clientY") int clientY) {
        PimNote pimNote = pimNoteManager.get(id);
        pimNote.setClientX(clientX);
        pimNote.setClientY(clientY);
        pimNoteManager.save(pimNote);

        return "success";
    }

    @RequestMapping("pim-note-update-content")
    @ResponseBody
    public String updateContent(@RequestParam("id") Long id,
            @RequestParam("content") String content) {
        PimNote pimNote = pimNoteManager.get(id);
        pimNote.setContent(content);
        pimNoteManager.save(pimNote);

        return "success";
    }

    // ~ ======================================================================
    @Resource
    public void setPimNoteManager(PimNoteManager pimNoteManager) {
        this.pimNoteManager = pimNoteManager;
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
