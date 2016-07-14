package com.mossle.javamail.rs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("javamail")
public class JavamailResource {
    private static Logger logger = LoggerFactory
            .getLogger(JavamailResource.class);

    @Path("tree")
    @POST
    public List<Map<String, Object>> tree() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        this.addInbox(list);
        this.addDraft(list);
        this.addOutbox(list);
        this.addDeleted(list);

        return list;
    }

    public void addInbox(List<Map<String, Object>> list) {
        Map<String, Object> map = new HashMap<String, Object>();
        list.add(map);
        map.put("ref", "INBOX");
        map.put("name", "收件箱");
    }

    public void addDraft(List<Map<String, Object>> list) {
        Map<String, Object> map = new HashMap<String, Object>();
        list.add(map);
        map.put("ref", "DRAFT");
        map.put("name", "草稿箱");
    }

    public void addOutbox(List<Map<String, Object>> list) {
        Map<String, Object> map = new HashMap<String, Object>();
        list.add(map);
        map.put("ref", "OUTBOX");
        map.put("name", "已发送邮件");
    }

    public void addDeleted(List<Map<String, Object>> list) {
        Map<String, Object> map = new HashMap<String, Object>();
        list.add(map);
        map.put("ref", "DELETED");
        map.put("name", "已删除邮件");
    }
}
