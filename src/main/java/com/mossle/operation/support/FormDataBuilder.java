package com.mossle.operation.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mossle.api.model.ModelInfoDTO;
import com.mossle.api.model.ModelItemDTO;

public class FormDataBuilder {
    private ModelInfoDTO modelInfoDto;

    public FormDataBuilder setModelInfoDto(ModelInfoDTO modelInfoDto) {
        this.modelInfoDto = modelInfoDto;

        return this;
    }

    public FormData build() {
        FormData formData = new FormData();

        for (ModelItemDTO modelItemDto : modelInfoDto.getItems()) {
            formData.getValues().put(modelItemDto.getCode(),
                    modelItemDto.getValue());
        }

        for (ModelInfoDTO row : modelInfoDto.getInfos()) {
            Map<String, String> map = new HashMap<String, String>();
            formData.getRows().add(map);

            for (ModelItemDTO modelItemDto : row.getItems()) {
                map.put(modelItemDto.getCode(), modelItemDto.getValue());
            }

            map.put("_rowId", row.getCode());
        }

        return formData;
    }
}
