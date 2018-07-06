package com.mossle.party.support;

import java.util.Comparator;

import com.mossle.party.persistence.domain.PartyStruct;

public class PartyStructComparator implements Comparator<PartyStruct> {
    public int compare(PartyStruct o1, PartyStruct o2) {
        Integer priority1 = o1.getPriority();
        Integer priority2 = o2.getPriority();

        if ((priority1 == null) && (priority2 == null)) {
            return 0;
        } else if (priority1 == null) {
            return -1;
        } else if (priority2 == null) {
            return 1;
        }

        return priority1 - priority2;
    }

    public boolean equals(Object obj) {
        return this == obj;
    }
}
