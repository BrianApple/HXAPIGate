package com.usthe.bootshiro.domain.vo;

import java.util.ArrayList;
import java.util.List;

/**
 *    tree 抽象节点 树可继承这个节点
 * @author tomsun28
 * @date 16:47 2018/3/20
 */
public abstract class BaseTreeNode {

    protected Integer id;
    protected Integer parentId;
    protected List<BaseTreeNode> children = new ArrayList<>();

    public void addChilren(BaseTreeNode node) {
        this.children.add(node);
    }

    public List<BaseTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<BaseTreeNode> children) {
        this.children = children;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
}
