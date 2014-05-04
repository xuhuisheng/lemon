package com.mossle.bpm.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.delegate.VariableScope;

public class MapVariableScope implements VariableScope {
    private Map<String, Object> map = new HashMap<String, Object>();

    public Map<String, Object> getVariables() {
        return map;
    }

    public Map<String, Object> getVariablesLocal() {
        return map;
    }

    public Object getVariable(String variableName) {
        return map.get(variableName);
    }

    public Object getVariableLocal(String variableName) {
        return map.get(variableName);
    }

    public Set<String> getVariableNames() {
        return map.keySet();
    }

    public Set<String> getVariableNamesLocal() {
        return map.keySet();
    }

    public void setVariable(String variableName, Object value) {
        map.put(variableName, value);
    }

    public Object setVariableLocal(String variableName, Object value) {
        map.put(variableName, value);

        return value;
    }

    public void setVariables(Map<String, ? extends Object> variables) {
        map.putAll(variables);
    }

    public void setVariablesLocal(Map<String, ? extends Object> variables) {
        map.putAll(variables);
    }

    public boolean hasVariables() {
        return !map.isEmpty();
    }

    public boolean hasVariablesLocal() {
        return !map.isEmpty();
    }

    public boolean hasVariable(String variableName) {
        return map.containsKey(variableName);
    }

    public boolean hasVariableLocal(String variableName) {
        return map.containsKey(variableName);
    }

    public void createVariableLocal(String variableName, Object value) {
        map.put(variableName, value);
    }

    public void removeVariable(String variableName) {
        map.remove(variableName);
    }

    public void removeVariableLocal(String variableName) {
        map.remove(variableName);
    }

    public void removeVariables(Collection<String> variableNames) {
        for (String variableName : variableNames) {
            map.remove(variableName);
        }
    }

    public void removeVariablesLocal(Collection<String> variableNames) {
        for (String variableName : variableNames) {
            map.remove(variableName);
        }
    }

    public void removeVariables() {
        map.clear();
    }

    public void removeVariablesLocal() {
        map.clear();
    }
}
