package com.mossle.cms.rs;

import java.io.InputStream;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mossle.api.store.StoreConnector;
import com.mossle.api.store.StoreDTO;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.util.ServletUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("cms")
public class CmsResource {
    private static Logger logger = LoggerFactory.getLogger(CmsResource.class);
    private StoreConnector storeConnector;
    private TenantHolder tenantHolder;

    @GET
    @Path("image")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream image(@QueryParam("key") String key) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        StoreDTO storeDto = storeConnector.getStore("cms/html/r/image", key,
                tenantId);

        return storeDto.getDataSource().getInputStream();
    }

    @GET
    @Path("video")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream video(@QueryParam("key") String key) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        StoreDTO storeDto = storeConnector.getStore("cms/html/r/video", key,
                tenantId);

        return storeDto.getDataSource().getInputStream();
    }

    @GET
    @Path("audio")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream audio(@QueryParam("key") String key) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        StoreDTO storeDto = storeConnector.getStore("cms/html/r/audio", key,
                tenantId);

        return storeDto.getDataSource().getInputStream();
    }

    @GET
    @Path("pdf")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream pdf(@QueryParam("key") String key) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        StoreDTO storeDto = storeConnector.getStore("cms/html/r/pdf", key,
                tenantId);

        return storeDto.getDataSource().getInputStream();
    }

    @GET
    @Path("attachment")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream zip(@QueryParam("key") String key) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        StoreDTO storeDto = storeConnector.getStore("cms/html/r/attachment",
                key, tenantId);

        return storeDto.getDataSource().getInputStream();
    }

    @GET
    @Path("attachments")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream attachments(@QueryParam("key") String key,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        StoreDTO storeDto = storeConnector.getStore("cms/html/r/attachments",
                key, tenantId);
        ServletUtils.setFileDownloadHeader(request, response,
                storeDto.getDisplayName());

        return storeDto.getDataSource().getInputStream();
    }

    @Resource
    public void setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
