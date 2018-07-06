package com.mossle.ticket.web;

import java.io.InputStream;

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
import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.store.MultipartFileDataSource;

import com.mossle.ticket.persistence.domain.TicketAttachment;
import com.mossle.ticket.persistence.domain.TicketCatalog;
import com.mossle.ticket.persistence.domain.TicketComment;
import com.mossle.ticket.persistence.domain.TicketInfo;
import com.mossle.ticket.persistence.manager.TicketAttachmentManager;
import com.mossle.ticket.persistence.manager.TicketCatalogManager;
import com.mossle.ticket.persistence.manager.TicketCommentManager;
import com.mossle.ticket.persistence.manager.TicketInfoManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("ticket")
public class TicketController {
    private static Logger logger = LoggerFactory
            .getLogger(TicketController.class);
    private TicketCatalogManager ticketCatalogManager;
    private TicketInfoManager ticketInfoManager;
    private TicketCommentManager ticketCommentManager;
    private TicketAttachmentManager ticketAttachmentManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private JsonMapper jsonMapper = new JsonMapper();
    private CurrentUserHolder currentUserHolder;
    private UserConnector userConnector;
    private StoreConnector storeConnector;

    @RequestMapping("index")
    public String index(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "id", required = false) Long id, Model model) {
        this.calculateTotal(model);
        this.calculateList(type, model);
        this.calculateTicketInfo(id, model);

        return "ticket/index";
    }

    @RequestMapping("assign")
    public String assign(@RequestParam("type") String type,
            @RequestParam("id") Long id, Model model) {
        this.calculateTotal(model);
        this.calculateList(type, model);
        this.calculateTicketInfo(id, model);

        return "ticket/assign";
    }

    @RequestMapping("create")
    public String create(Model model) {
        return "ticket/create";
    }

    @RequestMapping("list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = ticketInfoManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "ticket/list";
    }

    @RequestMapping("save")
    public String save(@ModelAttribute TicketInfo ticketInfo,
            @RequestParam("files") List<String> files) throws Exception {
        String userId = currentUserHolder.getUserId();
        Date now = new Date();
        ticketInfo.setCreator(userId);
        ticketInfo.setCreateTime(now);
        ticketInfo.setStatus("new");
        ticketInfo.setUpdateTime(now);
        ticketInfoManager.save(ticketInfo);

        List<TicketAttachment> ticketAttachments = ticketAttachmentManager
                .findBy("ticketInfo", ticketInfo);

        List<String> inserts = new ArrayList<String>();
        List<TicketAttachment> removes = new ArrayList<TicketAttachment>();

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

        for (TicketAttachment ticketAttachment : ticketAttachments) {
            if (!files.contains(ticketAttachment.getCode())) {
                removes.add(ticketAttachment);
            }
        }

        for (String fileKey : inserts) {
            StoreDTO storeDto = storeConnector.getStore("ticket", fileKey, "1");
            TicketAttachment ticketAttachment = new TicketAttachment();
            ticketAttachment.setTicketInfo(ticketInfo);
            ticketAttachment.setCode(fileKey);
            ticketAttachment.setName(storeDto.getDisplayName());
            ticketAttachmentManager.save(ticketAttachment);
        }

        ticketAttachmentManager.removeAll(removes);

        return "redirect:/ticket/list.do";
    }

    @RequestMapping("claim")
    public String claim(@RequestParam("id") Long id) {
        String userId = currentUserHolder.getUserId();
        TicketInfo ticketInfo = ticketInfoManager.get(id);
        ticketInfo.setAssignee(userId);
        ticketInfo.setStatus("open");

        Date now = new Date();
        ticketInfo.setUpdateTime(now);
        ticketInfoManager.save(ticketInfo);

        return "redirect:/ticket/index.do?type=open&id=" + id;
    }

    @RequestMapping("replyMessage")
    public String replyMessage(@RequestParam("type") String type,
            @RequestParam("id") Long id, @RequestParam("message") String message) {
        String userId = currentUserHolder.getUserId();
        TicketInfo ticketInfo = ticketInfoManager.get(id);
        TicketComment ticketComment = new TicketComment();
        ticketComment.setContent(message);
        ticketComment.setCreator(userId);
        ticketComment.setTicketInfo(ticketInfo);
        ticketComment.setCreateTime(new Date());
        ticketComment.setStatus("normal");
        ticketComment.setType("reply");
        ticketCommentManager.save(ticketComment);

        if ("open".equals(ticketInfo.getStatus())) {
            ticketInfo.setStatus("pending");
        }

        Date now = new Date();
        ticketInfo.setUpdateTime(now);

        ticketInfoManager.save(ticketInfo);

        return "redirect:/ticket/index.do?type=" + ticketInfo.getStatus()
                + "&id=" + id;
    }

    @RequestMapping("markResolve")
    public String markResolve(@RequestParam("type") String type,
            @RequestParam("id") Long id, @RequestParam("message") String message) {
        String userId = currentUserHolder.getUserId();
        TicketInfo ticketInfo = ticketInfoManager.get(id);
        ticketInfo.setStatus("resolved");

        Date now = new Date();
        ticketInfo.setUpdateTime(now);
        ticketInfoManager.save(ticketInfo);

        return "redirect:/ticket/index.do?type=" + ticketInfo.getStatus()
                + "&id=" + id;
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

        return "redirect:/ticket/index.do?type=" + ticketInfo.getStatus()
                + "&id=" + id;
    }

    @RequestMapping("edit")
    public String edit(@RequestParam("id") Long id, Model model) {
        TicketInfo ticketInfo = ticketInfoManager.get(id);
        model.addAttribute("ticketInfo", ticketInfo);

        return "ticket/edit";
    }

    @RequestMapping("update")
    public String update(@ModelAttribute TicketInfo ticketInfo) {
        String userId = currentUserHolder.getUserId();
        TicketInfo dest = ticketInfoManager.get(ticketInfo.getId());
        beanMapper.copy(ticketInfo, dest);

        Date now = new Date();
        dest.setUpdateTime(now);
        ticketInfoManager.save(dest);

        return "redirect:/ticket/index.do?type=" + dest.getStatus() + "&id="
                + ticketInfo.getId();
    }

    @RequestMapping("doAssign")
    public String doAssign(@RequestParam("id") Long id,
            @RequestParam("username") String username, Model model) {
        UserDTO userDto = userConnector.findByUsername(username, "1");

        if (userDto != null) {
            TicketInfo ticketInfo = ticketInfoManager.get(id);
            ticketInfo.setCreator(userDto.getId());

            if ("pending".equals(ticketInfo.getStatus())) {
                ticketInfo.setStatus("open");
            }

            Date now = new Date();
            ticketInfo.setUpdateTime(now);

            ticketInfoManager.save(ticketInfo);
        }

        return "redirect:/ticket/index.do?type=all";
    }

    @RequestMapping("view")
    public String view(@RequestParam("id") Long id, Model model) {
        this.calculateTicketInfo(id, model);

        return "ticket/view";
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

        return "redirect:/ticket/view.do?id=" + id;
    }

    @RequestMapping("survey")
    public String survey(@RequestParam("id") Long id, Model model) {
        model.addAttribute("id", id);

        return "ticket/survey";
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

        return "redirect:/ticket/doSurveyResult.do?id=" + id;
    }

    @RequestMapping("doSurveyResult")
    public String doSurveyResult() {
        return "ticket/doSurveyResult";
    }

    @RequestMapping("upload")
    @ResponseBody
    public String upload(@RequestParam("attachment[]") MultipartFile attachment)
            throws Exception {
        logger.info("attachment : {}", attachment);

        // String tenantId = tenantHolder.getTenantId();
        String tenantId = "1";
        StoreDTO storeDto = storeConnector.saveStore("ticket",
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
        StoreDTO storeDto = storeConnector.getStore("ticket", key, tenantId);
        IOUtils.copy(storeDto.getDataSource().getInputStream(),
                response.getOutputStream());
    }

    // ~
    public void calculateTotal(Model model) {
        String userId = currentUserHolder.getUserId();
        int total = ticketInfoManager.getCount(
                "select count(*) from TicketInfo where assignee=?", userId);
        model.addAttribute("total", total);

        int totalOpen = ticketInfoManager
                .getCount(
                        "select count(*) from TicketInfo where assignee=? and status='open'",
                        userId);
        model.addAttribute("totalOpen", totalOpen);

        int totalNew = ticketInfoManager
                .getCount("select count(*) from TicketInfo where status='new'");
        model.addAttribute("totalNew", totalNew);

        int totalPending = ticketInfoManager
                .getCount(
                        "select count(*) from TicketInfo where assignee=? and status='pending'",
                        userId);
        model.addAttribute("totalPending", totalPending);

        int totalResolved = ticketInfoManager
                .getCount(
                        "select count(*) from TicketInfo where assignee=? and status='resolved'",
                        userId);
        model.addAttribute("totalResolved", totalResolved);

        int totalClosed = ticketInfoManager
                .getCount(
                        "select count(*) from TicketInfo where assignee=? and status='closed'",
                        userId);
        model.addAttribute("totalClosed", totalClosed);
    }

    public void calculateList(String type, Model model) {
        if (StringUtils.isBlank(type)) {
            return;
        }

        String userId = currentUserHolder.getUserId();

        if ("all".equals(type)) {
            Page page = ticketInfoManager
                    .pagedQuery(
                            "from TicketInfo where assignee=? order by createTime desc",
                            1, 10, userId);
            model.addAttribute("page", page);
        } else if ("open".equals(type)) {
            Page page = ticketInfoManager
                    .pagedQuery(
                            "from TicketInfo where assignee=? and status='open' order by createTime desc",
                            1, 10, userId);
            model.addAttribute("page", page);
        } else if ("new".equals(type)) {
            Page page = ticketInfoManager
                    .pagedQuery(
                            "from TicketInfo where status='new' order by createTime desc",
                            1, 10);
            model.addAttribute("page", page);
        } else if ("pending".equals(type)) {
            Page page = ticketInfoManager
                    .pagedQuery(
                            "from TicketInfo where assignee=? and status='pending' order by createTime desc",
                            1, 10, userId);
            model.addAttribute("page", page);
        } else if ("resolved".equals(type)) {
            Page page = ticketInfoManager
                    .pagedQuery(
                            "from TicketInfo where assignee=? and status='resolved' order by createTime desc",
                            1, 10, userId);
            model.addAttribute("page", page);
        } else if ("closed".equals(type)) {
            Page page = ticketInfoManager
                    .pagedQuery(
                            "from TicketInfo where assignee=? and status='closed' order by createTime desc",
                            1, 10, userId);
            model.addAttribute("page", page);
        } else {
            logger.info("unsupport : {}", type);
        }
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

    // ~ ======================================================================
    @Resource
    public void setTicketCatalogManager(
            TicketCatalogManager ticketCatalogManager) {
        this.ticketCatalogManager = ticketCatalogManager;
    }

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
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    @Resource
    public void setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;
    }
}
