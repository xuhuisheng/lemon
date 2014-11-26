package com.mossle.internal.mail.service;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.util.StringUtils;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;
import com.mossle.ext.mail.MailDTO;
import com.mossle.ext.mail.MailHelper;
import com.mossle.ext.mail.MailServerInfo;
import com.mossle.ext.store.StoreConnector;
import com.mossle.ext.store.StoreDTO;
import com.mossle.ext.template.TemplateService;

import com.mossle.internal.mail.domain.MailAttachment;
import com.mossle.internal.mail.domain.MailConfig;
import com.mossle.internal.mail.domain.MailHistory;
import com.mossle.internal.mail.domain.MailQueue;
import com.mossle.internal.mail.domain.MailTemplate;
import com.mossle.internal.mail.manager.MailAttachmentManager;
import com.mossle.internal.mail.manager.MailConfigManager;
import com.mossle.internal.mail.manager.MailHistoryManager;
import com.mossle.internal.mail.manager.MailQueueManager;
import com.mossle.internal.mail.manager.MailTemplateManager;

import org.springframework.core.io.FileSystemResource;

import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
@Transactional
public class MailDataService {
    private MailConfigManager mailConfigManager;
    private MailQueueManager mailQueueManager;
    private MailHistoryManager mailHistoryManager;
    private MailTemplateManager mailTemplateManager;
    private StoreConnector storeConnector;
    private BeanMapper beanMapper = new BeanMapper();
    private MailHelper mailHelper;
    private TemplateService templateService;
    private JsonMapper jsonMapper = new JsonMapper();

    public void send(String to, String subject, String content,
            String configCode) {
        MailQueue mailQueue = new MailQueue();

        mailQueue.setReceiver(to);
        mailQueue.setSubject(subject);
        mailQueue.setContent(content);
        mailQueue.setMailConfig(mailConfigManager.findUniqueBy("name",
                configCode));
        mailQueueManager.save(mailQueue);
    }

    public void sendTemplate(String to, String data, String templateCode,
            String configCode) {
        MailQueue mailQueue = new MailQueue();
        mailQueue.setReceiver(to);
        mailQueue.setData(data);
        mailQueue.setMailTemplate(mailTemplateManager.findUniqueBy("name",
                templateCode));
        mailQueue.setMailConfig(mailConfigManager.findUniqueBy("name",
                configCode));
        mailQueueManager.save(mailQueue);
    }

    public void saveMailQueue(String from, String to, String subject,
            String content, long mailConfigId) {
        MailConfig mailConfig = mailConfigManager.get(mailConfigId);
        MailQueue mailQueue = new MailQueue();
        mailQueue.setSender(from);
        mailQueue.setReceiver(to);
        mailQueue.setSubject(subject);
        mailQueue.setContent(content);
        mailQueue.setMailConfig(mailConfig);
        mailQueueManager.save(mailQueue);
    }

    public void saveMailQueue(String from, String to, String subject,
            String content) {
        MailQueue mailQueue = new MailQueue();
        mailQueue.setSender(from);
        mailQueue.setReceiver(to);
        mailQueue.setSubject(subject);
        mailQueue.setContent(content);
        mailQueueManager.save(mailQueue);
    }

    public List<MailQueue> findTopMailQueues(int size) {
        return (List<MailQueue>) mailQueueManager.pagedQuery("from MailQueue",
                1, size).getResult();
    }

    public void processMailQueue(MailQueue mailQueue) throws Exception {
        MailDTO mailDto = new MailDTO();
        MailDTO resultMailDto = null;

        if (mailQueue.getMailConfig() != null) {
            MailTemplate mailTemplate = mailQueue.getMailTemplate();
            MailConfig mailConfig = mailQueue.getMailConfig();

            MailServerInfo mailServerInfo = new MailServerInfo();
            mailServerInfo.setHost(mailConfig.getHost());
            mailServerInfo.setSmtpAuth(mailConfig.getSmtpAuth() == 1);
            mailServerInfo.setSmtpStarttls(mailConfig.getSmtpStarttls() == 1);
            mailServerInfo.setUsername(mailConfig.getUsername());
            mailServerInfo.setPassword(mailConfig.getPassword());
            mailServerInfo.setDefaultFrom(mailConfig.getDefaultFrom());
            mailServerInfo.setMode(mailConfig.getStatus());
            mailServerInfo.setTestMail(mailConfig.getTestMail());

            // config
            this.configSender(mailDto, mailQueue, mailTemplate);
            this.configReceiver(mailDto, mailQueue, mailTemplate);
            this.configCc(mailDto, mailQueue, mailTemplate);
            this.configBcc(mailDto, mailQueue, mailTemplate);

            if (StringUtils.isBlank(mailQueue.getSubject())) {
                if (StringUtils.isBlank(mailQueue.getData())) {
                    mailDto.setSubject(mailTemplate.getSubject());
                } else {
                    Map<String, Object> map = jsonMapper.fromJson(
                            mailQueue.getData(), Map.class);
                    String subject = templateService.renderText(
                            mailTemplate.getSubject(), map);
                    mailDto.setSubject(subject);
                }
            } else {
                mailDto.setSubject(mailQueue.getSubject());
            }

            if (StringUtils.isBlank(mailQueue.getContent())) {
                if (StringUtils.isBlank(mailQueue.getData())) {
                    mailDto.setContent(mailTemplate.getContent());
                } else {
                    Map<String, Object> map = jsonMapper.fromJson(
                            mailQueue.getData(), Map.class);
                    String content = templateService.renderText(
                            mailTemplate.getContent(), map);
                    mailDto.setContent(content);
                }
            } else {
                mailDto.setContent(mailQueue.getContent());
            }

            // mailDto.setFrom(mailTemplate.getSender());
            // mailDto.setTo(mailTemplate.getReceiver());
            // mailDto.setCc(mailTemplate.getCc());
            // mailDto.setBcc(mailTemplate.getBcc());
            // mailDto.setSubject(mailTemplate.getSubject());
            // mailDto.setContent(mailTemplate.getContent());
            if (mailTemplate != null) {
                for (MailAttachment mailAttachment : mailTemplate
                        .getMailAttachments()) {
                    mailDto.addAttachment(
                            mailAttachment.getName(),
                            storeConnector.get("mailattachment",
                                    mailAttachment.getPath()).getResource());
                }
            }

            resultMailDto = new MailHelper().send(mailDto, mailServerInfo);

            // model.addAttribute("mailDto", mailDto);
            if (!resultMailDto.isSuccess()) {
                StringWriter writer = new StringWriter();
                resultMailDto.getException().printStackTrace(
                        new PrintWriter(writer));

                // model.addAttribute("exception", writer.toString());
            }
        } else {
            mailDto.setFrom(mailQueue.getSender());
            mailDto.setTo(mailQueue.getReceiver());
            mailDto.setSubject(mailQueue.getSubject());
            mailDto.setContent(mailQueue.getContent());
            resultMailDto = mailHelper.send(mailDto);
        }

        this.saveMailHistory(mailQueue, resultMailDto);
    }

    public void configSender(MailDTO mailDto, MailQueue mailQueue,
            MailTemplate mailTemplate) {
        if (StringUtils.isBlank(mailQueue.getSender())) {
            if ((mailTemplate != null) && (mailTemplate.getSender() != null)) {
                mailDto.setFrom(mailTemplate.getSender().trim());
            }
        } else {
            mailDto.setFrom(mailQueue.getSender().trim());
        }
    }

    public void configReceiver(MailDTO mailDto, MailQueue mailQueue,
            MailTemplate mailTemplate) {
        if (StringUtils.isBlank(mailQueue.getReceiver())) {
            if ((mailTemplate != null) && (mailTemplate.getReceiver() != null)) {
                mailDto.setTo(mailTemplate.getReceiver().trim());
            }
        } else {
            mailDto.setTo(mailQueue.getReceiver().trim());
        }
    }

    public void configCc(MailDTO mailDto, MailQueue mailQueue,
            MailTemplate mailTemplate) {
        if (StringUtils.isBlank(mailQueue.getCc())) {
            if ((mailTemplate != null) && (mailTemplate.getCc() != null)) {
                mailDto.setCc(mailTemplate.getCc().trim());
            }
        } else {
            mailDto.setCc(mailQueue.getCc().trim());
        }
    }

    public void configBcc(MailDTO mailDto, MailQueue mailQueue,
            MailTemplate mailTemplate) {
        if (StringUtils.isBlank(mailQueue.getBcc())) {
            if ((mailTemplate != null) && (mailTemplate.getBcc() != null)) {
                mailDto.setBcc(mailTemplate.getBcc().trim());
            }
        } else {
            mailDto.setBcc(mailQueue.getBcc().trim());
        }
    }

    public void saveMailHistory(MailQueue mailQueue, MailDTO mailDto) {
        MailHistory mailHistory = new MailHistory();
        beanMapper.copy(mailDto, mailHistory);
        mailHistory.setSender(mailDto.getFrom());
        mailHistory.setReceiver(mailDto.getTo());
        mailHistory.setStatus(mailDto.isSuccess() ? "success" : "error");

        if (mailDto.getException() != null) {
            mailHistory.setInfo(mailDto.getException().getMessage());
        }

        mailHistory.setMailTemplate(mailQueue.getMailTemplate());
        mailHistory.setMailConfig(mailQueue.getMailConfig());
        mailHistory.setData(mailQueue.getData());
        mailHistory.setCreateTime(new Date());

        mailHistoryManager.save(mailHistory);
        mailQueueManager.remove(mailQueue);
    }

    public boolean checkConfigCodeExists(String configCode) {
        MailConfig mailConfig = mailConfigManager.findUniqueBy("name",
                configCode);

        return mailConfig != null;
    }

    public boolean checkTemplateCodeExists(String templateCode) {
        MailTemplate mailTemplate = mailTemplateManager.findUniqueBy("name",
                templateCode);

        return mailTemplate != null;
    }

    @Resource
    public void setMailConfigManager(MailConfigManager mailConfigManager) {
        this.mailConfigManager = mailConfigManager;
    }

    @Resource
    public void setMailQueueManager(MailQueueManager mailQueueManager) {
        this.mailQueueManager = mailQueueManager;
    }

    @Resource
    public void setMailHistoryManager(MailHistoryManager mailHistoryManager) {
        this.mailHistoryManager = mailHistoryManager;
    }

    @Resource
    public void setMailTemplateManager(MailTemplateManager mailTemplateManager) {
        this.mailTemplateManager = mailTemplateManager;
    }

    @Resource
    public void setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;
    }

    @Resource
    public void setMailHelper(MailHelper mailHelper) {
        this.mailHelper = mailHelper;
    }

    @Resource
    public void setTemplateService(TemplateService templateService) {
        this.templateService = templateService;
    }
}
