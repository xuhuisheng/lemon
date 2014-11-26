package com.mossle.form.keyvalue;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mossle.form.support.FormParameter;

/**
 * 构建Record.
 */
public class RecordBuilder {
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
            FormParameter formParameter, String userId) {
        Record record = new Record();
        record.setCategory(category);
        record.setUserId(userId);
        record.setCreateTime(new Date());

        return build(record, status, formParameter);
    }

    /**
     * 更新record的ref属性.
     */
    public Record build(Record record, int status, String ref) {
        record.setRef(ref);
        record.setStatus(status);

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
