package com.mossle.vehicle.rs;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mossle.vehicle.persistence.domain.VehicleInfo;
import com.mossle.vehicle.persistence.manager.VehicleInfoManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("vehicle")
public class VehicleResource {
    private static Logger logger = LoggerFactory
            .getLogger(VehicleResource.class);
    private VehicleInfoManager vehicleInfoManager;

    @GET
    @Path("list")
    public List<Map<String, Object>> list() throws Exception {
        List<VehicleInfo> vehicleInfos = vehicleInfoManager.findBy("status", 0);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (VehicleInfo vehicleInfo : vehicleInfos) {
            Map<String, Object> map = new HashMap<String, Object>();
            list.add(map);
            map.put("key", vehicleInfo.getId());
            map.put("label",
                    vehicleInfo.getName() + " " + vehicleInfo.getCode());
        }

        return list;
    }

    // ~ ======================================================================
    @Resource
    public void setVehicleInfoManager(VehicleInfoManager vehicleInfoManager) {
        this.vehicleInfoManager = vehicleInfoManager;
    }
}
