package com.mossle.cms.support;

import java.util.List;

import com.mossle.cms.persistence.domain.CmsComment;

public class CommentDTO {
    private CmsComment cmsComment;
    private List<CmsComment> children;

    public CmsComment getCmsComment() {
        return cmsComment;
    }

    public void setCmsComment(CmsComment cmsComment) {
        this.cmsComment = cmsComment;
    }

    public List<CmsComment> getChildren() {
        return children;
    }

    public void setChildren(List<CmsComment> children) {
        this.children = children;
    }
}
