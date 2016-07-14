package com.mossle.javamail.web;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.core.auth.CurrentUserHolder;
import com.mossle.core.mapper.BeanMapper;

import com.mossle.javamail.persistence.domain.JavamailConfig;
import com.mossle.javamail.persistence.domain.JavamailMessage;
import com.mossle.javamail.persistence.manager.JavamailConfigManager;
import com.mossle.javamail.persistence.manager.JavamailMessageManager;
import com.mossle.javamail.service.JavamailQueue;
import com.mossle.javamail.service.JavamailService;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("javamail")
@Controller
public class JavamailController {
    private JavamailConfigManager javamailConfigManager;
    private JavamailMessageManager javamailMessageManager;
    private CurrentUserHolder currentUserHolder;
    private BeanMapper beanMapper = new BeanMapper();
    private JavamailService javamailService;
    private JavamailQueue javamailQueue;

    @RequestMapping("index")
    public String index(@RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "folder", required = false) String folder,
            Model model) throws Exception {
        String userId = currentUserHolder.getUserId();
        JavamailConfig javamailConfig = javamailConfigManager.findUniqueBy(
                "userId", userId);

        if (javamailConfig != null) {
            // javamailService.receive(javamailConfig);
            javamailQueue.receive(userId);

            if (folder == null) {
                folder = "INBOX";
            }

            String hql = "from JavamailMessage where javamailConfig.id=? and folder=? order by sendTime desc";
            List<JavamailMessage> javamailMessages = javamailMessageManager
                    .find(hql, javamailConfig.getId(), folder);
            model.addAttribute("javamailMessages", javamailMessages);
        }

        if (id != null) {
            model.addAttribute("javamailMessage",
                    javamailMessageManager.get(id));
        }

        return "javamail/index";
    }

    @RequestMapping("create")
    public String create() {
        return "javamail/create";
    }

    @RequestMapping("send")
    public String send(@RequestParam("receiver") String receiver,
            @RequestParam(value = "cc", required = false) String cc,
            @RequestParam(value = "bcc", required = false) String bcc,
            @RequestParam("subject") String subject,
            @RequestParam("content") String content) throws Exception {
        String userId = currentUserHolder.getUserId();
        JavamailConfig javamailConfig = javamailConfigManager.findUniqueBy(
                "userId", userId);
        // this.javamailService.send(receiver, subject, content, javamailConfig);
        javamailQueue.send(userId, receiver, cc, bcc, subject, content);

        return "redirect:/javamail/index.do";
    }

    @RequestMapping("config")
    public String config(Model model) throws Exception {
        JavamailConfig javamailConfig = javamailConfigManager.findUniqueBy(
                "userId", currentUserHolder.getUserId());

        if (javamailConfig != null) {
            model.addAttribute("javamailConfig", javamailConfig);
        }

        return "javamail/config";
    }

    @RequestMapping("configSave")
    public String configSave(@ModelAttribute JavamailConfig javamailConfig,
            Model model) throws Exception {
        JavamailConfig dest = javamailConfigManager.findUniqueBy("userId",
                currentUserHolder.getUserId());

        if (dest == null) {
            javamailConfig.setUserId(currentUserHolder.getUserId());
            javamailConfig.setPriority(0);
            javamailConfigManager.save(javamailConfig);
        } else {
            beanMapper.copy(javamailConfig, dest);
            javamailConfigManager.save(dest);
        }

        return "redirect:/javamail/config.do";
    }

    @Resource
    public void setJavamailConfigManager(
            JavamailConfigManager javamailConfigManager) {
        this.javamailConfigManager = javamailConfigManager;
    }

    @Resource
    public void setJavamailMessageManager(
            JavamailMessageManager javamailMessageManager) {
        this.javamailMessageManager = javamailMessageManager;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setJavamailService(JavamailService javamailService) {
        this.javamailService = javamailService;
    }

    @Resource
    public void setJavamailQueue(JavamailQueue javamailQueue) {
        this.javamailQueue = javamailQueue;
    }
}
