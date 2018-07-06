package com.mossle.party.support;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class ManagerInfoComparator implements Comparator<ManagerInfo> {
    public int compare(ManagerInfo o1, ManagerInfo o2) {
        return o1.getPriority() - o2.getPriority();
    }

    public boolean equals(Object obj) {
        return this == obj;
    }
}
