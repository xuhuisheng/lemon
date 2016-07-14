package com.mossle.internal.sendmail.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.store.StoreConnector;
import com.mossle.api.store.StoreDTO;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.store.MultipartFileDataSource;
import com.mossle.core.util.IoUtils;
import com.mossle.core.util.ServletUtils;

import com.mossle.internal.sendmail.persistence.domain.SendmailAttachment;
import com.mossle.internal.sendmail.persistence.manager.SendmailAttachmentManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("sendmail")
public class SendmailAttachmentController {
    private SendmailAttachmentManager sendmailAttachmentManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private StoreConnector storeConnector;
    private BeanMapper beanMapper = new BeanMapper();
    private TenantHolder tenantHolder;

    @RequestMapping("sendmail-attachment-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = sendmailAttachmentManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "sendmail/sendmail-attachment-list";
    }

    @RequestMapping("sendmail-attachment-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            SendmailAttachment sendmailAttachment = sendmailAttachmentManager
                    .get(id);
            model.addAttribute("model", sendmailAttachment);
        }

        return "sendmail/sendmail-attachment-input";
    }

    @RequestMapping("sendmail-attachment-save")
    public String save(@ModelAttribute SendmailAttachment sendmailAttachment,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        Long id = sendmailAttachment.getId();
        SendmailAttachment dest = null;

        if (id != null) {
            dest = sendmailAttachmentManager.get(id);
            beanMapper.copy(sendmailAttachment, dest);
        } else {
            dest = sendmailAttachment;
            dest.setTenantId(tenantId);
        }

        sendmailAttachmentManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/sendmail/sendmail-attachment-list.do";
    }

    @RequestMapping("sendmail-attachment-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<SendmailAttachment> sendmailAttachments = sendmailAttachmentManager
                .findByIds(selectedItem);
        sendmailAttachmentManager.removeAll(sendmailAttachments);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/sendmail/sendmail-attachment-list.do";
    }

    @RequestMapping("sendmail-attachment-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = sendmailAttachmentManager.pagedQuery(page, propertyFilters);

        List<SendmailAttachment> sendmailAttachments = (List<SendmailAttachment>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("sendmail config");
        tableModel.addHeaders("id", "name");
        tableModel.setData(sendmailAttachments);
        exportor.export(request, response, tableModel);
    }

    @RequestMapping("sendmail-attachment-upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile multipartFile)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        StoreDTO storeDto = storeConnector.saveStore("mailattachment",
                new MultipartFileDataSource(multipartFile), tenantId);
        SendmailAttachment sendmailAttachment = new SendmailAttachment();
        sendmailAttachment.setName(multipartFile.getOriginalFilename());
        sendmailAttachment.setPath(storeDto.getKey());
        sendmailAttachment.setTenantId(tenantId);

        sendmailAttachmentManager.save(sendmailAttachment);

        return "{\"id\":\"" + sendmailAttachment.getId() + "\",\"name\":\""
                + sendmailAttachment.getName() + "\"}";
    }

    @RequestMapping("sendmail-attachment-removeById")
    @ResponseBody
    public String removeById(@RequestParam("id") Long id) {
        sendmailAttachmentManager.removeById(id);

        return "{\"success\":true}";
    }

    @RequestMapping("sendmail-attachment-download")
    public void download(@RequestParam("id") Long id,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        SendmailAttachment sendmailAttachment = sendmailAttachmentManager
                .get(id);
        StoreDTO storeDto = storeConnector.getStore("sendmailattachment",
                sendmailAttachment.getPath(), tenantId);

        ServletUtils.setFileDownloadHeader(request, response,
                sendmailAttachment.getName());
        IoUtils.copyStream(storeDto.getDataSource().getInputStream(),
                response.getOutputStream());
    }

    // ~ ======================================================================
    @Resource
    public void setSendmailAttachmentManager(
            SendmailAttachmentManager sendmailAttachmentManager) {
        this.sendmailAttachmentManager = sendmailAttachmentManager;
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
    public void setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
