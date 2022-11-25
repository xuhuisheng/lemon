package com.mossle.ticket.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.store.StoreDTO;

import com.mossle.client.store.StoreClient;

import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.store.MultipartFileDataSource;

import com.mossle.ticket.persistence.domain.TicketAttachment;
import com.mossle.ticket.persistence.domain.TicketComment;
import com.mossle.ticket.persistence.domain.TicketInfo;
import com.mossle.ticket.persistence.manager.TicketAttachmentManager;
import com.mossle.ticket.persistence.manager.TicketCommentManager;
import com.mossle.ticket.persistence.manager.TicketInfoManager;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("ticket/my")
public class TicketMyController {
    private static Logger logger = LoggerFactory
            .getLogger(TicketMyController.class);
    private TicketInfoManager ticketInfoManager;
    private TicketCommentManager ticketCommentManager;
    private TicketAttachmentManager ticketAttachmentManager;
    private BeanMapper beanMapper = new BeanMapper();
    private JsonMapper jsonMapper = new JsonMapper();
    private CurrentUserHolder currentUserHolder;
    private StoreClient storeClient;

    @RequestMapping("create")
    public String create(Model model) {
        return "ticket/my/create";
    }

    @RequestMapping("list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        page.setDefaultOrder("id", Page.DESC);

        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = ticketInfoManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "ticket/my/list";
    }

    @RequestMapping("save")
    public String save(@ModelAttribute TicketInfo ticketInfo,
            @RequestParam(value = "files", required = false) List<String> files)
            throws Exception {
        String userId = currentUserHolder.getUserId();
        Date now = new Date();
        ticketInfo.setCreator(userId);
        ticketInfo.setCreateTime(now);
        ticketInfo.setStatus("new");
        ticketInfo.setUpdateTime(now);
        ticketInfoManager.save(ticketInfo);
        ticketInfo.setCode(Long.toString(ticketInfo.getId()));
        ticketInfoManager.save(ticketInfo);

        List<TicketAttachment> ticketAttachments = ticketAttachmentManager
                .findBy("ticketInfo", ticketInfo);

        List<String> inserts = new ArrayList<String>();
        List<TicketAttachment> removes = new ArrayList<TicketAttachment>();

        if (files != null) {
            for (String fileKey : files) {
                boolean exists = false;

                for (TicketAttachment ticketAttachment : ticketAttachments) {
                    if (fileKey.equals(ticketAttachment.getCode())) {
                        exists = true;

                        break;
                    }
                }

                if (!exists) {
                    inserts.add(fileKey);
                }
            }
        }

        for (TicketAttachment ticketAttachment : ticketAttachments) {
            if (!files.contains(ticketAttachment.getCode())) {
                removes.add(ticketAttachment);
            }
        }

        for (String fileKey : inserts) {
            StoreDTO storeDto = this.storeClient.getStore("ticket", fileKey,
                    "1");
            TicketAttachment ticketAttachment = new TicketAttachment();
            ticketAttachment.setTicketInfo(ticketInfo);
            ticketAttachment.setCode(fileKey);

            if (storeDto != null) {
                ticketAttachment.setName(storeDto.getDisplayName());
            }

            ticketAttachmentManager.save(ticketAttachment);
        }

        ticketAttachmentManager.removeAll(removes);

        return "redirect:/ticket/my/list.do";
    }

    @RequestMapping("edit")
    public String edit(@RequestParam("id") Long id, Model model) {
        TicketInfo ticketInfo = ticketInfoManager.get(id);
        model.addAttribute("ticketInfo", ticketInfo);

        return "ticket/my/edit";
    }

    @RequestMapping("update")
    public String update(@ModelAttribute TicketInfo ticketInfo) {
        String userId = currentUserHolder.getUserId();
        TicketInfo dest = ticketInfoManager.get(ticketInfo.getId());
        beanMapper.copy(ticketInfo, dest);

        Date now = new Date();
        dest.setUpdateTime(now);
        ticketInfoManager.save(dest);

        return "redirect:/ticket/my/list.do";
    }

    @RequestMapping("view")
    public String view(@RequestParam("id") Long id, Model model) {
        this.calculateTicketInfo(id, model);

        return "ticket/my/view";
    }

    @RequestMapping("upload")
    @ResponseBody
    public String upload(@RequestParam("attachment[]") MultipartFile attachment)
            throws Exception {
        logger.info("attachment : {}", attachment);

        // String tenantId = tenantHolder.getTenantId();
        String tenantId = "1";
        StoreDTO storeDto = this.storeClient.saveStore("ticket",
                new MultipartFileDataSource(attachment), tenantId);

        Map<String, Object> data = new HashMap<String, Object>();
        List<Map<String, Object>> files = new ArrayList<Map<String, Object>>();
        data.put("files", files);

        Map<String, Object> map = new HashMap<String, Object>();
        files.add(map);
        map.put("name", attachment.getOriginalFilename());
        map.put("url", "download.do?key=" + storeDto.getKey());
        map.put("thumbnailUrl", "download.do?key=" + storeDto.getKey());
        map.put("size", attachment.getSize());
        map.put("key", storeDto.getKey());

        return jsonMapper.toJson(data);
    }

    @RequestMapping("download")
    @ResponseBody
    public void download(@RequestParam("key") String key,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // String tenantId = tenantHolder.getTenantId();
        String tenantId = "1";
        StoreDTO storeDto = this.storeClient.getStore("ticket", key, tenantId);

        if (storeDto == null) {
            return;
        }

        IOUtils.copy(storeDto.getDataSource().getInputStream(),
                response.getOutputStream());
    }

    public void calculateTicketInfo(Long id, Model model) {
        if (id == null) {
            return;
        }

        model.addAttribute("ticketInfo", ticketInfoManager.get(id));

        String hql = "from TicketComment where ticketInfo.id=? order by createTime desc";
        List<TicketComment> ticketComments = ticketCommentManager.find(hql, id);
        model.addAttribute("ticketComments", ticketComments);
    }

    @RequestMapping("sendMessage")
    public String sendMessage(@RequestParam("id") Long id,
            @RequestParam("message") String message) {
        String userId = currentUserHolder.getUserId();
        TicketInfo ticketInfo = ticketInfoManager.get(id);
        TicketComment ticketComment = new TicketComment();
        ticketComment.setContent(message);
        ticketComment.setCreator(userId);
        ticketComment.setTicketInfo(ticketInfo);
        ticketComment.setCreateTime(new Date());
        ticketComment.setStatus("normal");
        ticketComment.setType("send");
        ticketCommentManager.save(ticketComment);

        if ("pending".equals(ticketInfo.getStatus())) {
            ticketInfo.setStatus("open");
        }

        Date now = new Date();
        ticketInfo.setUpdateTime(now);

        ticketInfoManager.save(ticketInfo);

        return "redirect:/ticket/my/view.do?id=" + id;
    }

    @RequestMapping("markClose")
    public String markClose(@RequestParam("type") String type,
            @RequestParam("id") Long id, @RequestParam("message") String message) {
        String userId = currentUserHolder.getUserId();
        TicketInfo ticketInfo = ticketInfoManager.get(id);
        ticketInfo.setStatus("closed");

        Date now = new Date();
        ticketInfo.setUpdateTime(now);
        ticketInfoManager.save(ticketInfo);

        // return "redirect:/ticket/index.do?type=" + ticketInfo.getStatus()
        // + "&id=" + id;
        return "redirect:/ticket/my/view.do?id=" + id;
    }

    @RequestMapping("survey")
    public String survey(@RequestParam("id") Long id, Model model) {
        model.addAttribute("id", id);

        return "ticket/my/survey";
    }

    @RequestMapping("doSurvey")
    public String doSurvey(@RequestParam("id") Long id,
            @RequestParam("survey") String survey,
            @RequestParam("surveyMessage") String surveyMessage) {
        TicketInfo ticketInfo = this.ticketInfoManager.get(id);
        ticketInfo.setStatus("closed");
        ticketInfo.setSurvey(survey);
        ticketInfo.setSurveyMessage(surveyMessage);
        ticketInfoManager.save(ticketInfo);

        return "redirect:/ticket/my/doSurveyResult.do?id=" + id;
    }

    @RequestMapping("doSurveyResult")
    public String doSurveyResult() {
        return "ticket/my/doSurveyResult";
    }

    // ~ ======================================================================
    @Resource
    public void setTicketInfoManager(TicketInfoManager ticketInfoManager) {
        this.ticketInfoManager = ticketInfoManager;
    }

    @Resource
    public void setTicketCommentManager(
            TicketCommentManager ticketCommentManager) {
        this.ticketCommentManager = ticketCommentManager;
    }

    @Resource
    public void setTicketAttachmentManager(
            TicketAttachmentManager ticketAttachmentManager) {
        this.ticketAttachmentManager = ticketAttachmentManager;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setStoreClient(StoreClient storeClient) {
        this.storeClient = storeClient;
    }
}
