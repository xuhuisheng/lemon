package com.mossle.cms.web.api;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserDTO;

import com.mossle.client.user.UserClient;

import com.mossle.cms.persistence.domain.*;
import com.mossle.cms.persistence.domain.CmsCatalog;
import com.mossle.cms.persistence.manager.*;
import com.mossle.cms.persistence.manager.CmsCatalogManager;
import com.mossle.cms.service.CmsService;

import com.mossle.core.page.Page;
import com.mossle.core.util.BaseDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("cms/api/comment")
public class CmsCommentApiController {
    private static Logger logger = LoggerFactory
            .getLogger(CmsCommentApiController.class);
    private CmsCatalogManager cmsCatalogManager;
    private TenantHolder tenantHolder;
    private CmsService cmsService;
    private CommentInfoManager commentInfoManager;
    private CommentThreadManager commentThreadManager;
    private CurrentUserHolder currentUserHolder;
    private UserClient userClient;
    private String avatarPrefix;

    @RequestMapping("")
    public BaseDTO fetch(
            @RequestParam("url") String url,
            @RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo)
            throws Exception {
        CommentThread commentThread = commentThreadManager.findUniqueBy("url",
                url);

        if (commentThread == null) {
            commentThread = new CommentThread();
            commentThread.setUrl(url);
            commentThreadManager.save(commentThread);
        }

        String hql = "from CommentInfo where commentInfoByParentId=null and commentThread=? order by id desc";
        int pageSize = 10;
        Page page = commentInfoManager.pagedQuery(hql, pageNo, pageSize,
                commentThread);
        List<CommentInfo> commentInfos = (List<CommentInfo>) page.getResult();
        List<Map<String, Object>> list = this.convertComments(commentInfos);
        page.setResult(list);

        BaseDTO baseDto = new BaseDTO();
        baseDto.setData(page);

        return baseDto;
    }

    @RequestMapping("create")
    public BaseDTO create(@RequestParam("content") String content,
            @RequestParam("parentId") long parentId,
            @RequestParam("url") String url) throws Exception {
        CommentThread commentThread = commentThreadManager.findUniqueBy("url",
                url);

        if (commentThread == null) {
            commentThread = new CommentThread();
            commentThread.setUrl(url);
            commentThreadManager.save(commentThread);
        }

        CommentInfo commentInfo = new CommentInfo();
        commentInfo.setContent(content);
        commentInfo.setCreateTime(new Date());
        commentInfo.setUrl(url);
        commentInfo.setCommentThread(commentThread);

        // commentInfo.setMode(1);
        if (parentId != 0L) {
            CommentInfo reply = commentInfoManager.get(parentId);

            CommentInfo parent;

            if (reply.getCommentInfoByParentId() != null) {
                parent = reply.getCommentInfoByParentId();
            } else {
                parent = reply;
            }

            commentInfo.setCommentInfoByParentId(parent);
            commentInfo.setCommentInfoByReplyId(reply);
        }

        String userId = currentUserHolder.getUserId();
        commentInfo.setUserId(userId);

        UserDTO userDto = userClient.findById(userId, "1");
        commentInfo.setUserName(userDto.getDisplayName());
        commentInfo.setUserAvatar(avatarPrefix + "/" + userDto.getUsername());
        commentInfoManager.save(commentInfo);

        return new BaseDTO();
    }

    public List<Map<String, Object>> convertComments(
            List<CommentInfo> commentInfos) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (CommentInfo commentInfo : commentInfos) {
            list.add(convertComment(commentInfo));
        }

        return list;
    }

    public Map<String, Object> convertComment(CommentInfo commentInfo) {
        CommentInfo reply = commentInfo.getCommentInfoByReplyId();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", commentInfo.getId());
        map.put("content", commentInfo.getContent());
        map.put("userId", commentInfo.getUserId());
        map.put("userName", commentInfo.getUserName());
        map.put("userAvatar", commentInfo.getUserAvatar());
        map.put("createTime", commentInfo.getCreateTime());

        if (reply != null) {
            map.put("replyUserId", reply.getUserId());
            map.put("replyUserName", reply.getUserName());
        }

        String hql = "from CommentInfo where commentInfoByParentId.id=? order by id asc";
        List<CommentInfo> children = commentInfoManager.find(hql,
                commentInfo.getId());

        if (!children.isEmpty()) {
            List<Map<String, Object>> list = this.convertComments(children);
            map.put("children", list);
        }

        return map;
    }

    @Resource
    public void setCommentInfo(CommentInfoManager commentInfoManager) {
        this.commentInfoManager = commentInfoManager;
    }

    @Resource
    public void setCommentThreadManager(
            CommentThreadManager commentThreadManager) {
        this.commentThreadManager = commentThreadManager;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setCmsService(CmsService cmsService) {
        this.cmsService = cmsService;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }

    @Value("${avatar.prefix}")
    public void setAvatarPrefix(String avatarPrefix) {
        this.avatarPrefix = avatarPrefix;
    }
}
