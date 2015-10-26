package com.mossle.party.rs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.util.JSONPObject;

import com.mossle.core.page.Page;

import com.mossle.party.persistence.domain.PartyEntity;
import com.mossle.party.persistence.manager.PartyEntityManager;

import org.springframework.stereotype.Component;

@Component
@Path("partyjsonp")
public class PartyJsonpResource {
    public static final int DFAULT_PAGE_SIZE = 10;
    private PartyEntityManager partyEntityManager;

    /**
     * TODO: replace JSONWithPadding to JSONPObject
     */
    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONPObject getPartyEntitiesByType(
            @QueryParam("callback") String callback,
            @QueryParam("typeId") long typeId, @QueryParam("q") String q) {
        String hql = "from PartyEntity where partyType.id=? and name like ? order by name";
        Page page = partyEntityManager.pagedQuery(hql, 1, DFAULT_PAGE_SIZE,
                typeId, q.replace("_", "\\_") + "%");
        List<PartyEntity> partyEntities = (List<PartyEntity>) page.getResult();

        List<PartyEntityDTO> partyEntityDtos = new ArrayList<PartyEntityDTO>();

        for (PartyEntity partyEntity : partyEntities) {
            PartyEntityDTO partyEntityDto = new PartyEntityDTO();
            partyEntityDto.setId(partyEntity.getId());
            partyEntityDto.setName(partyEntity.getName());

            partyEntityDtos.add(partyEntityDto);
        }

        return new JSONPObject(callback, partyEntityDtos);
    }

    @Resource
    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
    }

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
        private String reference;

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

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }
    }
}
