package com.mossle.forum.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.api.scope.ScopeHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;

import com.mossle.forum.domain.ForumTopic;
import com.mossle.forum.manager.ForumTopicManager;

import com.mossle.security.util.SpringSecurityUtils;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("forum")
public class ForumTopicController {
    private ForumTopicManager forumTopicManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;

    @RequestMapping("forum-topic-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);

        propertyFilters.add(new PropertyFilter("EQS_senderUsername",
                SpringSecurityUtils.getCurrentUsername()));
        page = forumTopicManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "forum/forum-topic-list";
    }

    @RequestMapping("forum-topic-listReceived")
    public String listReceived(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);

        propertyFilters.add(new PropertyFilter("EQS_receiverUsername",
                SpringSecurityUtils.getCurrentUsername()));
        page = forumTopicManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "forum/forum-topic-listReceived";
    }

    @RequestMapping("forum-topic-listSent")
    public String listSent(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);

        propertyFilters.add(new PropertyFilter("EQS_receiverUsername",
                SpringSecurityUtils.getCurrentUsername()));
        page = forumTopicManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "forum/forum-topic-listSent";
    }

    @RequestMapping("forum-topic-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            ForumTopic forumTopic = forumTopicManager.get(id);
            model.addAttribute("model", forumTopic);
        }

        return "forum/forum-topic-input";
    }

    @RequestMapping("forum-topic-save")
    public String save(@ModelAttribute ForumTopic forumTopic,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        ForumTopic dest = null;
        Long id = forumTopic.getId();

        if (id != null) {
            dest = forumTopicManager.get(id);
            beanMapper.copy(forumTopic, dest);
        } else {
            dest = forumTopic;

            String userId = SpringSecurityUtils.getCurrentUserId();
            dest.setUserId(Long.parseLong(userId));
            dest.setCreateTime(new Date());
        }

        forumTopicManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/forum/forum-topic-list.do";
    }

    @RequestMapping("forum-topic-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<ForumTopic> forumTopics = forumTopicManager
                .findByIds(selectedItem);

        forumTopicManager.removeAll(forumTopics);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/forum/forum-topic-list.do";
    }

    @RequestMapping("forum-topic-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = forumTopicManager.pagedQuery(page, propertyFilters);

        List<ForumTopic> forumTopics = (List<ForumTopic>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("msg info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(forumTopics);
        exportor.export(response, tableModel);
    }

    @RequestMapping("forum-topic-view")
    public String view(Model model) throws Exception {
        List<ForumTopic> forumTopics = forumTopicManager.getAll();

        model.addAttribute("forumTopics", forumTopics);

        return "forum/forum-topic-view";
    }

    @RequestMapping("forum-topic-create")
    public String create() throws Exception {
        return "forum/forum-topic-create";
    }

    @RequestMapping("forum-topic-createTopic")
    public String createTopic(@ModelAttribute ForumTopic forumTopic)
            throws Exception {
        String userId = SpringSecurityUtils.getCurrentUserId();
        forumTopic.setUserId(Long.parseLong(userId));
        forumTopic.setCreateTime(new Date());
        forumTopicManager.save(forumTopic);

        return "redirect:/forum/forum-topic-view.do";
    }

    // ~ ======================================================================
    @Resource
    public void setForumTopicManager(ForumTopicManager forumTopicManager) {
        this.forumTopicManager = forumTopicManager;
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
}
