package com.mossle.internal.sendmail.client;

import java.util.Map;
import java.util.concurrent.Future;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.InvocationCallback;

import com.mossle.core.util.BaseDTO;

@Path("sendmail/rs")
public interface RestSendmailClient {
    @POST
    @Path("send")
    Future<BaseDTO> sendMail(@FormParam("to") String to,
            @FormParam("templateCode") String templateCode,
            @FormParam("data") Map<String, Object> data,
            InvocationCallback<BaseDTO> callback);
}
