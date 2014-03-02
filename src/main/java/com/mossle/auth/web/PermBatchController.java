package com.mossle.auth.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.api.scope.ScopeConnector;
import com.mossle.api.scope.ScopeHolder;

import com.mossle.auth.domain.Perm;
import com.mossle.auth.domain.PermType;
import com.mossle.auth.manager.PermManager;
import com.mossle.auth.manager.PermTypeManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("auth")
public class PermBatchController {
    private static Logger logger = LoggerFactory
            .getLogger(PermBatchController.class);
    private PermManager permManager;
    private PermTypeManager permTypeManager;
    private ScopeConnector scopeConnector;

    @RequestMapping("perm-batch-list")
    public String list(Model model) {
        List<Perm> perms = permManager.findBy("scopeId",
                ScopeHolder.getScopeId());
        StringBuilder buff = new StringBuilder();

        for (Perm perm : perms) {
            buff.append(perm.getCode()).append(",").append(perm.getName())
                    .append(",").append(perm.getPermType().getName())
                    .append("\n");
        }

        String text = buff.toString();
        model.addAttribute("text", text);

        return "auth/perm-batch-list";
    }

    @RequestMapping("perm-batch-save")
    public String save(@RequestParam("text") String text,
            RedirectAttributes redirectAttributes) {
        if (text != null) {
            // code,name,type
            for (String str : text.split("\n")) {
                str = str.trim();

                String[] array = str.split(",");

                if (array.length < 3) {
                    String msg = str
                            + " is invalid, format should be 'code,name,type'.";
                    redirectAttributes.addFlashAttribute("message", msg);
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

        return "redirect:/auth/perm-list.do";
    }

    // ~ ======================================================================
    @Resource
    public void setPermManager(PermManager permManager) {
        this.permManager = permManager;
    }

    @Resource
    public void setPermTypeManager(PermTypeManager permTypeManager) {
        this.permTypeManager = permTypeManager;
    }

    @Resource
    public void setScopeConnector(ScopeConnector scopeConnector) {
        this.scopeConnector = scopeConnector;
    }
}
