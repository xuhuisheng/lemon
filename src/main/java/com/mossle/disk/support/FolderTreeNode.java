package com.mossle.disk.support;

import java.util.ArrayList;
import java.util.List;

public class FolderTreeNode {
    private String id;
    private String name;
    private boolean open;
    private List<FolderTreeNode> children = new ArrayList<FolderTreeNode>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public List<FolderTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<FolderTreeNode> children) {
        this.children = children;
    }
}
