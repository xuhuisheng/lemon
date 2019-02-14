package com.mossle.party.web.dev;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.party.persistence.domain.PartyEntity;
import com.mossle.party.persistence.domain.PartyStruct;
import com.mossle.party.persistence.manager.PartyEntityManager;
import com.mossle.party.persistence.manager.PartyStructManager;
import com.mossle.party.service.PartyService;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("party/dev")
public class ExportController {
    private PartyEntityManager partyEntityManager;
    private PartyStructManager partyStructManager;
    private PartyService partyService;
    private TenantHolder tenantHolder;

    @RequestMapping("export")
    public String doExport(Model model) {
        List<PartyStruct> partyStructs = this.partyStructManager.getAll();
        model.addAttribute("partyStructs", partyStructs);

        List<PartyEntity> partyEntities = this.partyEntityManager.getAll();
        model.addAttribute("partyEntities", partyEntities);

        return "party/dev/export";
    }

    // ~ ======================================================================
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

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
