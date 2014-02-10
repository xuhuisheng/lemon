package com.mossle.auth.web.auth;

import java.util.ArrayList;
import java.util.List;

import com.mossle.api.scope.ScopeConnector;
import com.mossle.api.scope.ScopeHolder;

import com.mossle.auth.domain.Perm;
import com.mossle.auth.domain.PermType;
import com.mossle.auth.manager.PermManager;
import com.mossle.auth.manager.PermTypeManager;

import com.mossle.core.struts2.BaseAction;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Results({ @Result(name = PermBatchAction.RELOAD, location = "perm-batch.do?operationMode=RETRIEVE", type = "redirect") })
public class PermBatchAction extends BaseAction implements ModelDriven<Perm>,
        Preparable {
    private static Logger logger = LoggerFactory
            .getLogger(PermBatchAction.class);
    public static final String RELOAD = "reload";
    private PermManager permManager;
    private PermTypeManager permTypeManager;
    private ScopeConnector scopeConnector;
    private String text;

    public String execute() {
        List<Perm> perms = permManager.findBy("scopeId",
                ScopeHolder.getScopeId());
        StringBuilder buff = new StringBuilder();

        for (Perm perm : perms) {
            buff.append(perm.getCode()).append(",").append(perm.getName())
                    .append(",").append(perm.getPermType().getName())
                    .append("\n");
        }

        text = buff.toString();

        return SUCCESS;
    }

    public String save() {
        if (text != null) {
            // code,name,type
            for (String str : text.split("\n")) {
                str = str.trim();

                String[] array = str.split(",");

                if (array.length < 3) {
                    addActionMessage(str
                            + " is invalid, format should be 'code,name,type'.");
                }

                String code = array[0];
                String name = array[1];
                String type = array[2];

                Perm perm = permManager.findUnique(
                        "from Perm where code=? and scopeId=?", code,
                        ScopeHolder.getScopeId());
                PermType permType = permTypeManager.findUniqueBy("name", type);

                if (permType == null) {
                    permType = new PermType();
                    permType.setName(type);
                    permType.setType(0);
                    permType.setScopeId(ScopeHolder.getScopeId());
                    permTypeManager.save(permType);
                }

                if (perm == null) {
                    perm = new Perm();
                    perm.setCode(code);
                    perm.setName(name);
                    perm.setPermType(permType);
                    perm.setScopeId(ScopeHolder.getScopeId());
                    permManager.save(perm);
                }
            }
        }

        return RELOAD;
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public Perm getModel() {
        return null;
    }

    public void setPermManager(PermManager permManager) {
        this.permManager = permManager;
    }

    public void setPermTypeManager(PermTypeManager permTypeManager) {
        this.permTypeManager = permTypeManager;
    }

    public void setScopeConnector(ScopeConnector scopeConnector) {
        this.scopeConnector = scopeConnector;
    }

    // ~ ======================================================================
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
