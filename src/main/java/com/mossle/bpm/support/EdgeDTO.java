package com.mossle.bpm.support;

import java.util.ArrayList;
import java.util.List;

public class EdgeDTO {
    private String id;
    private List<List<Integer>> g = new ArrayList<List<Integer>>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<List<Integer>> getG() {
        return g;
    }

    public void setG(List<List<Integer>> g) {
        this.g = g;
    }
}
