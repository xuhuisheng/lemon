package com.mossle.cms.rs;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.BaseDTO;
import com.mossle.core.util.StringUtils;

import com.mossle.ext.store.StoreConnector;
import com.mossle.ext.store.StoreDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("cms")
public class CmsResource {
    private static Logger logger = LoggerFactory.getLogger(CmsResource.class);
    private StoreConnector storeConnector;

    @GET
    @Path("image")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream image(@QueryParam("key") String key) throws Exception {
        StoreDTO storeDto = storeConnector.get("cms/html/r/image", key);

        return storeDto.getResource().getInputStream();
    }

    @GET
    @Path("video")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream video(@QueryParam("key") String key) throws Exception {
        StoreDTO storeDto = storeConnector.get("cms/html/r/video", key);

        return storeDto.getResource().getInputStream();
    }

    @GET
    @Path("audio")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream audio(@QueryParam("key") String key) throws Exception {
        StoreDTO storeDto = storeConnector.get("cms/html/r/audio", key);

        return storeDto.getResource().getInputStream();
    }

    @GET
    @Path("pdf")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream pdf(@QueryParam("key") String key) throws Exception {
        StoreDTO storeDto = storeConnector.get("cms/html/r/pdf", key);

        return storeDto.getResource().getInputStream();
    }

    @GET
    @Path("attachment")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream zip(@QueryParam("key") String key) throws Exception {
        StoreDTO storeDto = storeConnector.get("cms/html/r/attachment", key);

        return storeDto.getResource().getInputStream();
    }

    @Resource
    public void setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;
    }
}
