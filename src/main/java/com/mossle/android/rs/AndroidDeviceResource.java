package com.mossle.android.rs;

import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mossle.api.user.AccountStatus;
import com.mossle.api.user.AuthenticationHandler;
import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.BaseDTO;

import com.mossle.pim.persistence.domain.PimDevice;
import com.mossle.pim.persistence.manager.PimDeviceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("android")
public class AndroidDeviceResource {
    private static Logger logger = LoggerFactory
            .getLogger(AndroidDeviceResource.class);
    private PimDeviceManager pimDeviceManager;
    private AuthenticationHandler authenticationHandler;
    private UserConnector userConnector;
    private JsonMapper jsonMapper = new JsonMapper();

    @POST
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO login(@FormParam("username") String username,
            @FormParam("password") String password,
            @FormParam("code") String code, @FormParam("type") String type,
            @FormParam("name") String name) {
        logger.info(
                "username : {}, password : {}, code : {}, type : {}, name : {}",
                username, password, code, type, name);

        if (username == null) {
            BaseDTO result = new BaseDTO();
            result.setCode(400);
            result.setMessage("username is null");
            logger.info("username is null");

            return result;
        }

        username = username.toLowerCase();

        try {
            PimDevice pimDevice = pimDeviceManager.findUniqueBy("code", code);

            if ((pimDevice != null)
                    && (!"active".equals(pimDevice.getStatus()))) {
                BaseDTO result = new BaseDTO();
                result.setCode(403);
                result.setMessage("device is " + pimDevice.getStatus());
                logger.info("device is " + pimDevice.getStatus());

                return result;
            }

            String response = authenticationHandler.doAuthenticate(username,
                    password, "normal");

            if (!AccountStatus.SUCCESS.equals(response)) {
                BaseDTO result = new BaseDTO();
                result.setCode(401);
                result.setMessage("authenticate fail, " + response);

                logger.info("authenticate fail, " + response);

                return result;
            }

            if (pimDevice == null) {
                UserDTO userDto = userConnector.findByUsername(username, "1");

                pimDevice = new PimDevice();
                pimDevice.setName(name);
                pimDevice.setCode(code);
                pimDevice.setType(type);
                pimDevice.setStatus("active");
                pimDevice.setCreateTime(new Date());
                pimDevice.setUserId(userDto.getId());

                UUID uuid = UUID.randomUUID();
                pimDevice.setSessionId(uuid.toString());
                pimDeviceManager.save(pimDevice);
            } else {
                UserDTO userDto = userConnector.findByUsername(username, "1");
                UUID uuid = UUID.randomUUID();
                pimDevice.setSessionId(uuid.toString());
                pimDevice.setUserId(userDto.getId());
                pimDeviceManager.save(pimDevice);
            }

            BaseDTO result = new BaseDTO();

            result.setCode(200);
            result.setData(pimDevice.getSessionId());

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
    @Path("checkLogin")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO checkLogin(@HeaderParam("sessionId") String sessionId) {
        logger.info("sessionId : {}", sessionId);

        PimDevice pimDevice = pimDeviceManager.findUniqueBy("sessionId",
                sessionId);
        logger.info("pimDevice : {}", pimDevice);

        if (pimDevice == null) {
            return null;
        }

        BaseDTO result = new BaseDTO();

        result.setCode(200);

        return result;
    }

    @POST
    @Path("logout")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO logout(@HeaderParam("sessionId") String sessionId) {
        PimDevice pimDevice = pimDeviceManager.findUniqueBy("sessionId",
                sessionId);

        if (pimDevice != null) {
            pimDevice.setSessionId(null);
            pimDeviceManager.save(pimDevice);
        }

        BaseDTO result = new BaseDTO();

        result.setCode(200);

        return result;
    }

    // ~ ======================================================================
    @Resource
    public void setPimDeviceManager(PimDeviceManager pimDeviceManager) {
        this.pimDeviceManager = pimDeviceManager;
    }

    @Resource
    public void setAuthenticationHandler(
            AuthenticationHandler authenticationHandler) {
        this.authenticationHandler = authenticationHandler;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }
}
