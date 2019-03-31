package com.mossle.core.util;

import java.util.ArrayList;
import java.util.List;

public class Select2Info {
    private List<Select2Item> results = new ArrayList<Select2Item>();

    public List<Select2Item> getResults() {
        return results;
    }

    public void setResults(List<Select2Item> results) {
        this.results = results;
    }

    public void addItem(String id, String text) {
        this.results.add(new Select2Item(id, text));
    }
}
