package com.mossle.disk.support;

import java.util.ArrayList;
import java.util.List;

public class TreeViewNode {
    private String id;
    private String text;
    private String href;
    private String type;
    private String catalog;
    private List<TreeViewNode> nodes = new ArrayList<TreeViewNode>();
    private boolean children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public List<TreeViewNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<TreeViewNode> nodes) {
        this.nodes = nodes;
    }

    // ~
    public String getName() {
        return this.getText();
    }

    public boolean isChildren() {
        return children;
    }

    public void setChildren(boolean children) {
        this.children = children;
    }
}
