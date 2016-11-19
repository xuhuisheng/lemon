package com.mossle.user.rs;

import javax.annotation.Resource;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import com.mossle.api.user.AccountAliasConverter;
import com.mossle.api.user.AccountStatusHelper;

import com.mossle.core.util.BaseDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

@Component
@Path("account/status")
public class AccountStatusResource {
    private static Logger logger = LoggerFactory
            .getLogger(AccountStatusResource.class);
    private AccountStatusHelper accountStatusHelper;
    private AccountAliasConverter accountAliasConverter;

    @GET
    @Path("islocked")
    public BaseDTO isLocked(@QueryParam("username") String username,
            @QueryParam("application") String application) {
        BaseDTO baseDto = new BaseDTO();

        try {
            baseDto.setCode(200);
            baseDto.setData(accountStatusHelper.isLocked(username, application));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            baseDto.setCode(500);
            baseDto.setMessage(ex.getMessage());
        }

        return baseDto;
    }

    @GET
    @Path("accountstatus")
    public BaseDTO accountStatus(@QueryParam("username") String username,
            @QueryParam("application") String application) {
        BaseDTO baseDto = new BaseDTO();

        try {
            baseDto.setCode(200);
            baseDto.setData(accountStatusHelper.getAccountStatus(username,
                    application));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            baseDto.setCode(500);
            baseDto.setMessage(ex.getMessage());
        }

        return baseDto;
    }

    @POST
    @Path("accountalias")
    public BaseDTO accountStatus(@FormParam("username") String username) {
        BaseDTO baseDto = new BaseDTO();

        try {
            baseDto.setCode(200);
            baseDto.setData(accountAliasConverter.convertAlias(username));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            baseDto.setCode(500);
            baseDto.setMessage(ex.getMessage());
        }

        return baseDto;
    }

    @Resource
    public void setAccountStatusHelper(AccountStatusHelper accountStatusHelper) {
        this.accountStatusHelper = accountStatusHelper;
    }

    @Autowired(required = false)
    public void setAccountAliasConverter(
            AccountAliasConverter accountAliasConverter) {
        this.accountAliasConverter = accountAliasConverter;
    }
}
