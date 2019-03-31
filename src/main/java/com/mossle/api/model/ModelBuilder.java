package com.mossle.api.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mossle.api.keyvalue.FormParameter;

import com.mossle.client.store.StoreClient;

import com.mossle.core.MultipartHandler;
import com.mossle.core.store.MultipartFileDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.MultiValueMap;

import org.springframework.web.multipart.MultipartFile;

/**
 * 构建model.
 */
public class ModelBuilder {
    private static Logger logger = LoggerFactory.getLogger(ModelBuilder.class);

    public ModelInfoDTO build(ModelInfoDTO modelInfo,
            MultipartHandler multipartHandler, StoreClient storeClient,
            String tenantId) throws Exception {
        for (Map.Entry<String, List<String>> entry : multipartHandler
                .getMultiValueMap().entrySet()) {
            String key = entry.getKey();

            if (key == null) {
                continue;
            }

            List<String> value = entry.getValue();

            if ((value == null) || (value.isEmpty())) {
                continue;
            }

            String theValue = this.getValue(value);
            this.updateItem(modelInfo, key, theValue);
        }

        if (multipartHandler.getMultiFileMap() == null) {
            return modelInfo;
        }

        for (Map.Entry<String, List<MultipartFile>> entry : multipartHandler
                .getMultiFileMap().entrySet()) {
            String key = entry.getKey();

            if (key == null) {
                continue;
            }

            List<MultipartFile> value = entry.getValue();

            if ((value == null) || (value.isEmpty())) {
                continue;
            }

            MultipartFile multipartFile = value.get(0);

            if ((multipartFile.getName() == null)
                    || "".equals(multipartFile.getName().trim())) {
                continue;
            }

            if (multipartFile.getSize() == 0) {
                logger.info("ignore empty file");

                continue;
            }

            String theValue = storeClient.saveStore("form",
                    new MultipartFileDataSource(multipartFile), tenantId)
                    .getKey();
            this.updateItem(modelInfo, key, theValue);
        }

        return modelInfo;
    }

    public void updateItem(ModelInfoDTO info, String key, String value) {
        ModelItemDTO item = new ModelItemDTO();
        item.setCode(key);
        item.setType("text");
        item.setValue(value);
        info.addItem(item);
    }

    /**
     * 主要是获得多值属性，比如checkbox.
     */
    public String getValue(List<String> values) {
        if ((values == null) || (values.isEmpty())) {
            return "";
        }

        if (values.size() == 1) {
            return values.get(0);
        }

        StringBuilder buff = new StringBuilder();

        for (String value : values) {
            buff.append(value).append(",");
        }

        buff.deleteCharAt(buff.length() - 1);

        return buff.toString();
    }

    /**
     * 创建一个新model.
     */
    public ModelInfoDTO build(String bpmProcessId, String status,
            FormParameter formParameter, String userId, String tenantId) {
        String category = bpmProcessId;

        ModelInfoDTO modelInfoDto = new ModelInfoDTO();
        modelInfoDto.setCategory(category);
        // modelInfoDto.setUserId(userId);
        modelInfoDto.setCreateTime(new Date());

        // modelInfoDto.setTenantId(tenantId);
        return build(modelInfoDto, status, formParameter);
    }

    /**
     * 把status和parameters更新到model里.
     */
    public ModelInfoDTO build(ModelInfoDTO modelInfoDto, String status,
            FormParameter formParameter) {
        modelInfoDto.setStatus(status);

        for (Map.Entry<String, List<String>> entry : formParameter
                .getMultiValueMap().entrySet()) {
            String key = entry.getKey();
            List<String> value = entry.getValue();

            if ((value == null) || (value.isEmpty())) {
                continue;
            }

            String theValue = this.getValue(value);
            this.updateItem(modelInfoDto, key, theValue);
        }

        return modelInfoDto;
    }

    public ModelInfoDTO build(ModelInfoDTO modelInfoDto,
            MultiValueMap<String, String> multiValueMap, String tenantId)
            throws Exception {
        for (Map.Entry<String, List<String>> entry : multiValueMap.entrySet()) {
            String key = entry.getKey();

            if (key == null) {
                continue;
            }

            List<String> value = entry.getValue();

            if ((value == null) || (value.isEmpty())) {
                continue;
            }

            String theValue = this.getValue(value);
            this.updateItem(modelInfoDto, key, theValue);
        }

        return modelInfoDto;
    }
}
