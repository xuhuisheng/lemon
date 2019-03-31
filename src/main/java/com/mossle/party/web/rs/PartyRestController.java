package com.mossle.party.web.rs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserDTO;

import com.mossle.client.user.UserClient;

import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.party.persistence.domain.PartyEntity;
import com.mossle.party.persistence.domain.PartyStruct;
import com.mossle.party.persistence.domain.PartyStructType;
import com.mossle.party.persistence.domain.PartyType;
import com.mossle.party.persistence.manager.PartyEntityManager;
import com.mossle.party.persistence.manager.PartyStructManager;
import com.mossle.party.persistence.manager.PartyStructTypeManager;
import com.mossle.party.persistence.manager.PartyTypeManager;
import com.mossle.party.service.PartyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("party/rs")
public class PartyRestController {
    private static Logger logger = LoggerFactory
            .getLogger(PartyRestController.class);
    private PartyTypeManager partyTypeManager;
    private PartyStructManager partyStructManager;
    private PartyEntityManager partyEntityManager;
    private PartyStructTypeManager partyStructTypeManager;
    private PartyService partyService;
    private MessageHelper messageHelper;
    private BeanMapper beanMapper = new BeanMapper();
    private TenantHolder tenantHolder;
    private UserClient userClient;

    @RequestMapping("entities")
    public List<PartyEntityDTO> getPartyEntitiesByType(
            @RequestParam("typeId") long typeId) {
        List<PartyEntity> partyEntities = partyEntityManager.findBy(
                "partyType.id", typeId);

        List<PartyEntityDTO> partyEntityDtos = new ArrayList<PartyEntityDTO>();

        for (PartyEntity partyEntity : partyEntities) {
            if (partyEntity.getParentStructs().size() == 1) {
                PartyStruct partyStruct = partyEntity.getParentStructs()
                        .iterator().next();

                if (partyStruct.getParentEntity() == null) {
                    logger.info("skip top entity : {}, {}",
                            partyEntity.getId(), partyEntity.getName());

                    continue;
                }
            }

            PartyEntityDTO partyEntityDto = new PartyEntityDTO();
            partyEntityDto.setId(partyEntity.getId());
            partyEntityDto.setName(partyEntity.getName());
            partyEntityDto.setRef(partyEntity.getRef());
            partyEntityDtos.add(partyEntityDto);
        }

        PartyType partyType = partyTypeManager.get(typeId);

        if (partyType.getType() != 2) {
            return partyEntityDtos;
        }

        // 如果是岗位，按名称去重
        Set<String> names = new HashSet<String>();
        List<PartyEntityDTO> list = new ArrayList<PartyEntityDTO>();

        for (PartyEntityDTO partyEntityDto : partyEntityDtos) {
            if (names.contains(partyEntityDto.getName())) {
                list.add(partyEntityDto);

                continue;
            }

            names.add(partyEntityDto.getName());
        }

        partyEntityDtos.removeAll(list);

        return partyEntityDtos;
    }

    @RequestMapping("tree")
    public List<Map> tree(
            @RequestParam(value = "partyStructTypeId", required = false) Long partyStructTypeId) {
        if (partyStructTypeId == null) {
            partyStructTypeId = this.partyService.getDefaultPartyStructTypeId();
        }

        List<PartyEntity> partyEntities = partyService
                .getTopPartyEntities(partyStructTypeId);

        return generatePartyEntities(partyEntities, partyStructTypeId);
    }

    @RequestMapping("tree-data")
    public List<Map> treeData(@RequestParam("type") String type) {
        PartyStructType partyStructType = partyStructTypeManager.findUniqueBy(
                "type", type);

        return this.tree(partyStructType.getId());
    }

    public List<Map> generatePartyEntities(List<PartyEntity> partyEntities,
            long partyStructTypeId) {
        if (partyEntities == null) {
            return null;
        }

        List<Map> list = new ArrayList<Map>();

        try {
            for (PartyEntity partyEntity : partyEntities) {
                list.add(generatePartyEntity(partyEntity, partyStructTypeId));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return list;
    }

    public Map<String, Object> generatePartyEntity(PartyEntity partyEntity,
            long partyStructTypeId) {
        Map<String, Object> map = new HashMap<String, Object>();

        try {
            map.put("id", partyEntity.getId());
            map.put("name", partyEntity.getName());
            map.put("ref", partyEntity.getRef());

            List<PartyStruct> partyStructs = partyStructManager.find(
                    "from PartyStruct where parentEntity=? order by priority",
                    partyEntity);
            List<PartyEntity> partyEntities = new ArrayList<PartyEntity>();

            for (PartyStruct partyStruct : partyStructs) {
                if (partyStruct.getPartyStructType().getId() == partyStructTypeId) {
                    PartyEntity childPartyEntity = partyStruct.getChildEntity();

                    if (childPartyEntity == null) {
                        logger.info("child party entity is null");

                        continue;
                    }

                    if (childPartyEntity.getPartyType().getType() != 1) {
                        partyEntities.add(childPartyEntity);
                    }
                }
            }

            if (partyEntities.isEmpty()) {
                map.put("open", false);
            } else {
                map.put("open", true);
                map.put("children",
                        generatePartyEntities(partyEntities, partyStructTypeId));
            }

            return map;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return map;
        }
    }

    @RequestMapping("search-user")
    public List<Map<String, String>> searchUser(
            @RequestParam("parentId") Long parentId) {
        String hql = "select child from PartyEntity child join child.parentStructs parent where child.partyType.type=1 and parent.parentEntity.id=?";
        List<PartyEntity> partyEntities = partyEntityManager
                .find(hql, parentId);

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        String tenantId = tenantHolder.getTenantId();

        for (PartyEntity partyEntity : partyEntities) {
            Map<String, String> map = new HashMap<String, String>();
            UserDTO userDto = userClient.findById(partyEntity.getRef(),
                    tenantId);
            map.put("id", userDto.getId());
            map.put("username", userDto.getUsername());
            map.put("displayName", userDto.getDisplayName());
            list.add(map);
        }

        return list;
    }

    // ~ ======================================================================
    @Resource
    public void setPartyTypeManager(PartyTypeManager partyTypeManager) {
        this.partyTypeManager = partyTypeManager;
    }

    @Resource
    public void setPartyStructManager(PartyStructManager partyStructManager) {
        this.partyStructManager = partyStructManager;
    }

    @Resource
    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
    }

    @Resource
    public void setPartyStructTypeManager(
            PartyStructTypeManager partyStructTypeManager) {
        this.partyStructTypeManager = partyStructTypeManager;
    }

    @Resource
    public void setPartyService(PartyService partyService) {
        this.partyService = partyService;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }

    // ~ ==================================================
    public static class PartyTypeDTO {
        private long id;
        private String name;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class PartyEntityDTO {
        private long id;
        private String name;
        private String ref;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRef() {
            return ref;
        }

        public void setRef(String ref) {
            this.ref = ref;
        }
    }
}
