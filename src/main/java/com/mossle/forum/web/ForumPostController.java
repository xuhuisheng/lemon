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

import com.mossle.forum.domain.ForumPost;
import com.mossle.forum.domain.ForumTopic;
import com.mossle.forum.manager.ForumPostManager;
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
public class ForumPostController {
    private ForumPostManager forumPostManager;
    private ForumTopicManager forumTopicManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;

    @RequestMapping("forum-post-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);

        propertyFilters.add(new PropertyFilter("EQS_senderUsername",
                SpringSecurityUtils.getCurrentUsername()));
        page = forumPostManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "forum/forum-post-list";
    }

    @RequestMapping("forum-post-listReceived")
    public String listReceived(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);

        propertyFilters.add(new PropertyFilter("EQS_receiverUsername",
                SpringSecurityUtils.getCurrentUsername()));
        page = forumPostManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "forum/forum-post-listReceived";
    }

    @RequestMapping("forum-post-listSent")
    public String listSent(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_receiverUsername",
                SpringSecurityUtils.getCurrentUsername()));
        page = forumPostManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "forum/forum-post-listSent";
    }

    @RequestMapping("forum-post-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            ForumPost forumPost = forumPostManager.get(id);
            model.addAttribute("model", forumPost);
        }

        return "forum/forum-post-input";
    }

    @RequestMapping("forum-post-save")
    public String save(@ModelAttribute ForumPost forumPost,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        ForumPost dest = null;
        Long id = forumPost.getId();

        if (id != null) {
            dest = forumPostManager.get(id);
            beanMapper.copy(forumPost, dest);
        } else {
            dest = forumPost;

            String userId = SpringSecurityUtils.getCurrentUserId();
            dest.setUserId(Long.parseLong(userId));
            dest.setCreateTime(new Date());
        }

        forumPostManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/forum/forum-post-list.do";
    }

    @RequestMapping("forum-post-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<ForumPost> forumPosts = forumPostManager.findByIds(selectedItem);

        forumPostManager.removeAll(forumPosts);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/forum/forum-post-list.do";
    }

    @RequestMapping("forum-post-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = forumPostManager.pagedQuery(page, propertyFilters);

        List<ForumPost> forumPosts = (List<ForumPost>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("msg info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(forumPosts);
        exportor.export(response, tableModel);
    }

    @RequestMapping("forum-post-view")
    public String view(@RequestParam("id") Long id, Model model)
            throws Exception {
        ForumTopic forumTopic = forumTopicManager.get(id);
        model.addAttribute("forumTopic", forumTopic);

        return "forum/forum-post-view";
    }

    @RequestMapping("forum-post-createPost")
    public String createPost(@ModelAttribute ForumPost forumPost,
            @RequestParam("forumTopicId") Long forumTopicId) throws Exception {
        String userId = SpringSecurityUtils.getCurrentUserId();
        forumPost.setId(null);
        forumPost.setUserId(Long.parseLong(userId));
        forumPost.setCreateTime(new Date());
        forumPost.setForumTopic(forumTopicManager.get(forumTopicId));
        forumPostManager.save(forumPost);

        return "redirect:/forum/forum-post-view.do?id=" + forumTopicId;
    }

    // ~ ======================================================================
    @Resource
    public void setForumPostManager(ForumPostManager forumPostManager) {
        this.forumPostManager = forumPostManager;
    }

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
