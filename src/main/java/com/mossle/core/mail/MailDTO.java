package com.mossle.core.mail;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.io.InputStreamSource;

public class MailDTO {
    private String from;
    private String to;
    private String cc;
    private String bcc;
    private String subject;
    private String content;
    private Map<String, InputStreamSource> inlines = new LinkedHashMap<String, InputStreamSource>();
    private Map<String, InputStreamSource> attachments = new LinkedHashMap<String, InputStreamSource>();
    private boolean success;
    private Throwable exception;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, InputStreamSource> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, InputStreamSource> attachments) {
        this.attachments = attachments;
    }

    public void addAttachment(String name, InputStreamSource attachment) {
        attachments.put(name, attachment);
    }

    public Map<String, InputStreamSource> getInlines() {
        return inlines;
    }

    public void setInlines(Map<String, InputStreamSource> inlines) {
        this.inlines = inlines;
    }

    public void addInline(String name, InputStreamSource inline) {
        inlines.put(name, inline);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }
}
