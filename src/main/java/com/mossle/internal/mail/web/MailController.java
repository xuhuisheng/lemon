package com.mossle.internal.mail.web;

import javax.annotation.Resource;

import com.mossle.internal.mail.service.MailDataService;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("mail")
public class MailController {
    private MailDataService mailDataService;
    private String from;
    private String to;
    private String subject;
    private String content;

    @RequestMapping("mail-input")
    public String input() {
        return "mail/mail-input";
    }

    @RequestMapping("mail-send")
    public String send(@RequestParam("from") String from,
            @RequestParam("to") String to,
            @RequestParam("subject") String subject,
            @RequestParam("content") String content) {
        for (String line : to.split("\n")) {
            mailDataService.saveMailQueue(from, line, subject, content);
        }

        return "redirect:/mail/mail-input.do";
    }

    @Resource
    public void setMailDataService(MailDataService mailDataService) {
        this.mailDataService = mailDataService;
    }
}
