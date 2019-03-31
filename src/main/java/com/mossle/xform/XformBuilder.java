package com.mossle.xform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mossle.api.keyvalue.Prop;
import com.mossle.api.keyvalue.Record;
import com.mossle.api.model.ModelInfoDTO;
import com.mossle.api.model.ModelItemDTO;
import com.mossle.api.store.StoreDTO;
import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;

import com.mossle.client.store.StoreClient;

import com.mossle.core.mapper.JsonMapper;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XformBuilder {
    private static Logger logger = LoggerFactory.getLogger(XformBuilder.class);
    private Xform xform = new Xform();
    private JsonMapper jsonMapper = new JsonMapper();
    private StoreClient storeClient;
    private UserConnector userConnector;

    public XformBuilder setStoreClient(StoreClient storeClient) {
        this.storeClient = storeClient;

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

    public XformBuilder setModelInfoDto(ModelInfoDTO modelInfoDto)
            throws Exception {
        if (modelInfoDto == null) {
            logger.info("modelInfoDto is null");

            return this;
        }

        for (ModelItemDTO modelItemDto : modelInfoDto.getItems()) {
            String name = modelItemDto.getCode();
            String value = modelItemDto.getValue();
            XformField xformField = xform.findXformField(name);

            if (xformField == null) {
                continue;
            }

            String type = xformField.getType();

            if ("fileupload".equals(type)) {
                processFileUpload(xformField, value);
            } else if ("userpicker".equals(type)) {
                processUserPicker(xformField, value);
            } else {
                xformField.setValue(value);
            }
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
                processFileUpload(xformField, value);
            } else if ("userpicker".equals(type)) {
                processUserPicker(xformField, value);
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

    public void processFileUpload(XformField xformField, String value)
            throws Exception {
        StoreDTO storeDto = storeClient.getStore("form", value, "1");
        xformField.setValue(storeDto.getKey());
        xformField.setContentType(storeDto.getDataSource().getContentType());
        xformField.setLabel(storeDto.getDisplayName());
    }

    public void processUserPicker(XformField xformField, String value) {
        xformField.setValue(value);

        StringBuilder buff = new StringBuilder();

        for (String userId : value.split(",")) {
            if (StringUtils.isBlank(userId)) {
                continue;
            }

            UserDTO userDto = userConnector.findById(userId);

            if (userDto == null) {
                continue;
            }

            buff.append(userDto.getDisplayName()).append(",");
        }

        if (buff.length() > 0) {
            buff.deleteCharAt(buff.length() - 1);
        }

        xformField.setLabel(buff.toString());
    }
}
