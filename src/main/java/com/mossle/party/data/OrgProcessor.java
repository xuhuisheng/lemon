package com.mossle.party.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrgProcessor {
    private OrgDTO root = new OrgDTO();
    private Map<String, OrgDTO> orgMap = new HashMap<String, OrgDTO>();

    public void init(String orgDataFilePath, String orgDataEncoding)
            throws Exception {
        Map<String, Object> map = new JsonParser().parseMap(orgDataFilePath,
                orgDataEncoding);
        this.processOrgDto(map, root);
    }

    public void processOrgDto(Map<String, Object> map, OrgDTO orgDto) {
        String code = (String) map.get("code");
        String name = (String) map.get("name");
        String type = (String) map.get("type");
        String leader = (String) map.get("leader");

        if (type == null) {
            type = "department";
        }

        orgDto.setCode(code);
        orgDto.setName(name);
        orgDto.setType(type);
        orgDto.setLeader(leader);

        orgMap.put(code, orgDto);

        List<Map<String, Object>> children = (List<Map<String, Object>>) map
                .get("children");

        if (children == null) {
            return;
        }

        for (Map<String, Object> item : children) {
            OrgDTO child = new OrgDTO();
            orgDto.getChildren().add(child);
            this.processOrgDto(item, child);
            child.setParentCode(code);
        }
    }

    public Collection<OrgDTO> getOrgDtos() {
        return orgMap.values();
    }

    public OrgDTO getRoot() {
        return root;
    }

    public OrgDTO findByCode(String code) {
        return orgMap.get(code);
    }
}
