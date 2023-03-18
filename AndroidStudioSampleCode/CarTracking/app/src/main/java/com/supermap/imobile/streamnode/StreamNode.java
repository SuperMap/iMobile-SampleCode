package com.supermap.imobile.streamnode;

import java.util.ArrayList;
import java.util.List;

/**
 * 运行流节点基类
 */
public class StreamNode {

    protected String className = "";
    protected String caption = "";
    protected String name = "";
    protected String description = "";
    protected List<String> nextNodes = new ArrayList<>();
    protected List<String> prevNodes = new ArrayList<>();

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getNextNodes() {
        return nextNodes;
    }

    public void setNextNodes(List<String> nextNodes) {
        this.nextNodes = nextNodes;
    }

    public List<String> getPrevNodes() {
        return prevNodes;
    }

    public void setPrevNodes(List<String> prevNodes) {
        this.prevNodes = prevNodes;
    }

}
