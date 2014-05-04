package com.mossle.party.rs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mossle.party.domain.PartyEntity;
import com.mossle.party.domain.PartyStruct;
import com.mossle.party.domain.PartyType;
import com.mossle.party.manager.PartyEntityManager;
import com.mossle.party.manager.PartyStructManager;
import com.mossle.party.manager.PartyTypeManager;
import com.mossle.party.service.PartyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("party")
public class PartyResource {
    private static Logger logger = LoggerFactory.getLogger(PartyResource.class);
    private PartyTypeManager partyTypeManager;
    private PartyEntityManager partyEntityManager;
    private PartyStructManager partyStructManager;
    private PartyService partyService;

    @GET
    @Path("types")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PartyTypeDTO> getAllPartyTypes() {
        List<PartyType> partyTypes = partyTypeManager.getAll();

        List<PartyTypeDTO> partyTypeDtos = new ArrayList<PartyTypeDTO>();

        for (PartyType partyType : partyTypes) {
            PartyTypeDTO partyTypeDto = new PartyTypeDTO();
            partyTypeDto.setId(partyType.getId());
            partyTypeDto.setName(partyType.getName());
            partyTypeDtos.add(partyTypeDto);
        }

        return partyTypeDtos;
    }

    @GET
    @Path("entities")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PartyEntityDTO> getPartyEntitiesByType(
            @QueryParam("typeId") long typeId) {
        List<PartyEntity> partyEntities = partyEntityManager.findBy(
                "partyType.id", typeId);

        List<PartyEntityDTO> partyEntityDtos = new ArrayList<PartyEntityDTO>();

        for (PartyEntity partyEntity : partyEntities) {
            PartyEntityDTO partyEntityDto = new PartyEntityDTO();
            partyEntityDto.setId(partyEntity.getId());
            partyEntityDto.setName(partyEntity.getName());
            partyEntityDto.setRef(partyEntity.getRef());
            partyEntityDtos.add(partyEntityDto);
        }

        return partyEntityDtos;
    }

    @POST
    @Path("tree")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Map> tree(
            @QueryParam("partyStructTypeId") long partyStructTypeId) {
        List<PartyEntity> partyEntities = partyService
                .getTopPartyEntities(partyStructTypeId);

        return generatePartyEntities(partyEntities, partyStructTypeId);
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

                    if (childPartyEntity.getPartyType().getType() == 0) {
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

    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> search(@QueryParam("name") String name,
            @QueryParam("partyTypeId") long partyTypeId) {
        List<String> names = partyEntityManager
                .find("select name from PartyEntity where name like ? and partyType.id=?",
                        "%" + name + "%", partyTypeId);

        return names;
    }

    // ~ ==================================================
    @Resource
    public void setPartyTypeManager(PartyTypeManager partyTypeManager) {
        this.partyTypeManager = partyTypeManager;
    }

    @Resource
    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
    }

    @Resource
    public void setPartyStructManager(PartyStructManager partyStructManager) {
        this.partyStructManager = partyStructManager;
    }

    @Resource
    public void setPartyService(PartyService partyService) {
        this.partyService = partyService;
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
