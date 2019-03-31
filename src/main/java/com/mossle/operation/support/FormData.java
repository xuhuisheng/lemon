package com.mossle.operation.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormData {
    private Map<String, String> values = new HashMap<String, String>();
    private List<Map<String, String>> rows = new ArrayList<Map<String, String>>();

    public Map<String, String> getValues() {
        return values;
    }

    public List<Map<String, String>> getRows() {
        return rows;
    }
}
