package com.mossle.disk.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TreeViewNodeBuilder {
    public List<TreeViewNode> buildChildren() {
        return Collections.emptyList();
    }

    public List<TreeViewNode> buildRoot(FolderTreeNode userSpaceFolder,
            boolean hideTrash) {
        List<TreeViewNode> list = new ArrayList<TreeViewNode>();
        list.add(this.recent());
        list.add(this.createUserSpace(userSpaceFolder));
        list.add(this.createShareSpace());

        if (!hideTrash) {
            list.add(this.createTrash());
        }

        return list;
    }

    public List<TreeViewNode> buildSpaces(List<FolderTreeNode> spaces) {
        return this.convertChildren(spaces, "space");
    }

    public List<TreeViewNode> buildFolders(List<FolderTreeNode> folders) {
        return this.convertChildren(folders, "folder");
    }

    // ~
    public TreeViewNode recent() {
        TreeViewNode root = new TreeViewNode();
        root.setText("最近访问");
        root.setType("home");
        root.setChildren(false);
        root.setId("home");

        return root;
    }

    public TreeViewNode createUserSpace(FolderTreeNode rootFolder) {
        TreeViewNode root = new TreeViewNode();
        root.setText("我的空间");
        root.setType("user");
        root.setChildren(true);
        root.setId(rootFolder.getId());

        return root;
    }

    public TreeViewNode createShareSpace() {
        TreeViewNode root = new TreeViewNode();
        root.setText("共享空间");
        root.setType("group");
        root.setChildren(true);
        root.setId("group");

        return root;
    }

    public TreeViewNode createTrash() {
        TreeViewNode root = new TreeViewNode();
        root.setText("回收站");
        root.setType("trash");
        root.setChildren(false);
        root.setId("trash");

        return root;
    }

    // ~
    public List<TreeViewNode> convertChildren(List<FolderTreeNode> children,
            String type) {
        List<TreeViewNode> treeViewNodes = new ArrayList<TreeViewNode>();

        for (FolderTreeNode child : children) {
            treeViewNodes.add(this.convertChild(child, type));
        }

        return treeViewNodes;
    }

    public TreeViewNode convertChild(FolderTreeNode child, String type) {
        TreeViewNode treeViewNode = new TreeViewNode();
        treeViewNode.setId(child.getId());
        treeViewNode.setText(child.getName());
        treeViewNode.setType(type);
        // treeViewNode.setNodes(this.convertChildren(child.getChildren()));
        treeViewNode.setChildren(true);

        return treeViewNode;
    }
}
