package com.mossle.form.keyvalue;

import java.util.Collections;
import java.util.Map;

public class RecordBuilder {
    public Record build(Record record, int status,
            Map<String, String[]> parameters) {
        record.setStatus(status);

        for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();

            if ((value == null) || (value.length == 0)) {
                continue;
            }

            Prop prop = new Prop();
            prop.setCode(key);
            prop.setType(0);
            prop.setValue(value[0]);
            record.getProps().put(prop.getCode(), prop);
        }

        return record;
    }

    public Record build(String category, int status,
            Map<String, String[]> parameters) {
        Record record = new Record();
        record.setCategory(category);

        return build(record, status, parameters);
    }

    public Record build(Record record, int status, String ref) {
        record.setRef(ref);

        return build(record, status, Collections.EMPTY_MAP);
    }
}
