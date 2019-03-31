package com.mossle.operation.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mossle.api.model.ModelInfoDTO;
import com.mossle.api.model.ModelItemDTO;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class ModelBuilder {
    private MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<String, String>();
    private Map<String, String> rootMap = new HashMap<String, String>();
    private Map<String, List<String>> itemMap = new HashMap<String, List<String>>();
    private ModelInfoDTO modelInfoDto = new ModelInfoDTO();

    public void addItem(String key, String value) {
        multiValueMap.add(key, value);
    }

    public void visitMultiValueMap() {
        for (Map.Entry<String, List<String>> entry : multiValueMap.entrySet()) {
            String key = entry.getKey();

            if (key.indexOf(".") != -1) {
                this.appendGroupMap(key, entry.getValue());
            } else {
                this.appendRootMap(key, entry.getValue());
            }
        }
    }

    public void appendRootMap(String key, List<String> value) {
        rootMap.put(key, value.get(0));
    }

    public void appendGroupMap(String key, List<String> value) {
        int index = key.indexOf(".");
        String groupKey = key.substring(0, index);
        String itemKey = key.substring(index + 1);
        itemMap.put(itemKey, value);
    }

    public void buildItems() {
        if (itemMap.isEmpty()) {
            return;
        }

        Set<String> itemKeys = itemMap.keySet();
        int size = itemMap.get(itemKeys.iterator().next()).size();

        for (int i = 0; i < size; i++) {
            ModelInfoDTO child = new ModelInfoDTO();
            child.setName("sub" + i);
            modelInfoDto.getInfos().add(child);

            for (String itemKey : itemKeys) {
                String itemValue = itemMap.get(itemKey).get(i);
                ModelItemDTO modelItemDto = new ModelItemDTO();
                modelItemDto.setCode(itemKey);
                modelItemDto.setValue(itemValue);
                child.addItem(modelItemDto);
            }
        }
    }

    public ModelInfoDTO build() {
        this.visitMultiValueMap();

        for (Map.Entry<String, String> entry : rootMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            ModelItemDTO modelItemDto = new ModelItemDTO();
            modelItemDto.setCode(key);
            modelItemDto.setValue(value);
            modelInfoDto.addItem(modelItemDto);
        }

        this.buildItems();

        return modelInfoDto;
    }
}
