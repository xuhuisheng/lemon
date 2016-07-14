package com.mossle.internal.store.rs;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.activation.DataSource;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mossle.api.store.StoreConnector;
import com.mossle.api.store.StoreDTO;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.store.ByteArrayDataSource;
import com.mossle.core.util.BaseDTO;
import com.mossle.core.util.IoUtils;
import com.mossle.core.util.ServletUtils;

import com.mossle.internal.store.service.StoreService;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("store")
public class StoreResource {
    private static Logger logger = LoggerFactory.getLogger(StoreResource.class);
    private StoreService storeService;
    private StoreConnector storeConnector;
    private TenantHolder tenantHolder;

    @GET
    @Path("getStore")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO getStore(@QueryParam("model") String model,
            @QueryParam("key") String key,
            @QueryParam("tenantId") String tenantId) {
        try {
            BaseDTO result = new BaseDTO();

            StoreDTO storeDto = storeConnector.getStore(model, key, tenantId);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IoUtils.copyStream(storeDto.getDataSource().getInputStream(), baos);

            String base64 = new String(new Base64().encodeBase64(baos
                    .toByteArray()));

            result.setCode(200);
            result.setData(base64);

            return result;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            BaseDTO result = new BaseDTO();
            result.setCode(500);
            result.setMessage(ex.getMessage());

            return result;
        }
    }

    @POST
    @Path("saveStore")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO saveStore(@FormParam("model") String model,
            @FormParam("content") String content,
            @FormParam("fileName") String fileName,
            @FormParam("contentType") String contentType,
            @QueryParam("tenantId") String tenantId) {
        try {
            byte[] bytes = new Base64().decodeBase64(content.getBytes("utf-8"));

            BaseDTO result = new BaseDTO();
            DataSource dataSource = new ByteArrayDataSource(fileName, bytes);
            StoreDTO storeDto = storeService.saveStore(model, dataSource,
                    tenantId);
            result.setCode(200);
            result.setData(storeDto.getKey());

            return result;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            BaseDTO result = new BaseDTO();
            result.setCode(500);
            result.setMessage(ex.getMessage());

            return result;
        }
    }

    @GET
    @Path("view")
    public void view(@QueryParam("model") String model,
            @QueryParam("key") String key, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        StoreDTO storeDto = storeConnector.getStore(model, key, tenantId);

        InputStream is = storeDto.getDataSource().getInputStream();
        ServletUtils.setFileDownloadHeader(request, response, storeDto
                .getDataSource().getName());
        IOUtils.copy(is, response.getOutputStream());
    }

    @Resource
    public void setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;
    }

    @Resource
    public void setStoreService(StoreService storeService) {
        this.storeService = storeService;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
