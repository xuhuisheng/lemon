package com.mossle.android.rs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.BaseDTO;

import com.mossle.pim.persistence.domain.PimDevice;
import com.mossle.pim.persistence.domain.PimInfo;
import com.mossle.pim.persistence.manager.PimDeviceManager;
import com.mossle.pim.persistence.manager.PimInfoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("android/pim")
public class AndroidPimResource {
    private static Logger logger = LoggerFactory
            .getLogger(AndroidCmsResource.class);
    private JsonMapper jsonMapper = new JsonMapper();
    private TenantHolder tenantHolder;
    private PimInfoManager pimInfoManager;
    private UserConnector userConnector;
    private PimDeviceManager pimDeviceManager;

    @POST
    @Path("contract")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO contract(@HeaderParam("sessionId") String sessionId)
            throws Exception {
        logger.info("start");

        PimDevice pimDevice = pimDeviceManager.findUniqueBy("sessionId",
                sessionId);

        if (pimDevice == null) {
            BaseDTO result = new BaseDTO();
            result.setCode(401);
            result.setMessage("auth fail");

            return result;
        }

        String userId = pimDevice.getUserId();
        String hql = "from PimInfo";
        List<PimInfo> pimInfos = pimInfoManager.find(hql);

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (PimInfo pimInfo : pimInfos) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", pimInfo.getName());
            map.put("tel", pimInfo.getTel());
            map.put("email", pimInfo.getEmail());
            list.add(map);
        }

        String json = jsonMapper.toJson(list);
        BaseDTO result = new BaseDTO();
        result.setCode(200);
        result.setData(json);
        logger.info("end");

        return result;
    }

    // ~ ======================================================================
    @Resource
    public void setPimInfoManager(PimInfoManager pimInfoManager) {
        this.pimInfoManager = pimInfoManager;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    @Resource
    public void setPimDeviceManager(PimDeviceManager pimDeviceManager) {
        this.pimDeviceManager = pimDeviceManager;
    }
}
