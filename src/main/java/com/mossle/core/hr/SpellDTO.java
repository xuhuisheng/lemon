package com.mossle.core.hr;

import java.util.List;

public class SpellDTO {
    private char name;
    private List<String> list;

    public SpellDTO(char name) {
        this.name = name;
    }

    public char getName() {
        return name;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public List<String> getList() {
        return list;
    }

    public String getFirst() {
        return this.list.get(0);
    }
}
