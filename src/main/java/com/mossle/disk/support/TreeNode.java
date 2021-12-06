package com.mossle.disk.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeNode {
    private String id;
    private String name;
    private String type;
    private Map<String, TreeNode> childrenMap = new HashMap<String, TreeNode>();

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, TreeNode> getChildrenMap() {
        return childrenMap;
    }

    public void setChildrenMap(Map<String, TreeNode> childrenMap) {
        this.childrenMap = childrenMap;
    }

    // ~
    public boolean isOpen() {
        return !childrenMap.isEmpty();
    }

    public String getIconSkin() {
        return "";
    }

    public List<TreeNode> getChildren() {
        return new ArrayList<TreeNode>(childrenMap.values());
    }

    public void setChildren(List<TreeNode> children) {
        for (TreeNode child : children) {
            childrenMap.put(child.getName(), child);
        }
    }

    public TreeNode createOrFindChild(String name) {
        TreeNode child = childrenMap.get(name);

        if (child == null) {
            child = new TreeNode();
            child.setName(name);
            child.setType("folder");
            childrenMap.put(name, child);
        }

        return child;
    }
}
