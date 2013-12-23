package com.mossle.form.keyvalue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DatabaseKeyValue implements KeyValue {
    private static Logger logger = LoggerFactory
            .getLogger(DatabaseKeyValue.class);
    private JdbcTemplate jdbcTemplate;

    public Record findByCode(String code) {
        if (isBlank(code)) {
            return null;
        }

        Record record = null;

        try {
            Map<String, Object> map = jdbcTemplate.queryForMap(
                    "select * from KV_RECORD where id=?", code);
            record = convertRecord(map);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return record;
    }

    public Record findByRef(String ref) {
        if (isBlank(ref)) {
            return null;
        }

        Record record = null;

        try {
            Map<String, Object> map = jdbcTemplate.queryForMap(
                    "select * from KV_RECORD where ref=?", ref);
            record = convertRecord(map);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return record;
    }

    public void save(Record record) {
        if (record.getCode() == null) {
            insert(record);
        } else {
            update(record);
        }
    }

    public void removeByCode(String code) {
        jdbcTemplate.update("delete from KV_RECORD where id=", code);
    }

    public List<Record> findByStatus(int status) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select * from KV_RECORD where status=?", status);
        List<Record> records = new ArrayList<Record>();

        for (Map<String, Object> map : list) {
            Record record = convertRecord(map);
            records.add(record);
        }

        return records;
    }

    // ~ ======================================================================
    public Record convertRecord(Map<String, Object> recordMap) {
        Record record = new Record();
        record.setCode(getStringValue(recordMap, "id"));
        record.setCategory(getStringValue(recordMap, "category"));
        record.setStatus(getIntValue(recordMap, "status"));
        record.setRef(getStringValue(recordMap, "ref"));

        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select * from KV_PROP where record_id=?", record.getCode());

        for (Map<String, Object> propMap : list) {
            Prop prop = new Prop();
            prop.setCode(getStringValue(propMap, "code"));
            prop.setType(getIntValue(propMap, "type"));
            prop.setValue(getStringValue(propMap, "value"));
            record.getProps().put(prop.getCode(), prop);
        }

        return record;
    }

    public String getStringValue(Map<String, Object> map, String name) {
        Object value = map.get(name);

        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            return (String) value;
        }

        return value.toString();
    }

    public Integer getIntValue(Map<String, Object> map, String name) {
        Object value = map.get(name);

        if (value == null) {
            return null;
        }

        if (value instanceof Integer) {
            return (Integer) value;
        }

        return Integer.parseInt(value.toString());
    }

    public void insert(Record record) {
        String sqlRecordInsert = "insert into KV_RECORD(category,status,ref) values(?,?,?)";
        String originalRef = record.getRef();
        String ref = originalRef;

        if (originalRef == null) {
            ref = UUID.randomUUID().toString();
        }

        jdbcTemplate.update(sqlRecordInsert, record.getCategory(),
                record.getStatus(), ref);

        Record resultRecord = findByRef(ref);
        String code = resultRecord.getCode();

        if (originalRef == null) {
            String sqlRecordUpdate = "update KV_RECORD set ref=null where id=?";
            jdbcTemplate.update(sqlRecordUpdate, code);
        }

        record.setCode(resultRecord.getCode());

        String sqlProp = "insert into KV_PROP(code,type,value,record_id) values(?,?,?,?)";

        for (Prop prop : record.getProps().values()) {
            jdbcTemplate.update(sqlProp, prop.getCode(), prop.getType(),
                    prop.getValue(), record.getCode());
        }
    }

    public void update(Record record) {
        String sqlRecord = "update KV_RECORD set category=?,status=?,ref=? where id=?";
        jdbcTemplate.update(sqlRecord, record.getCategory(),
                record.getStatus(), record.getRef(), record.getCode());

        Record resultRecord = findByCode(record.getCode());
        String sqlPropInsert = "insert into KV_PROP(code,type,value,record_id) values(?,?,?,?)";
        String sqlPropUpdate = "update KV_PROP set type=?,value=? where code=? and record_id=?";

        for (Prop prop : record.getProps().values()) {
            // only append, won't delete
            if (resultRecord.getProps().containsKey(prop.getCode())) {
                jdbcTemplate.update(sqlPropUpdate, prop.getType(),
                        prop.getValue(), prop.getCode(), record.getCode());
            } else {
                jdbcTemplate.update(sqlPropInsert, prop.getCode(),
                        prop.getType(), prop.getValue(), record.getCode());
            }
        }
    }

    public boolean isBlank(String text) {
        return (text == null) || "".equals(text);
    }

    // ~ ======================================================================
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
