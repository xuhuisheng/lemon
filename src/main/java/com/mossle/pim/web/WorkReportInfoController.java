package com.mossle.pim.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.store.StoreConnector;
import com.mossle.api.store.StoreDTO;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.auth.CurrentUserHolder;
import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.store.MultipartFileDataSource;
import com.mossle.core.util.ServletUtils;

import com.mossle.pim.persistence.domain.WorkReportAttachment;
import com.mossle.pim.persistence.domain.WorkReportInfo;
import com.mossle.pim.persistence.manager.WorkReportAttachmentManager;
import com.mossle.pim.persistence.manager.WorkReportInfoManager;

import org.apache.commons.io.IOUtils;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("pim")
public class WorkReportInfoController {
    private WorkReportInfoManager workReportInfoManager;
    private WorkReportAttachmentManager workReportAttachmentManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private JsonMapper jsonMapper = new JsonMapper();
    private CurrentUserHolder currentUserHolder;
    private TenantHolder tenantHolder;
    private StoreConnector storeConnector;

    @RequestMapping("work-report-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page = workReportInfoManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "pim/work-report-info-list";
    }

    @RequestMapping("work-report-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            WorkReportInfo workReportInfo = workReportInfoManager.get(id);
            model.addAttribute("model", workReportInfo);
        }

        return "pim/work-report-info-input";
    }

    @RequestMapping("work-report-info-save")
    public String save(
            @ModelAttribute WorkReportInfo workReportInfo,
            @RequestParam(value = "attachmentIds", required = false) List<Long> attachmentIds,
            RedirectAttributes redirectAttributes) {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();
        Long id = workReportInfo.getId();
        WorkReportInfo dest = null;

        if (id != null) {
            dest = workReportInfoManager.get(id);
            beanMapper.copy(workReportInfo, dest);
        } else {
            dest = workReportInfo;
            dest.setUserId(userId);
            dest.setCreateTime(new Date());
        }

        workReportInfoManager.save(dest);
        dest = workReportInfoManager.get(dest.getId());

        List<Long> requestIds = null;

        if (attachmentIds == null) {
            requestIds = new ArrayList<Long>();
        } else {
            requestIds = attachmentIds;
        }

        List<Long> existIds = new ArrayList<Long>();

        for (WorkReportAttachment workReportAttachment : workReportAttachmentManager
                .findBy("workReportInfo", dest)) {
            existIds.add(workReportAttachment.getId());
        }

        List<Long> inserted = new ArrayList<Long>();
        List<Long> removed = new ArrayList<Long>();

        for (Long theId : requestIds) {
            if (!existIds.contains(theId)) {
                inserted.add(theId);
            }
        }

        for (Long theId : existIds) {
            if (!requestIds.contains(theId)) {
                removed.add(theId);
            }
        }

        for (Long theId : removed) {
            WorkReportAttachment workReportAttachment = workReportAttachmentManager
                    .get(theId);
            workReportAttachment.setWorkReportInfo(null);
            dest.getWorkReportAttachments().remove(workReportAttachment);
            workReportAttachmentManager.remove(workReportAttachment);
        }

        for (Long theId : inserted) {
            WorkReportAttachment workReportAttachment = workReportAttachmentManager
                    .get(theId);
            workReportAttachment.setWorkReportInfo(dest);
            workReportAttachmentManager.save(workReportAttachment);
        }

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/pim/work-report-info-list.do";
    }

    @RequestMapping("work-report-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<WorkReportInfo> workReportInfos = workReportInfoManager
                .findByIds(selectedItem);
        workReportInfoManager.removeAll(workReportInfos);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/pim/work-report-info-list.do";
    }

    @RequestMapping("work-report-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page = workReportInfoManager.pagedQuery(page, propertyFilters);

        List<WorkReportInfo> workReportInfos = (List<WorkReportInfo>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("pim info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(workReportInfos);
        exportor.export(request, response, tableModel);
    }

    @RequestMapping("work-report-info-download")
    @ResponseBody
    public String download(@RequestParam("id") Long id) throws Exception {
        List<WorkReportAttachment> workReportAttachments = workReportAttachmentManager
                .findBy("workReportInfo.id", id);

        Map<String, Object> data = new HashMap<String, Object>();
        List<Map<String, Object>> files = new ArrayList<Map<String, Object>>();
        data.put("files", files);

        for (WorkReportAttachment workReportAttachment : workReportAttachments) {
            Map<String, Object> map = new HashMap<String, Object>();
            files.add(map);
            map.put("name", workReportAttachment.getName());
            map.put("url", "work-report-info-image.do?id="
                    + workReportAttachment.getId());

            // map.put("thumbnailUrl", "./rs/cms/image?key=" + storeDto.getKey());
        }

        return jsonMapper.toJson(data);
    }

    @RequestMapping("work-report-info-upload")
    @ResponseBody
    public String upload(@RequestParam("id") Long id,
            @RequestParam("files[]") MultipartFile attachment) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        StoreDTO storeDto = storeConnector.saveStore("workReport/attachment",
                new MultipartFileDataSource(attachment), tenantId);
        WorkReportInfo workReportInfo = null;

        if (id != null) {
            workReportInfo = workReportInfoManager.get(id);
        }

        WorkReportAttachment workReportAttachment = new WorkReportAttachment();
        workReportAttachment.setWorkReportInfo(workReportInfo);
        workReportAttachment.setName(attachment.getOriginalFilename());
        workReportAttachment.setRef(storeDto.getKey());
        workReportAttachmentManager.save(workReportAttachment);

        Map<String, Object> data = new HashMap<String, Object>();
        List<Map<String, Object>> files = new ArrayList<Map<String, Object>>();
        data.put("files", files);

        Map<String, Object> map = new HashMap<String, Object>();
        files.add(map);
        map.put("id", workReportAttachment.getId());
        map.put("name", attachment.getOriginalFilename());
        // map.put("url", "../rs/cms/image?key=" + storeDto.getKey());
        map.put("url",
                "work-report-info-image.do?id=" + workReportAttachment.getId());

        // map.put("thumbnailUrl", "./rs/cms/image?key=" + storeDto.getKey());
        return jsonMapper.toJson(data);
    }

    @RequestMapping("work-report-info-attachment")
    public void attachment(@RequestParam("id") Long id,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        WorkReportAttachment workReportAttachment = workReportAttachmentManager
                .get(id);
        StoreDTO storeDto = storeConnector.getStore("workReport/attachment",
                workReportAttachment.getRef(), tenantId);
        ServletUtils.setFileDownloadHeader(request, response,
                workReportAttachment.getName());
        IOUtils.copy(storeDto.getDataSource().getInputStream(),
                response.getOutputStream());
    }

    // ~ ======================================================================
    @Resource
    public void setWorkReportInfoManager(
            WorkReportInfoManager workReportInfoManager) {
        this.workReportInfoManager = workReportInfoManager;
    }

    @Resource
    public void setWorkReportAttachmentManager(
            WorkReportAttachmentManager workReportAttachmentManager) {
        this.workReportAttachmentManager = workReportAttachmentManager;
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

    @Resource
    public void setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;
    }
}
