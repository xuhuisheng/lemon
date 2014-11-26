package com.mossle.internal.mail.web;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.util.IoUtils;
import com.mossle.core.util.ServletUtils;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;
import com.mossle.ext.mail.MailDTO;
import com.mossle.ext.mail.MailHelper;
import com.mossle.ext.mail.MailServerInfo;
import com.mossle.ext.store.MultipartFileResource;
import com.mossle.ext.store.StoreConnector;
import com.mossle.ext.store.StoreDTO;

import com.mossle.internal.mail.domain.MailAttachment;
import com.mossle.internal.mail.manager.MailAttachmentManager;

import org.springframework.core.io.InputStreamResource;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mail")
public class MailAttachmentController {
    private MailAttachmentManager mailAttachmentManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private StoreConnector storeConnector;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("mail-attachment-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = mailAttachmentManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "mail/mail-attachment-list";
    }

    @RequestMapping("mail-attachment-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            MailAttachment mailAttachment = mailAttachmentManager.get(id);
            model.addAttribute("model", mailAttachment);
        }

        return "mail/mail-attachment-input";
    }

    @RequestMapping("mail-attachment-save")
    public String save(@ModelAttribute MailAttachment mailAttachment,
            RedirectAttributes redirectAttributes) {
        Long id = mailAttachment.getId();
        MailAttachment dest = null;

        if (id != null) {
            dest = mailAttachmentManager.get(id);
            beanMapper.copy(mailAttachment, dest);
        } else {
            dest = mailAttachment;
        }

        mailAttachmentManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/mail/mail-attachment-list.do";
    }

    @RequestMapping("mail-attachment-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<MailAttachment> mailAttachments = mailAttachmentManager
                .findByIds(selectedItem);
        mailAttachmentManager.removeAll(mailAttachments);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/mail/mail-attachment-list.do";
    }

    @RequestMapping("mail-attachment-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = mailAttachmentManager.pagedQuery(page, propertyFilters);

        List<MailAttachment> mailAttachments = (List<MailAttachment>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("mail config");
        tableModel.addHeaders("id", "name");
        tableModel.setData(mailAttachments);
        exportor.export(response, tableModel);
    }

    @RequestMapping("mail-attachment-upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile multipartFile)
            throws Exception {
        StoreDTO storeDto = storeConnector.save("mailattachment",
                new MultipartFileResource(multipartFile),
                multipartFile.getOriginalFilename());
        MailAttachment mailAttachment = new MailAttachment();
        mailAttachment.setName(multipartFile.getOriginalFilename());
        mailAttachment.setPath(storeDto.getKey());

        mailAttachmentManager.save(mailAttachment);

        return "{\"id\":\"" + mailAttachment.getId() + "\",\"name\":\""
                + mailAttachment.getName() + "\"}";
    }

    @RequestMapping("mail-attachment-removeById")
    @ResponseBody
    public String removeById(@RequestParam("id") Long id) {
        mailAttachmentManager.removeById(id);

        return "{\"success\":true}";
    }

    @RequestMapping("mail-attachment-download")
    public void download(@RequestParam("id") Long id,
            HttpServletResponse response) throws Exception {
        MailAttachment mailAttachment = mailAttachmentManager.get(id);
        StoreDTO storeDto = storeConnector.get("mailattachment",
                mailAttachment.getPath());

        ServletUtils.setFileDownloadHeader(response, mailAttachment.getName());
        IoUtils.copyStream(storeDto.getResource().getInputStream(),
                response.getOutputStream());
    }

    // ~ ======================================================================
    @Resource
    public void setMailAttachmentManager(
            MailAttachmentManager mailAttachmentManager) {
        this.mailAttachmentManager = mailAttachmentManager;
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
}
