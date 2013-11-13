package com.mossle.core.struts2;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.Validateable;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.ValidationAwareSupport;

public class BaseAction implements Action, Validateable, ValidationAware {
    private ValidationAwareSupport validationAware = new ValidationAwareSupport();

    public void setActionErrors(Collection<String> errorMessages) {
        validationAware.setActionErrors(errorMessages);
    }

    public Collection<String> getActionErrors() {
        return validationAware.getActionErrors();
    }

    public void setActionMessages(Collection<String> messages) {
        validationAware.setActionMessages(messages);
    }

    public Collection<String> getActionMessages() {
        return validationAware.getActionMessages();
    }

    public void setFieldErrors(Map<String, List<String>> errorMap) {
        validationAware.setFieldErrors(errorMap);
    }

    public Map<String, List<String>> getFieldErrors() {
        return validationAware.getFieldErrors();
    }

    public void addActionError(String anErrorMessage) {
        validationAware.addActionError(anErrorMessage);
    }

    public void addActionMessage(String aMessage) {
        validationAware.addActionMessage(aMessage);
    }

    public void addFieldError(String fieldName, String errorMessage) {
        validationAware.addFieldError(fieldName, errorMessage);
    }

    public String input() throws Exception {
        return INPUT;
    }

    public String doDefault() throws Exception {
        return SUCCESS;
    }

    public String execute() throws Exception {
        return SUCCESS;
    }

    public boolean hasActionErrors() {
        return validationAware.hasActionErrors();
    }

    public boolean hasActionMessages() {
        return validationAware.hasActionMessages();
    }

    public boolean hasErrors() {
        return validationAware.hasErrors();
    }

    public boolean hasFieldErrors() {
        return validationAware.hasFieldErrors();
    }

    public void clearFieldErrors() {
        validationAware.clearFieldErrors();
    }

    public void clearActionErrors() {
        validationAware.clearActionErrors();
    }

    public void clearMessages() {
        validationAware.clearMessages();
    }

    public void clearErrors() {
        validationAware.clearErrors();
    }

    public void clearErrorsAndMessages() {
        validationAware.clearErrorsAndMessages();
    }

    public void validate() {
    }

    public void pause(String result) {
    }
}
