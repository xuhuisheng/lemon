package com.mossle.asset.rs;

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

import com.mossle.asset.persistence.domain.AssetInfo;
import com.mossle.asset.persistence.manager.AssetInfoManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("asset")
public class AssetResource {
    private static Logger logger = LoggerFactory.getLogger(AssetResource.class);
    private AssetInfoManager assetInfoManager;

    @GET
    @Path("list")
    public List<Map<String, Object>> list() throws Exception {
        List<AssetInfo> assetInfos = assetInfoManager.findBy("status", "0");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (AssetInfo assetInfo : assetInfos) {
            Map<String, Object> map = new HashMap<String, Object>();
            list.add(map);
            map.put("key", assetInfo.getId());
            map.put("label", assetInfo.getName() + " " + assetInfo.getCode());
        }

        return list;
    }

    // ~ ======================================================================
    @Resource
    public void setAssetInfoManager(AssetInfoManager assetInfoManager) {
        this.assetInfoManager = assetInfoManager;
    }
}
