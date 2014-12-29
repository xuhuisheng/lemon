package com.mossle.form.xform;

import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mossle.api.internal.StoreConnector;
import com.mossle.api.internal.StoreDTO;

import com.mossle.core.mapper.JsonMapper;

import com.mossle.form.keyvalue.Prop;
import com.mossle.form.keyvalue.Record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XformBuilder {
    private static Logger logger = LoggerFactory.getLogger(XformBuilder.class);
    private Xform xform = new Xform();
    private JsonMapper jsonMapper = new JsonMapper();
    private StoreConnector storeConnector;

    public XformBuilder setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;

        return this;
    }

    public XformBuilder setContent(String content) {
        xform.setContent(content);
        logger.debug("content : {}", content);

        try {
            this.handleStructure();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return this;
    }

    public XformBuilder setRecord(Record record) throws Exception {
        for (Prop prop : record.getProps().values()) {
            String name = prop.getCode();
            String value = prop.getValue();
            XformField xformField = xform.findXformField(name);

            if (xformField == null) {
                continue;
            }

            String type = xformField.getType();

            if ("fileupload".equals(type)) {
                StoreDTO storeDto = storeConnector.getStore("form", value);
                xformField.setValue(storeDto.getKey());
                xformField.setContentType(storeDto.getDataSource()
                        .getContentType());
                xformField.setLabel(storeDto.getDisplayName());
            } else {
                xformField.setValue(value);
            }
        }

        return this;
    }

    public Xform build() {
        return xform;
    }

    public void handleStructure() throws Exception {
        Map map = jsonMapper.fromJson(xform.getContent(), Map.class);
        logger.debug("map : {}", map);

        List<Map> sections = (List<Map>) map.get("sections");
        logger.debug("sections : {}", sections);

        Map<String, String> formTypeMap = new HashMap<String, String>();

        for (Map section : sections) {
            if (!"grid".equals(section.get("type"))) {
                continue;
            }

            List<Map> fields = (List<Map>) section.get("fields");

            for (Map field : fields) {
                this.handleField(field);
            }
        }
    }

    public void handleField(Map map) {
        XformField xformField = new XformField();
        xformField.setName((String) map.get("name"));
        xformField.setType((String) map.get("type"));
        xform.addXformField(xformField);
    }
}
