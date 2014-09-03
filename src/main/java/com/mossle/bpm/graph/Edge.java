package com.mossle.bpm.graph;

/**
 * 连线.
 */
public class Edge extends GraphElement {
    /**
     * 起点.
     */
    private Node src;

    /**
     * 终点.
     */
    private Node dest;

    /**
     * 循环.
     */
    private boolean cycle;

    public Node getSrc() {
        return src;
    }

    public void setSrc(Node src) {
        this.src = src;
    }

    public Node getDest() {
        return dest;
    }

    public void setDest(Node dest) {
        this.dest = dest;
    }

    public boolean isCycle() {
        return cycle;
    }

    public void setCycle(boolean cycle) {
        this.cycle = cycle;
    }
}
