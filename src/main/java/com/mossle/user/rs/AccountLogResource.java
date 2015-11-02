package com.mossle.user.rs;

import java.text.SimpleDateFormat;

import java.util.Date;

import javax.annotation.Resource;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.mossle.api.user.AccountLogDTO;

import com.mossle.core.util.BaseDTO;

import com.mossle.user.component.AccountLogQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("account/log")
public class AccountLogResource {
    private static Logger logger = LoggerFactory
            .getLogger(AccountLogResource.class);
    private AccountLogQueue accountLogQueue;

    @POST
    public BaseDTO log(@FormParam("username") String username,
            @FormParam("result") String result,
            @FormParam("reason") String reason,
            @FormParam("application") String application,
            @FormParam("logTime") String logTime,
            @FormParam("client") String client,
            @FormParam("server") String server,
            @FormParam("description") String description) {
        Date date = this.tryToParseTime(logTime);

        AccountLogDTO accountLogDto = new AccountLogDTO();
        accountLogDto.setUsername(username);
        accountLogDto.setResult(result);
        accountLogDto.setReason(reason);
        accountLogDto.setApplication(application);
        accountLogDto.setLogTime(date);
        accountLogDto.setClient(client);
        accountLogDto.setServer(server);
        accountLogDto.setDescription(description);

        BaseDTO baseDto = new BaseDTO();

        try {
            accountLogQueue.add(accountLogDto);
            baseDto.setCode(200);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            baseDto.setCode(500);
            baseDto.setMessage(ex.getMessage());
        }

        return baseDto;
    }

    public Date tryToParseTime(String text) {
        if (text == null) {
            return new Date();
        }

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S")
                    .parse(text);

            return date;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return new Date();
        }
    }

    @Resource
    public void setAccountLogQueue(AccountLogQueue accountLogQueue) {
        this.accountLogQueue = accountLogQueue;
    }
}
