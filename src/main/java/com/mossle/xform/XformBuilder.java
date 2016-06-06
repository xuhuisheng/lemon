package com.mossle.xform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mossle.api.keyvalue.Prop;
import com.mossle.api.keyvalue.Record;
import com.mossle.api.store.StoreConnector;
import com.mossle.api.store.StoreDTO;
import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;

import com.mossle.core.mapper.JsonMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XformBuilder {
    private static Logger logger = LoggerFactory.getLogger(XformBuilder.class);
    private Xform xform = new Xform();
    private JsonMapper jsonMapper = new JsonMapper();
    private StoreConnector storeConnector;
    private UserConnector userConnector;

    public XformBuilder setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;

        return this;
    }

    public XformBuilder setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;

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
        if (record == null) {
            logger.info("record is null");

            return this;
        }

        for (Prop prop : record.getProps().values()) {
            String name = prop.getCode();
            String value = prop.getValue();
            XformField xformField = xform.findXformField(name);

            if (xformField == null) {
                continue;
            }

            String type = xformField.getType();

            if ("fileupload".equals(type)) {
                StoreDTO storeDto = storeConnector.getStore("form", value,
                        record.getTenantId());
                xformField.setValue(storeDto.getKey());
                xformField.setContentType(storeDto.getDataSource()
                        .getContentType());
                xformField.setLabel(storeDto.getDisplayName());
            } else if ("userpicker".equals(type)) {
                xformField.setValue(value);

                StringBuilder buff = new StringBuilder();

                for (String userId : value.split(",")) {
                    UserDTO userDto = userConnector.findById(userId);
                    buff.append(userDto.getDisplayName()).append(",");
                }

                if (buff.length() > 0) {
                    buff.deleteCharAt(buff.length() - 1);
                }

                xformField.setLabel(buff.toString());
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
        if (xform.getContent() == null) {
            logger.info("cannot find xform content");

            return;
        }

        Map map = jsonMapper.fromJson(xform.getContent(), Map.class);
        logger.debug("map : {}", map);

        if (map == null) {
            logger.info("cannot find map");

            return;
        }

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
