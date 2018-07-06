package com.mossle.workcal.data;

import java.util.ArrayList;
import java.util.List;

public class WorkcalDTO {
    private TypeDTO type = new TypeDTO();
    private List<RuleDTO> rules = new ArrayList<RuleDTO>();
    private List<YearDTO> years = new ArrayList<YearDTO>();

    public TypeDTO getType() {
        return type;
    }

    public void setType(TypeDTO type) {
        this.type = type;
    }

    public List<RuleDTO> getRules() {
        return rules;
    }

    public void setRules(List<RuleDTO> rules) {
        this.rules = rules;
    }

    public List<YearDTO> getYears() {
        return years;
    }

    public void setYears(List<YearDTO> years) {
        this.years = years;
    }
}
