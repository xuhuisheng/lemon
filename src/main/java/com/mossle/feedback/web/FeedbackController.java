package com.mossle.feedback.web;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.core.auth.CurrentUserHolder;
import com.mossle.core.page.Page;

import com.mossle.feedback.persistence.domain.FeedbackCatalog;
import com.mossle.feedback.persistence.domain.FeedbackInfo;
import com.mossle.feedback.persistence.manager.FeedbackCatalogManager;
import com.mossle.feedback.persistence.manager.FeedbackInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("feedback")
public class FeedbackController {
    private CurrentUserHolder currentUserHolder;
    private FeedbackCatalogManager feedbackCatalogManager;
    private FeedbackInfoManager feedbackInfoManager;

    @RequestMapping("submit")
    public String submit(@RequestParam("content") String content,
            @RequestParam("contact") String contact, Model model) {
        String userId = currentUserHolder.getUserId();
        FeedbackInfo feedbackInfo = new FeedbackInfo();
        feedbackInfo.setContent(content);
        feedbackInfo.setContact(contact);
        feedbackInfo.setCreateTime(new Date());
        feedbackInfo.setUserId(userId);
        feedbackInfoManager.save(feedbackInfo);

        return "feedback/submit";
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setFeedbackCatalogManager(
            FeedbackCatalogManager feedbackCatalogManager) {
        this.feedbackCatalogManager = feedbackCatalogManager;
    }

    @Resource
    public void setFeedbackInfoManager(FeedbackInfoManager feedbackInfoManager) {
        this.feedbackInfoManager = feedbackInfoManager;
    }
}
