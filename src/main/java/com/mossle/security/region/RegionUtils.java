package com.mossle.security.region;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mossle.security.util.SpringSecurityUtils;

public class RegionUtils {
    protected RegionUtils() {
    }

    public static Map<String, List<Long>> getRegionMap() {
        Map<String, List<Long>> map = new HashMap<String, List<Long>>();

        for (String authority : SpringSecurityUtils.getAuthorities()) {
            String[] array = authority.split(":");
            String text;

            if (array.length == 1) {
                text = RegionConstants.SYSTEM_REGION;
            } else {
                text = array[0];
            }

            RegionKey regionKey = new RegionKey(text);
            String type = regionKey.getType();
            Long id = regionKey.getLong();

            if (map.containsKey(type)) {
                map.get(type).add(id);
            } else {
                List<Long> list = new ArrayList<Long>();
                list.add(id);
                map.put(type, list);
            }
        }

        return map;
    }
}
