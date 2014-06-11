package com.mossle.form.keyvalue;

import java.util.List;

public interface KeyValue {
    Record findByCode(String code);

    Record findByRef(String ref);

    void save(Record record);

    void removeByCode(String code);

    List<Record> findByStatus(int status, String userId);
}
