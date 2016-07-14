package com.mossle.api.keyvalue;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mossle.api.store.StoreConnector;

import com.mossle.core.MultipartHandler;
import com.mossle.core.store.MultipartFileDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.MultiValueMap;

import org.springframework.web.multipart.MultipartFile;

/**
 * 构建Record.
 */
public class RecordBuilder {
    private static Logger logger = LoggerFactory.getLogger(RecordBuilder.class);

    /**
     * 把status和parameters更新到record里.
     */
    public Record build(Record record, int status, FormParameter formParameter) {
        record.setStatus(status);

        for (Map.Entry<String, List<String>> entry : formParameter
                .getMultiValueMap().entrySet()) {
            String key = entry.getKey();
            List<String> value = entry.getValue();

            if ((value == null) || (value.isEmpty())) {
                continue;
            }

            Prop prop = new Prop();
            prop.setCode(key);
            prop.setType(0);
            prop.setValue(this.getValue(value));
            record.getProps().put(prop.getCode(), prop);
        }

        return record;
    }

    /**
     * 创建一个新record
     */
    public Record build(String category, int status,
            FormParameter formParameter, String userId, String tenantId) {
        Record record = new Record();
        record.setCategory(category);
        record.setUserId(userId);
        record.setCreateTime(new Date());
        record.setTenantId(tenantId);

        return build(record, status, formParameter);
    }

    /**
     * 更新record的ref属性.
     */
    public Record build(Record record, int status, String ref) {
        if (record == null) {
            record = new Record();
        }

        record.setRef(ref);
        record.setStatus(status);

        return record;
    }

    public Record build(Record record, MultipartHandler multipartHandler,
            StoreConnector storeConnector, String tenantId) throws Exception {
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

            Prop prop = new Prop();
            prop.setCode(key);
            prop.setType(0);
            prop.setValue(this.getValue(value));
            record.getProps().put(prop.getCode(), prop);
        }

        if (multipartHandler.getMultiFileMap() == null) {
            return record;
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

            Prop prop = new Prop();
            prop.setCode(key);
            prop.setType(0);
            prop.setValue(storeConnector.saveStore("form",
                    new MultipartFileDataSource(multipartFile), tenantId)
                    .getKey());
            record.getProps().put(prop.getCode(), prop);
        }

        return record;
    }

    public Record build(Record record,
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

            Prop prop = new Prop();
            prop.setCode(key);
            prop.setType(0);
            prop.setValue(this.getValue(value));
            record.getProps().put(prop.getCode(), prop);
        }

        return record;
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
}
